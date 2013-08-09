package com.px.MyTimetable.Service;

import java.util.Calendar;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.TimetablePage.DaySelector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ServiceStart extends BroadcastReceiver
{
   @Override
   public void onReceive(Context context, Intent intent){
      
      Lecture nextLecture = ((TimetableAccess)context.getApplicationContext()).getTimetable().getNextLecture(Calendar.getInstance());
      NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
      Intent intent1 = new Intent(context, DaySelector.class);
      PendingIntent pending = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
      
      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(R.drawable.ic_launcher)
      .setTicker("Next Lecture")
      .setAutoCancel(true)
      .setContentTitle(nextLecture.getSubject().getName())
      .setWhen(System.currentTimeMillis())
      .setDefaults(Notification.DEFAULT_ALL)
      .setContentIntent(pending)
      .setContentText(nextLecture.getPlace().getRoom());
      
      nManager.notify(0,mBuilder.build());
      
//      Intent service = new Intent(context, NotificationService.class);
//      context.startService(service);
   }
}
