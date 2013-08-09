package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.px.MyTimetable.Entities.Subject;

public class SubjectTable
{
   public static final String TABLE_SUBJECT = "subject";
   public static final String COLUMN_UNITCODE = "_id";
   public static final String COLUMN_NAME = "name";
   public static final String COLUMN_DEPARTMENT = "department";

   // Database creation SQL statement
   private static final String DATABASE_CREATE = "create table if not exists " 
      + TABLE_SUBJECT
      + "(" 
      + COLUMN_UNITCODE + " text primary key, " 
      + COLUMN_NAME + " text not null, " 
      + COLUMN_DEPARTMENT + " text not null"
      + ");";
   
   public static void onCreate(SQLiteDatabase database) 
   {
      database.execSQL(DATABASE_CREATE);
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
     database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
     onCreate(database);
   }
   
   public static void drop(SQLiteDatabase db)
   {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
   }
   
   public static List<Subject> getSubjects(SQLiteDatabase db)
   {
      List<Subject> subjects = new ArrayList<Subject>();
      
      Cursor cursor = db.query(TABLE_SUBJECT, null, null, null, null, null, null);    
      cursor.moveToFirst();
      
      if (cursor.moveToFirst())
      {
         do
         {
            subjects.add(cursorToSubject(cursor));
         }
         while (cursor.moveToNext());
      }
      
      return subjects;
   }
   
   private static Subject cursorToSubject(Cursor cursor)
   {
      return new Subject(cursor.getString(1), cursor.getString(0), cursor.getColumnName(2));
   }
   
   public static Subject insert(SQLiteDatabase db, String unitCode, String name, String department)
   {
      ContentValues values = new ContentValues();
      values.put(COLUMN_UNITCODE, unitCode);
      values.put(COLUMN_NAME, name);
      values.put(COLUMN_DEPARTMENT, department);
      db.insert(TABLE_SUBJECT, null, values);
      return new Subject(name, unitCode, department);      
   }
   
   public static void delete(SQLiteDatabase db, String unitCode)
   {
      db.delete(TABLE_SUBJECT, COLUMN_UNITCODE + "=?", new String[] { unitCode });
   }
}
