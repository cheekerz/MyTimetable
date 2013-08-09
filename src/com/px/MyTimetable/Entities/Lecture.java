package com.px.MyTimetable.Entities;

import java.util.Calendar;
import java.util.List;

import com.px.MyTimetable.Data.LectureTable;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

public class Lecture
{
   private long id;
   private long oId;
   
   private Subject oSubject;
   private Place oPlace;
   private Calendar oCal;
   private int oDuration;
   private List<Integer> oWeeks;
   
   private Subject subject;
   private Place place;
   private Calendar cal;
   private int duration;
   private List<Integer> weeks;
   
   private String type;
   private String lecturer;
   
   private int attendance;
   
   public static int ATTENDANCE_NULL = 0;
   public static int ATTENDANCE_TRUE = 1;
   public static int ATTENDANCE_FALSE = 2;

   /**
    * Initialises a new instance of the Lecture class
    * 
    * @param place
    *           Place where the lecture is
    * @param subject
    *           Subject of the lecture
    * @param lecturer
    *           Who will be lecturing
    */
   public Lecture(long id, long oId, Subject oSubject, Place oPlace,
         Calendar oCal, int oDuration, List<Integer> oWeeks, Subject subject,
         Place place, Calendar cal, int duration, List<Integer> weeks,
         String type, String lecturer, int attendance)
   {
      this.id = id;
      this.oId = oId;
      this.oSubject = oSubject;
      this.oPlace = oPlace;
      this.oCal = oCal;
      this.oDuration = oDuration;
      this.oWeeks = oWeeks;
      this.subject = subject;
      this.place = place;
      this.cal = cal;
      this.duration = duration;
      this.weeks = weeks;
      this.type = type;
      this.lecturer = lecturer;
      this.attendance = attendance;
   }

   public boolean isInstance(Subject subject, Place place, Calendar cal,
         int duration, String type, String lecturer)
   {
      return this.subject == subject
            && this.place == place
            && this.cal.get(Calendar.HOUR_OF_DAY) == cal
                  .get(Calendar.HOUR_OF_DAY)
            && this.cal.get(Calendar.DAY_OF_WEEK) == cal
                  .get(Calendar.DAY_OF_WEEK) && this.duration == duration
            && this.type.equals(type) && this.lecturer.equals(lecturer);
   }

   public boolean isOriginal()
   {
      return this.oId != 0;
   }

   public boolean subjectIsOriginal()
   {
      return this.subject.equals(this.oSubject);
   }

   public boolean placeIsOriginal()
   {
      return this.place.equals(this.oPlace);
   }

   public boolean weeksIsOriginal()
   {
      return this.weeks.equals(this.oWeeks);
   }

   public boolean timingsIsOriginal()
   {
      return this.duration == this.oDuration
            && this.cal.get(Calendar.HOUR_OF_DAY) == this.oCal
                  .get(Calendar.HOUR_OF_DAY)
            && this.cal.get(Calendar.DAY_OF_WEEK) == this.oCal
                  .get(Calendar.DAY_OF_WEEK);
   }

   public void restoreLecture(SQLiteDatabase db)
   {
      setSubject(db, this.oSubject);
      setPlace(db, this.oPlace);
      setCal(db, this.oCal);
      setDuration(db, this.oDuration);
      setWeeks(db, this.oWeeks);
      return;
   }

   public boolean isEdited()
   {
      return !(this.subjectIsOriginal() && this.placeIsOriginal()
            && this.weeksIsOriginal() && this.timingsIsOriginal());
   }

