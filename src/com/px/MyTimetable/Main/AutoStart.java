package com.px.MyTimetable.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver
{
   UpdateTimetable updateTimetable = new UpdateTimetable();
   @Override
   public void onReceive(Context context, Intent intent)
   {   
//       if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
//       {
//           updateTimetable.SetAlarm(context);
//       }
   }   
}
