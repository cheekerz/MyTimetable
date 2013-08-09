package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.NotificationCompat;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Place;
import com.px.MyTimetable.Entities.Subject;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;

public class CalendarProvider
{   

   private static final String CALNAME = "'Bristol University Personal Timetable'";
   private static final int MAX_NUM_LOCATIONS = 3;

   /**
    * Returns a list of lectures extracted from the events found in the calendar
    * @return List of lectures objects which contain information about a set of lectures
    */

   public static void merge(ContentResolver contentResolver, Context context)
   {
      // TODO get rid of notifications when first retrieving from db
      Timetable timetable = ((TimetableAccess)context).getTimetable();
      boolean[] found = new boolean[timetable.getLectureCount() + 1];
      Arrays.fill(found, false);
      
      String[] projection = 
            new String[]{
                  CalendarContract.Events.TITLE,
                  CalendarContract.Events.DESCRIPTION,
                  CalendarContract.Events.EVENT_LOCATION,
                  CalendarContract.Events.DTSTART,
                  CalendarContract.Events.RDATE,
                  CalendarContract.Events.DURATION,
                  CalendarContract.Events.DTEND,
                  CalendarContract.Events._ID};

      Cursor calCursor = 
            contentResolver.query(CalendarContract.Events.CONTENT_URI, 
                        projection, 
                        Calendars.CALENDAR_DISPLAY_NAME + "=" + CALNAME,
                        null, null);

      if (calCursor.moveToFirst())
      {
         do
         {
            found[parseEvent(context, timetable, calCursor.getString(0), calCursor.getString(1), calCursor.getString(2), calCursor.getLong(3), calCursor.getString(4), calCursor.getString(5), calCursor.getLong(6), calCursor.getLong(7), found.length - 1)] = true;
         }
         while (calCursor.moveToNext());
      }

      for (int i = 0; i < found.length - 1; i++)
      {
         if (!found[i] && timetable.getLectureFromPosition(i).isOriginal())
         {
            Intent intent = new Intent(context, CalendarWarning.class);
            intent.putExtra("LecturePosition", i);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("Purpose", 0);
            makeNotification(context, intent, i, context.getString(R.string.NotificationDeleteTitle));
         }
      }
      
      calCursor.close();
   }
   
   private static void makeNotification(Context context, Intent intent, int id, String title)
   {
      // Sets the Activity to start in a new, empty task
      // Creates the PendingIntent
      PendingIntent notifyIntent =
              PendingIntent.getActivity(
              context,
              0,
              intent,
              PendingIntent.FLAG_UPDATE_CURRENT
      );    
      
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setSmallIcon(R.drawable.ic_launcher)
      .setTicker(context.getString(R.string.NotificationUpdateTicker))
      .setAutoCancel(true)
      .setContentTitle(title)
      .setWhen(System.currentTimeMillis())
      .setDefaults(Notification.DEFAULT_ALL)              
      .setContentIntent(notifyIntent)
      .setContentText(context.getString(R.string.NotificationContent));
      // Notifications are issued by sending them to the
      // NotificationManager system service.
      NotificationManager mNotificationManager =
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      // Builds an anonymous Notification object from the builder, and
      // passes it to the NotificationManager
      mNotificationManager.notify(id, builder.build());
   }
   
