package com.px.MyTimetable.Service;

import java.util.Calendar;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.TimetablePage.DaySelector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service
{
   NotificationManager nManager;;
   
   public void onCreate(){  
      
      super.onCreate();
   }
   
   public int onStartCommand(Intent intent, int flag, int startID){
     //TODO do timetable checking and notify
      sendNotification();
      
      return Service.START_STICKY;
   }
   
   void sendNotification(){
      Lecture nextLecture = ((TimetableAccess)getApplication()).getTimetable().getNextLecture(Calendar.getInstance());
      nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      Intent intent = new Intent(this, DaySelector.class);
      PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
      
      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.ic_launcher)
      .setTicker("Next Lecture")
      .setAutoCancel(true)
      .setContentTitle(nextLecture.getSubject().getName())
      .setWhen(System.currentTimeMillis())
      .setDefaults(Notification.DEFAULT_ALL)
      .setContentIntent(pending)
      .setContentText(nextLecture.getPlace().getRoom());
      
      
      
      nManager.notify(0,mBuilder.build());
   }

   @Override
   public IBinder onBind(Intent intent)
   {
      // TODO Auto-generated method stub
      return null;
   }
}

