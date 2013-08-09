package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.px.MyTimetable.Entities.Place;

public class PlaceTable
{
   public static final String TABLE_PLACE = "place";
   public static final String COLUMN_ID = "_id";
   public static final String COLUMN_SHORT_ADDRESS = "shortaddress";
   public static final String COLUMN_ROOM = "room";
   public static final String COLUMN_BUILDING = "building";
   public static final String COLUMN_POSTCODE = "postcode";

   // Database creation SQL statement
   private static final String DATABASE_CREATE = "create table if not exists " 
       + TABLE_PLACE
       + "(" 
       + COLUMN_ID + " integer primary key autoincrement, " 
       + COLUMN_SHORT_ADDRESS + " text not null, " 
       + COLUMN_ROOM + " text not null, " 
       + COLUMN_BUILDING + " text not null, "
       + COLUMN_POSTCODE + " text not null"
       + ");";
   
   public static void onCreate(SQLiteDatabase database) 
   {
      database.execSQL(DATABASE_CREATE);
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
      database.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
      onCreate(database);
   }
   
   public static void drop(SQLiteDatabase db)
   {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
   }
   
   public static List<Place> getPlaces(SQLiteDatabase db)
   {
      List<Place> places = new ArrayList<Place>();
      
      Cursor cursor = db.query(TABLE_PLACE, null, null, null, null, null, null);    
      cursor.moveToFirst();
      
      if (cursor.moveToFirst())
      {
         do
         {
            places.add(cursorToPlace(cursor));
         }
         while (cursor.moveToNext());
      }
      
      return places;
   }
   
   private static Place cursorToPlace(Cursor cursor)
   {
      return new Place(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
   }

   public static Place insert(SQLiteDatabase db, String shortAddress, String room, String building, String postcode)
   {
      ContentValues values = new ContentValues();
      // Ensure none of the arguments are null, so we have a string to compare to when trying to find places
      if (shortAddress == null) shortAddress = "";
      if (room == null) room = "";
      if (building == null) building = "";
      if (postcode == null) postcode = "";
      values.put(COLUMN_SHORT_ADDRESS, shortAddress);
      values.put(COLUMN_ROOM, room);
      values.put(COLUMN_BUILDING, building);
      values.put(COLUMN_POSTCODE, postcode);
      long id = db.insert(TABLE_PLACE, null, values);
      return new Place(id, shortAddress, room, building, postcode);
   }
   
   public static void delete(SQLiteDatabase db, long placeId)
   {
      db.delete(TABLE_PLACE, COLUMN_ID + "=?", new String[] { Long.toString(placeId) });
   }
}