   /**
    * Determines if a given lecture is between 2 dates
    * 
    * @param startCal
    *           Start date of timetable
    * @param endCal
    *           End date of timetable
    * @param now
    *           First date to compare to
    * @param test
    *           Second date to compare to
    * @param testWeek
    *           Week to test
    * @return -1 if not inbetween, week number of closest instance to the first
    *         date
    */
   public int isBetween(Calendar startCal, Calendar endCal, Calendar now,
         Calendar test, int testWeek)
   {
      int week = -1;
      boolean first = true;
      Calendar testCal = (Calendar) test.clone();
      setWeek(testCal, startCal, endCal, testWeek);

      // Run through each week in this lectures instances and test if it is
      // between the 2 calendars
      for (int w : weeks)
      {
         setWeek(this.cal, startCal, endCal, w);

         if (this.cal.before(testCal) && this.cal.after(now))
         {
            // First instance that is between is always the closest
            if (first)
            {
               week = w;
               first = false;
            }
            // Any other instances must be tested against previous closest
            else
            {
               Calendar temp = (Calendar) this.cal.clone();
               setWeek(temp, startCal, endCal, week);

               if (temp.after(this.cal))
               {
                  week = w;
               }
            }
         }
      }

      // Set this lectures calendar to reflect the cloest week
      setWeek(this.cal, startCal, endCal, week);
      return week;
   }

   /**
    * Sets the week of year of a calendar, because we span 2 years weeks must
    * ensure the year corresponds to the week
    * 
    * @param cal
    *           Calendar to set
    * @param startCal
    *           Start date of timetable
    * @param endCal
    *           End date of timetable
    * @param week
    *           Week to set
    */
   public static void setWeek(Calendar cal, Calendar startCal, Calendar endCal,
         int week)
   {
      cal.set(Calendar.WEEK_OF_YEAR, week);

      if (cal.before(startCal))
      {
         cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
      }
      else if (cal.after(endCal))
      {
         cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
      }
   }

   /**
    * Test if this lecture occurs on a given week
    * 
    * @param week
    *           Week to test
    * @return Indicates if lecture is on the week
    */
   public boolean onWeek(int week)
   {
      return weeks.contains(week);
   }

   /**
    * Test if this lecture occurs on a given day
    * 
    * @param day
    *           Day to test
    * @return Indicates if lecture is on the day
    */
   public boolean onDay(int day)
   {
      return this.cal.get(Calendar.DAY_OF_WEEK) == day;
   }

   /**
    * Gets the Calendar
    * 
    * @return Calendar of lecture
    */
   public Calendar getCalendar()
   {
      return this.cal;
   }

   /**
    * Gets the place
    * 
    * @return Place where the lecture is
    */
   public Place getPlace()
   {
      return this.place;
   }

   /**
    * Gets the subject
    * 
    * @return subject of the lecture
    */
   public Subject getSubject()
   {
      return this.subject;
   }

   /**
    * Gets the Lecturer
    * 
    * @return Who will be lecturing
    */
   public String getLecturer()
   {
      return this.lecturer;
   }

   /**
    * @return the duration
    */
   public int getDuration()
   {
      return duration;
   }

   /**
    * @return the timeSlot
    */
   public int getTimeSlot()
   {
      return cal.get(Calendar.HOUR_OF_DAY);
   }

   public int getOcurrences()
   {
      return this.weeks.size();
   }

