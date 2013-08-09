package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Note;
import com.px.MyTimetable.Entities.Subject;

public class FileTable
{
   public static final String TABLE_NOTES = "notes";
   public static final String COLUMN_SUBJECT = "subject";
   public static final String COLUMN_TIME = "time";
   public static final String COLUMN_DAY = "day";
   public static final String COLUMN_WEEK = "week";
   public static final String COLUMN_FILEPATH = "filepath";

   // Database creation SQL statement
   private static final String DATABASE_CREATE = "create table if not exists " 
       + TABLE_NOTES
       + "(" 
       + COLUMN_SUBJECT + " text not null, " 
       + COLUMN_TIME + " integer not null, " 
       + COLUMN_DAY + " integer not null, " 
       + COLUMN_WEEK + " integer not null, "
       + COLUMN_FILEPATH + " text not null, "
       + "FOREIGN KEY(" + COLUMN_SUBJECT + ") REFERENCES " + SubjectTable.TABLE_SUBJECT + "(" + SubjectTable.COLUMN_UNITCODE + ")"        
       + ");";
   
   public static void onCreate(SQLiteDatabase database) 
   {
      database.execSQL(DATABASE_CREATE);
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
      database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
      onCreate(database);
   }
   
   public static List<Note> getNotes(SQLiteDatabase db, Calendar startCal, Calendar endCal, List<Subject> subjects)
   {
      List<Note> notes = new ArrayList<Note>();
      
      Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null, null);    
      cursor.moveToFirst();
      
      if (cursor.moveToFirst())
      {
         do
         {
            notes.add(cursorToNote(cursor, startCal, endCal, subjects));
         }
         while (cursor.moveToNext());
      }
      
      return notes;      
   }
   
   private static Note cursorToNote(Cursor cursor, Calendar startCal, Calendar endCal, List<Subject> subjects)
   {
      String unitCode = cursor.getString(0);
      Calendar cal = createCalendar(cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), startCal, endCal);
      String filePath = cursor.getString(4);
      
      for (int i = 0; i < subjects.size(); i++)
      {
         if (subjects.get(i).getUnitCode().equals(unitCode))
         {
            return new Note(subjects.get(i), cal, filePath);
         }
      }
      
      return new Note(null, cal, filePath);
   }
   
   private static Calendar createCalendar(int time, int day, int week, Calendar startCal, Calendar endCal)
   {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, time);
      cal.set(Calendar.DAY_OF_WEEK, day);
      Lecture.setWeek(cal, startCal, endCal, week);
      return cal;
   }
   
   public static Note insertRow(SQLiteDatabase database, Subject sub, Calendar cal, String path)
   {
      ContentValues cv = new ContentValues();
      cv.put(COLUMN_SUBJECT, sub.getUnitCode());
      cv.put(COLUMN_TIME, cal.get(Calendar.HOUR_OF_DAY));
      cv.put(COLUMN_DAY, cal.get(Calendar.DAY_OF_WEEK));
      cv.put(COLUMN_WEEK, cal.get(Calendar.WEEK_OF_YEAR));
      cv.put(COLUMN_FILEPATH, path);
      long rowID = database.insert(TABLE_NOTES, null, cv);
      if(rowID == -1)
         throw new Error("Unable to insert into database");
      return new Note(sub, cal, path);
   }
}
