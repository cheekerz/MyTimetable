package com.px.MyTimetable.Main;

import java.util.Calendar;

import android.app.Application;

import com.px.MyTimetable.Data.CalendarProvider;
import com.px.MyTimetable.Entities.Timetable;

public class TimetableAccess extends Application
{
   private Timetable timetable = null;
   private Calendar startCal = null;
   private Calendar endCal = null;
   
   /*
    * Called when application is first started
    * (non-Javadoc)
    * @see android.app.Application#onCreate()
    */
   @Override
   public void onCreate()
   {
      super.onCreate();
      // Get timetable data
      this.timetable = this.getTimetable();
      new UpdateTimetable().SetAlarm(this);
   }
   
   /**
    * Retrieve timetable, create it if it is null
    * @return current timetable
    */
   public Timetable getTimetable()
   {
      if (timetable == null)
      {
         this.timetable = new Timetable(getApplicationContext(), this.getStartCal(), this.getEndCal());
         CalendarProvider.merge(getContentResolver(), getApplicationContext());
      }
      
      return this.timetable;
   }
   
   /**
    * Write timetable to calendars and set it to null to force update on access
    */
   public void resetTimetable()
   {
      this.timetable = null;
   }
   
   /**
    * Gets start date, works it out if unknown
    * @return start Calendar
    */
   public Calendar getStartCal()
   {
      if (this.startCal == null)
      {
         setStartEndCalendars();
      }
      
      return this.startCal;
   }
   
   /**
    * Gets end date, works it out if unknown
    * @return end Calendar
    */
   public Calendar getEndCal()
   {
      if (this.endCal == null)
      {
         setStartEndCalendars();
      }
      
      return this.endCal;
   }
   
   /**
    * set Start and end dates for timetable
    */
   private void setStartEndCalendars()
   {
      Calendar now = Calendar.getInstance();
      Calendar firstMonday = findFirstMondaySeptember((Calendar)now.clone());
      
      // Check if the first Monday in September is the end date or start date of timetable
      if (now.before(firstMonday))
      {
         // Monday is end date, find first Monday of September of previous year
         this.endCal = (Calendar) firstMonday.clone();
         now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
         this.startCal = findFirstMondaySeptember(now);
      }
      else
      {
         // Monday is start date, find first Monday of September of next year
         this.startCal = (Calendar) firstMonday.clone();
         now.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
         this.endCal = findFirstMondaySeptember(now);
      }      
      
      zeroCalendar(this.endCal);
      zeroCalendar(this.startCal);
   }
   
   /**
    * Set Calendar to exactly the current day it is set at
    * @param cal Calendar to set
    */
   private static void zeroCalendar (Calendar cal)
   {      
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR, 0);
   }

   /**
    * Given a calendar find the first Monday in September for that calendars year
    * @param cal Calendar for year reference
    * @return Calendar set to the first Monday in September of original calendars year
    */
   private static Calendar findFirstMondaySeptember (Calendar cal)
   {
      // Starting from the first of September go through until a Monday is encountered
      // must be within at least 7 days
      for (int day = 1; day < 8; day++)
      {
         cal.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, day);      
         if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
         {
            return cal;
         }
      }      
      return null;
   }
}