   public static int parseEvent(Context context, Timetable timetable, String title, String description, String location, long start, String rDate, String lDuration, long end, long eventId, int lastIndex)
   {
      String[] descriptionStrings = description.split("\n");
      String[] locationStrings = location.split(",");

      String name = descriptionStrings[0]; // Assume will always be the title of the lecture
      String type = descriptionStrings[1]; // Same for title
      String code = title.split("/")[0];
      String depCode = code.substring(0,4);
      String lecturer = "";
      String room = "";
      String building = "";
      String postcode = locationStrings[locationStrings.length - 1].trim();
      
      int i = 0, j = 0;
      
      if(containsDepCode(descriptionStrings[2], depCode))
      {
         i = 1;
      }
      
      // Keep reading into location until weeks is read
      String[] lLocation = new String[MAX_NUM_LOCATIONS];
      String[] lLocationDescription = new String[MAX_NUM_LOCATIONS];
      
      while(j < 3 && (2+i) < descriptionStrings.length && !((descriptionStrings[2+i].toLowerCase(Locale.ENGLISH)).contains("weeks")))
      {
         // Short often abbreviated description of lecture building ie MVB 2.11 LPC 
         lLocation[j] = descriptionStrings[2+i]; 
         // Detailed description of location of lecture ie Merchant Venturer's Building: 2.11 Linux Computer Lab
         lLocationDescription[j] = descriptionStrings[3+i]; 
         j++;
         i = i+3;
      }
      
      if(lLocationDescription[0] != null)
      {         
         String splitDescription[] = lLocationDescription[0].split(":");
         if (splitDescription.length > 0)
         {
            building = splitDescription[0].trim();
         }
         
         if (splitDescription.length > 1)
         {
            room = splitDescription[1].trim();
         }
      }
      
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(start);
      List<Integer> weeks = new ArrayList<Integer>();
      
      if (rDate == null)
      {
         weeks.add(cal.get(Calendar.WEEK_OF_YEAR));
      }
      else
      {
         String[] rDates = rDate.split(";")[1].split(",");
         
         for (String date : rDates)
         {
            cal.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6, 8)));
            weeks.add(cal.get(Calendar.WEEK_OF_YEAR));
         }         
      }
      
      // Duration is only defined in cal if the event is recurring
      // For non-recurring events DTEND is given so can work out duration from this
      int duration = 1;
      
      if (lDuration == null)
      {
         duration = (int) ((end - start) / 3600000);
      }
      else
      {
         duration = Integer.parseInt(lDuration.substring(1,  lDuration.length() - 1))/3600;
      }

      int subjectIndex = timetable.getSubjectIndex(code);
      Subject subject;
      
      if (subjectIndex >= 0)
      {
         subject = timetable.getSubject(subjectIndex);
      }
      else
      {
         subject = timetable.addSubject(code, name, depCode);
      }
      
      int placeIndex = timetable.getPlaceIndex(room, building, postcode);
      Place place;
      
      if (placeIndex >= 0)
      {
         place = timetable.getPlace(placeIndex);
      }
      else
      {            
         place = timetable.addPlace(lLocation[0], room, building, postcode);
      }
      
      int lecturePosition = timetable.getLecturePositionFromOID(eventId);
      Intent intent = new Intent(context, CalendarWarning.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      intent.putExtra("LecturePosition", lecturePosition);
      
      if (lecturePosition < 0)
      {
         timetable.addLecture(eventId, subject, place, cal, duration, weeks, subject, place, cal, duration, weeks, type, lecturer, 0);
         intent.putExtra("LecturePosition", timetable.getLecturePositionFromOID(eventId));
         intent.putExtra("Purpose", 1);
         makeNotification(context, intent, lecturePosition, context.getString(R.string.NotificationAddTitle));
      }
      else
      {
         SQLiteDatabase db = timetable.getWritableDatabase();
         Lecture lecture = timetable.getLectureFromPosition(lecturePosition);
         
         if (!lecture.getoSubject().getUnitCode().equals(code))
         {
            intent.putExtra("Subject", code);
            lecture.setoSubject(db, subject);
         }            
         if (lecture.getoPlace().getId() != place.getId())
         {
            intent.putExtra("Place", place.getId());
            lecture.setoPlace(db, place);
         }
         if (lecture.getoCal().get(Calendar.HOUR_OF_DAY) != cal.get(Calendar.HOUR_OF_DAY) && lecture.getoCal().get(Calendar.DAY_OF_WEEK) != cal.get(Calendar.DAY_OF_WEEK))
         {
            intent.putExtra("Calendar", cal);
            lecture.setoCal(db, cal);
         }
         if (lecture.getoDuration() != duration)
         {
            intent.putExtra("Duration", duration);
            lecture.setoDuration(db, duration);
         }
         if (!lecture.getoWeeks().equals(weeks))
         {
            intent.putExtra("Weeks", weeks.toArray());
            lecture.setoWeeks(db, weeks);
         }
         
         intent.putExtra("Purpose", 2);
         
         if (intent.hasExtra("Subject") || intent.hasExtra("Place") || intent.hasExtra("Calendar") || intent.hasExtra("Duration") || intent.hasExtra("Weeks"))
         {
            makeNotification(context, intent, lecturePosition, context.getString(R.string.NotificationUpdateTitle));
         }
         db.close();
         return lecturePosition;
      }
      
      return lastIndex;
   }
   
   /**
    *  Given a string checks whether the string contains a department code
    * @param line String to check
    * @param depCode String to find
    * @return True if the given string contains the department code, false otherwise
    */
   private static boolean containsDepCode(String line, String depCode)
   {
      if((line.toLowerCase(Locale.ENGLISH)).contains((depCode.toLowerCase(Locale.ENGLISH))))
      {
         return true;
      }
      return false;
   }
}
