package com.px.MyTimetable.Main;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Service.NotificationSchedule;
import com.px.MyTimetable.Widget.WidgetUpdater;

public class UpdateTimetable extends BroadcastReceiver 
{    
   private final long HOUR = 1000 *60 * 60;
   
   @Override
   public void onReceive(Context context, Intent intent) 
   {   
      PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
      wl.acquire();
      boolean reminder = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("reminder", true);
      
      try
      {
         ((TimetableAccess)context.getApplicationContext()).resetTimetable();
         Calendar cal = Calendar.getInstance();
         Intent notification = new Intent(context, NotificationSchedule.class);
         
         if (((TimetableAccess)context.getApplicationContext()).getTimetable().getLecture(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY)) == null)
         {
            if (((TimetableAccess)context.getApplicationContext()).getTimetable().getLecture(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY)-1) != null)
            {
               setVolume(context, AudioManager.RINGER_MODE_NORMAL);
            }
         }
         else
         {
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString("volume", "Off");
            String[] values = context.getResources().getStringArray(R.array.volume_options);
            
            if (values[1].equals(value))
            {
               setVolume(context, AudioManager.RINGER_MODE_SILENT);
            }
            else if(values[2].equals(value))
            {
               setVolume(context, AudioManager.RINGER_MODE_VIBRATE);
            }
            
            WidgetUpdater.updateWidget(context);
         }
         if (reminder)
         {
            context.sendBroadcast(notification);
         }
      }
      finally
      {
         wl.release();
      }
   }
   
   private static void setVolume(Context c, int ringerMode)
   {
      AudioManager manage = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
      manage.setRingerMode(ringerMode);
   }
   
   public void SetAlarm(Context context)
   {
       CancelAlarm(context);
       AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
       Intent i = new Intent(context, UpdateTimetable.class);
       PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
       Calendar cal = Calendar.getInstance();
       cal.add(Calendar.HOUR, 1);
       cal.set(Calendar.MILLISECOND, 0);
       cal.set(Calendar.SECOND, 0);
       cal.set(Calendar.MINUTE, 0);
       am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), HOUR, pi); // Millisec * Second * Minute
   }

   public void CancelAlarm(Context context)
   {
      Intent intent = new Intent(context, UpdateTimetable.class);
      PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmManager.cancel(sender);
   }
}