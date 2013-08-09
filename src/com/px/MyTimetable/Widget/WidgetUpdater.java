package com.px.MyTimetable.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class WidgetUpdater
{
   public static void updateWidget(Context context)
   {      
      Intent widgetIntent = new Intent(context, NextLectureWidgetProvider.class);
      widgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
      // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
      // since it seems the onUpdate() is only fired on that:      
      widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextLectureWidgetProvider.class)));
      context.sendBroadcast(widgetIntent);      
   }
}