   public void setoId(SQLiteDatabase db, int newId)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_REFID, newId);
      this.oId = newId;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setPlace(SQLiteDatabase db, Place newPlace)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_PLACEID,
            newPlace.getId());
      this.place = newPlace;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setoPlace(SQLiteDatabase db, Place newPlace)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_PLACEID,
            newPlace.getId());
      this.oPlace = newPlace;
   }

   /**
    * Sets the subject
    * 
    * @param newSubject
    *           subject of the lecture
    */
   public void setSubject(SQLiteDatabase db, Subject newSubject)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_SUBJECT,
            newSubject.getUnitCode());
      this.subject = newSubject;
   }

   /**
    * Sets the subject
    * 
    * @param newSubject
    *           subject of the lecture
    */
   public void setoSubject(SQLiteDatabase db, Subject newSubject)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_SUBJECT,
            newSubject.getUnitCode());
      this.oSubject = newSubject;
   }

   /**
    * Sets the Lecturer
    * 
    * @param newLecturer
    *           Who will be lecturing
    */
   public void setLecturer(SQLiteDatabase db, String newLecturer)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_LECTURER,
            newLecturer);
      this.lecturer = newLecturer;
   }

   /**
    * @param duration
    *           the duration to set
    */
   public void setDuration(SQLiteDatabase db, int duration)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_DURATION,
            duration);
      this.oDuration = duration;
   }

   /**
    * @param duration
    *           the duration to set
    */
   public void setoDuration(SQLiteDatabase db, int duration)
   {
      LectureTable.update(db, this.getID(),
            LectureTable.COLUMN_ORIGINALDURATION, duration);
      this.duration = duration;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setCal(SQLiteDatabase db, Calendar newCal)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_TIME,
            LectureTable.COLUMN_DAY, newCal);
      this.cal = newCal;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setoCal(SQLiteDatabase db, Calendar newCal)
   {
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_ORIGINALTIME,
            LectureTable.COLUMN_ORIGINALDAY, newCal);
      this.oCal = newCal;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setWeeks(SQLiteDatabase db, List<Integer> newWeeks)
   {
      LectureTable
            .update(db, this.getID(), LectureTable.COLUMN_WEEKS, newWeeks);
      this.weeks = newWeeks;
   }

   /**
    * Sets the Place
    * 
    * @param newPlace
    *           Place where the lecture is
    */
   public void setoWeeks(SQLiteDatabase db, List<Integer> newWeeks)
   {
      LectureTable
            .update(db, this.getID(), LectureTable.COLUMN_WEEKS, newWeeks);
      this.oWeeks = newWeeks;
   }

   /**
    * @param newWeek
    *           week to add
    */
   public void addWeek(SQLiteDatabase db, int newWeek)
   {
      if (!weeks.contains(newWeek)) weeks.add(newWeek);
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_WEEKS,
            this.weeks);
   }

   /**
    * 
    * @param week
    *           to remove
    * @return indicates if week was removed
    */
   public void removeWeek(SQLiteDatabase db, int newWeek)
   {
      weeks.remove((Object) newWeek);
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_WEEKS,
            this.weeks);
   }

   /**
    * @param type
    *           the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * Deletes this lecture by clearing all instances from week, can be processed
    * later
    */
   public void delete(SQLiteDatabase db)
   {
      weeks.clear();
      LectureTable.update(db, this.getID(), LectureTable.COLUMN_WEEKS, weeks);
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @return the iD
    */
   public long getID()
   {
      return id;
   }

   /**
    * @return the oCal
    */
   public Calendar getoCal()
   {
      return oCal;
   }

   /**
    * @return the oDuration
    */
   public int getoDuration()
   {
      return oDuration;
   }

   /**
    * @return the oSubject
    */
   public Subject getoSubject()
   {
      return oSubject;
   }

   /**
    * @return the oPlace
    */
   public Place getoPlace()
   {
      return oPlace;
   }

   /**
    * @return the oWeeks
    */
   public List<Integer> getoWeeks()
   {
      return oWeeks;
   }

   /**
    * @return the refId
    */
   public long getoId()
   {
      return oId;
   }

   public int getAttendance()
   {
      return attendance;
   }

   public void changeAttendance(SQLiteDatabase db)
   {

      LectureTable.update(db, this.getID(), LectureTable.COLUMN_ATTENDANCE,
            ((this.attendance + 1) % 3));
      this.attendance = ((this.attendance + 1) % 3);

   }

   public void setAttendance(SQLiteDatabase db, int attendance)
   {

      LectureTable.update(db, this.getID(), LectureTable.COLUMN_ATTENDANCE,
            attendance);
      this.attendance = attendance;

   }

   public int getAttendanceColor()
   {
      switch (this.getAttendance())
      {
      case (0):
         return Color.LTGRAY;
      case (1):
         return Color.GREEN;
      case (2):
         return Color.RED;
      default:
         return Color.LTGRAY;
      }
   }

   public void eraseOriginal(SQLiteDatabase db)
   {
      this.setoId(db, 0);
   }
}
