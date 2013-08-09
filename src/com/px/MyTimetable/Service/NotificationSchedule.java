package com.px.MyTimetable.Service;

import java.util.Calendar;

import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Main.TimetableAccess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotificationSchedule extends BroadcastReceiver
{
   boolean network_available = false;
   ConnectionCheck check;
   
   
   public void onReceive(Context context, Intent intent){
      //TODO Log.d are for debugging using LogCat
      
      SmartNotification lUpdate = new SmartNotification();
      
      //get next lecture info
      Lecture nextLecture = ((TimetableAccess)context.getApplicationContext()).getTimetable().getNextLecture(Calendar.getInstance());
      Log.d("receiver 2","started schedule");
      AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Intent intent_2 = new Intent(context, ServiceStart.class);
      PendingIntent pIntent_2 = PendingIntent.getBroadcast(context, 0, intent_2, PendingIntent.FLAG_CANCEL_CURRENT);
      
      // Smart notifications
      // Can only set in the hour before the lecture

      boolean smartReminder = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("smart_reminder", false);
      
      Calendar lecture = nextLecture.getCalendar();
      Calendar cal = Calendar.getInstance();
      Calendar now = Calendar.getInstance();
      // Get the day of the month for the lecture and for this day
      int lecture_day = lecture.get(Calendar.DAY_OF_MONTH);
      Log.d("LectureDay","is "+ lecture_day);
      int today = now.get(Calendar.DAY_OF_MONTH);
      Log.d("Today","is "+today);
      
      int lecture_timeslot = lecture.get(Calendar.HOUR_OF_DAY);
      int today_timeslot = now.get(Calendar.HOUR_OF_DAY);
      if (lecture_day == today && (lecture_timeslot == (today_timeslot + 1)))
      {
         check = new ConnectionCheck(context);
         network_available = check.isNetworkAvailable();
         if(smartReminder && network_available ){
            Log.d("Network","available,location based settings");
            lUpdate.run(context, "BS1 1XA");
         }else{
            Log.d("Network","not available,default settings");
            int timeSlot = nextLecture.getTimeSlot();
            Log.d("TimeSlot",""+timeSlot);
            cal.set(Calendar.HOUR_OF_DAY, timeSlot-1);
            cal.set(Calendar.MINUTE, 50);
            service.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent_2);
         }      
      }
   }

}
