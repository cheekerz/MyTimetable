package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Place;
import com.px.MyTimetable.Entities.Subject;

public class LectureTable
{
   public static final String TABLE_LECTURES = "lectures";
      
   public static final String COLUMN_ID = "_id";                              //  0
   public static final String COLUMN_REFID = "ref_id";                        //  1
   
   public static final String COLUMN_ORIGINALSUBJECT = "original_subject";    //  2
   public static final String COLUMN_ORIGINALPLACEID = "original_place_id";   //  3
   public static final String COLUMN_ORIGINALTIME = "original_time";          //  4
   public static final String COLUMN_ORIGINALDAY = "original_day";            //  5
   public static final String COLUMN_ORIGINALDURATION = "original_duration";  //  6
   public static final String COLUMN_ORIGINALWEEKS = "original_weeks";        //  7
   
   public static final String COLUMN_SUBJECT = "subject";                     //  8
   public static final String COLUMN_PLACEID = "place_id";                    //  9
   public static final String COLUMN_TIME = "time";                           // 10
   public static final String COLUMN_DAY = "day";                             // 11
   public static final String COLUMN_DURATION = "duration";                   // 12
   public static final String COLUMN_WEEKS = "weeks";                         // 13

   public static final String COLUMN_TYPE = "type";                           // 14
   public static final String COLUMN_LECTURER = "lecturer";                   // 15
   public static final String COLUMN_ATTENDANCE = "attendance";               // 16
   
   // Database creation sql statement
   private static final String DATABASE_CREATE = "create table if not exists "
      + TABLE_LECTURES
      + "("
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_REFID + " long DEFAULT 0, "
      
      + COLUMN_ORIGINALSUBJECT + " text DEFAULT null, "
      + COLUMN_ORIGINALPLACEID + " long DEFAULT 0, "
      + COLUMN_ORIGINALTIME + " integer not null, "
      + COLUMN_ORIGINALDAY + " integer not null, "
      + COLUMN_ORIGINALDURATION + " integer not null, "
      + COLUMN_ORIGINALWEEKS + " text DEFAULT null, "
      
      + COLUMN_SUBJECT + " text not null, "
      + COLUMN_PLACEID + " long not null, "
      + COLUMN_TIME + " integer not null, "
      + COLUMN_DAY + " integer not null, "
      + COLUMN_DURATION + " integer not null, "
      + COLUMN_WEEKS + " text, "
      
      + COLUMN_TYPE + " text not null, "
      + COLUMN_LECTURER + " text, "
      
      + COLUMN_ATTENDANCE + " integer DEFAULT 0, "
      
      + "FOREIGN KEY(" + COLUMN_SUBJECT + ") REFERENCES " + SubjectTable.TABLE_SUBJECT + "(" + SubjectTable.COLUMN_UNITCODE + "), " 
      + "FOREIGN KEY(" + COLUMN_PLACEID + ") REFERENCES " + PlaceTable.TABLE_PLACE + "(" + PlaceTable.COLUMN_ID + "), "
      + "FOREIGN KEY(" + COLUMN_ORIGINALSUBJECT + ") REFERENCES " + SubjectTable.TABLE_SUBJECT + "(" + SubjectTable.COLUMN_UNITCODE + "), " 
      + "FOREIGN KEY(" + COLUMN_ORIGINALPLACEID + ") REFERENCES " + PlaceTable.TABLE_PLACE + "(" + PlaceTable.COLUMN_ID + ")"
      + ");";
   
