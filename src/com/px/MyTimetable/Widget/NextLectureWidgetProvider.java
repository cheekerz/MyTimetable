package com.px.MyTimetable.Widget;

import java.util.Calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.TimetablePage.DayFragment;
import com.px.MyTimetable.TimetablePage.LecturePage;

public class NextLectureWidgetProvider extends AppWidgetProvider
{
   private static String WIDGET_ATTENDANCE_BUTTON = "ATTENDANCE_BUTTON";
   
   @Override
   public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
   {
      super.onUpdate(context, appWidgetManager, appWidgetIds);
     final int N = appWidgetIds.length;
     
     // Perform this loop procedure for each App Widget that belongs to this provider
     for (int i=0; i<N; i++) {
         int appWidgetId = appWidgetIds[i];                  

         RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
         // Display next lecture
         Lecture nextLecture = ((TimetableAccess)context.getApplicationContext()).getTimetable().getNextLecture(Calendar.getInstance());
         
         if (nextLecture.getCalendar().get(Calendar.YEAR) != nextLecture.getCalendar().getActualMaximum(Calendar.YEAR))
         {
            views.setTextViewText(R.id.widgetNextLectureTitle, context.getString(R.string.NextLectureOn) + DayFragment.dayToString(nextLecture.getCalendar(), Calendar.LONG));
            views.setTextViewText(R.id.timeslot_time, Timetable.timeSlotToTime(nextLecture.getTimeSlot()));
            views.setTextViewText(R.id.timeslot_subject, nextLecture.getSubject().getName());
            views.setTextViewText(R.id.timeslot_location, nextLecture.getPlace().getShortAddress());
         }
         else 
         {
            views.setTextViewText(R.id.nextLectureTitle, context.getString(R.string.EndOfLectures));
            views.setViewVisibility((R.id.nextLectureTimeSlot), View.GONE);
         }

         views.setInt(R.id.attendanceButton, "setBackgroundColor", nextLecture.getAttendanceColor());
         Intent lectureIntent = new Intent(context, LecturePage.class);
         Intent attendanceIntent = new Intent(context, NextLectureWidgetProvider.class);
         attendanceIntent.setAction(WIDGET_ATTENDANCE_BUTTON);
         attendanceIntent.putExtra("LecturePosition", ((TimetableAccess)context.getApplicationContext()).getTimetable().getLecturePositionFromID(nextLecture.getID()));
         Calendar timeSlotCal = (Calendar) nextLecture.getCalendar().clone();
         lectureIntent.putExtra("Calendar", timeSlotCal);               
         PendingIntent lecturePendingIntent = PendingIntent.getActivity(context, 0, lectureIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
         views.setOnClickPendingIntent(R.id.widgetNextLectureTimeSlot, lecturePendingIntent);
         PendingIntent attendancePendingIntent = PendingIntent.getBroadcast(context, 0, attendanceIntent, 0);
         views.setOnClickPendingIntent(R.id.attendanceButton, attendancePendingIntent);
         
         // Tell the AppWidgetManager to perform an update on the current App Widget
         appWidgetManager.updateAppWidget(appWidgetId, views);
     }
   }
   
   @Override
   public void onReceive(Context context, Intent intent)
   {
      super.onReceive(context, intent);
      
      if (intent.getAction().equals(WIDGET_ATTENDANCE_BUTTON))
      {
         int lecturePosition = intent.getIntExtra("LecturePosition", -1);
         Lecture l = ((TimetableAccess)context.getApplicationContext()).getTimetable().getLectureFromPosition(lecturePosition);
         
         if (l != null)
         {
            l.changeAttendance(((TimetableAccess)context.getApplicationContext()).getTimetable().getWritableDatabase());
            WidgetUpdater.updateWidget(context);
         }
      }
   }
}