   public static void onCreate(SQLiteDatabase database) 
   {
      database.execSQL(DATABASE_CREATE);
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
     database.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURES);
     onCreate(database);
   }
   
   public static void drop(SQLiteDatabase db)
   {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURES);
   }
   
   public static Lecture insert(
         SQLiteDatabase db,
         
         long refId,
         
         Subject originalSubject,
         Place originalPlace,
         Calendar originalCal,
         int originalDuration,
         List<Integer> originalWeeks,
         
         Subject subject,
         Place place,
         Calendar cal,
         int duration,
         List<Integer> weeks,
         
         String type,
         String lecturer,
         
         int attendance)
   {
      ContentValues values = new ContentValues();
      values.put(COLUMN_REFID, refId);
      
      values.put(COLUMN_ORIGINALSUBJECT, originalSubject.getUnitCode());
      values.put(COLUMN_ORIGINALPLACEID, originalPlace.getId());
      values.put(COLUMN_ORIGINALTIME, originalCal.get(Calendar.HOUR_OF_DAY));
      values.put(COLUMN_ORIGINALDAY, originalCal.get(Calendar.DAY_OF_WEEK));
      values.put(COLUMN_ORIGINALDURATION, originalDuration);
      values.put(COLUMN_ORIGINALWEEKS, weeksToString(originalWeeks));
      
      values.put(COLUMN_SUBJECT, subject.getUnitCode());
      values.put(COLUMN_PLACEID, place.getId());
      values.put(COLUMN_TIME, cal.get(Calendar.HOUR_OF_DAY));
      values.put(COLUMN_DAY, cal.get(Calendar.DAY_OF_WEEK));
      values.put(COLUMN_DURATION, duration);
      values.put(COLUMN_WEEKS, weeksToString(weeks));
      
      values.put(COLUMN_TYPE, type);
      values.put(COLUMN_LECTURER, lecturer);
      values.put(COLUMN_ATTENDANCE, attendance);
      
      long id = db.insert(TABLE_LECTURES, null, values);
      return new Lecture(id, refId, originalSubject, originalPlace, originalCal, originalDuration, originalWeeks, subject, place, cal, duration, weeks, type, lecturer, attendance);
   }

   public static List<Lecture> getLectures(SQLiteDatabase db, Calendar startCal, Calendar endCal, List<Subject> subjects, List<Place> places)
   {
      List<Lecture> lectures = new ArrayList<Lecture>();
      Lecture l;
      Cursor cursor = db.query(TABLE_LECTURES, null, null, null, null, null, null);    
      cursor.moveToFirst();
      
      if (cursor.moveToFirst())
      {
         do
         {
            l = (cursorToLecture(cursor, startCal, endCal, subjects, places));
            if(l != null) lectures.add(l);
         }
         while (cursor.moveToNext());
      }
      
      return lectures;
   }
   
   public static Lecture cursorToLecture(Cursor cursor, Calendar startCal, Calendar endCal, List<Subject> subjects, List<Place> places)
   {
      String oSubjectId = cursor.getString(2);
      String subjectId = cursor.getString(8);
      int oSubjectIdIndex = -1;
      int subjectIdIndex = -1;
      
      for (int i = 0; i < subjects.size(); i++)
      {
         if (subjects.get(i).getUnitCode().equals(oSubjectId))
         {
            oSubjectIdIndex = i;
         }
         if (subjects.get(i).getUnitCode().equals(subjectId))
         {
            subjectIdIndex = i;
         }
      }
      

      long oPlaceId = cursor.getLong(3);
      long placeId = cursor.getLong(9);
      int oPlaceIdIndex = 0;
      int placeIdIndex = 0;
      
      for (int i = 0; i < places.size(); i++)
      {
         if (places.get(i).getId() == oPlaceId)
         {
            oPlaceIdIndex = i;
         }
         if (places.get(i).getId() == placeId)
         {
            placeIdIndex = i;
         }
      }
      
      List<Integer> oWeeks = stringToWeeks(cursor.getString(7));
      List<Integer> weeks = stringToWeeks(cursor.getString(13));
      if(weeks == null) return null; // Do not add lecture
      Calendar oCal = createCalendar(cursor.getInt(4), cursor.getInt(5), oWeeks, startCal, endCal);
      Calendar cal = createCalendar(cursor.getInt(10), cursor.getInt(11), weeks, startCal, endCal);
      
      return new Lecture(cursor.getLong(0), cursor.getLong(1), subjects.get(oSubjectIdIndex), places.get(oPlaceIdIndex), oCal, cursor.getInt(6), oWeeks, subjects.get(subjectIdIndex), places.get(placeIdIndex), cal, cursor.getInt(12), weeks, cursor.getString(14), cursor.getString(15), cursor.getInt(16));
   }
   
   private static Calendar createCalendar(int time, int day, List<Integer> weeks, Calendar startCal, Calendar endCal)
   {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, time);
      cal.set(Calendar.DAY_OF_WEEK, day);
      Lecture.setWeek(cal, startCal, endCal, weeks.get(0));
      return cal;
   }
   
   private static String weeksToString(List<Integer> weeks)
   {
      StringBuilder s = new StringBuilder();
      
      if (weeks.size() > 0)
      {
         s.append(weeks.get(0));
      }
      
      for (int i = 1; i < weeks.size(); i++)
      {
         s.append(',');
         s.append(weeks.get(i));
      }
      
      return s.toString();
   }
   
   private static List<Integer> stringToWeeks(String weekCommaList)
   {
      List<Integer> weeks = new ArrayList<Integer>();      
      String[] weekList = weekCommaList.split(",");
      
      for (String week : weekList)
      {
         if(week.equals("")) return null;
         weeks.add(Integer.parseInt(week));
      }      
      
      return weeks;
   }

   public static void update(SQLiteDatabase db, long lectureId, String column, long value)
   {
      ContentValues values = new ContentValues();
      values.put(column, value);
      db.update(TABLE_LECTURES, values, COLUMN_ID + "=?", new String[] { Long.toString(lectureId) });
   }

   public static void update(SQLiteDatabase db, long lectureId, String column, String value)
   {
      ContentValues values = new ContentValues();
      values.put(column, value);
      db.update(TABLE_LECTURES, values, COLUMN_ID + "=?", new String[] { Long.toString(lectureId) });
   }
   
   public static void update(SQLiteDatabase db, long lectureId, String timeColumn, String dayColumn, Calendar cal)
   {
      ContentValues values = new ContentValues();
      values.put(timeColumn, cal.get(Calendar.HOUR_OF_DAY));
      values.put(dayColumn, cal.get(Calendar.DAY_OF_WEEK));
      db.update(TABLE_LECTURES, values, COLUMN_ID + "=?", new String[] { Long.toString(lectureId) });
   }
   
   public static void update(SQLiteDatabase db, long lectureId, String column, List<Integer> weeks)
   {
      ContentValues values = new ContentValues();
      values.put(column, weeksToString(weeks));
      db.update(TABLE_LECTURES, values, COLUMN_ID + "=?", new String[] { Long.toString(lectureId) });      
   }
   
   public static void delete(SQLiteDatabase db, long lectureId)
   {
      db.delete(TABLE_LECTURES, COLUMN_ID + "=?", new String[] { Long.toString(lectureId) });
   }
}