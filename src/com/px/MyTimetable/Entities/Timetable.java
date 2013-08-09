package com.px.MyTimetable.Entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.px.MyTimetable.Data.FileTable;
import com.px.MyTimetable.Data.LectureTable;
import com.px.MyTimetable.Data.PlaceTable;
import com.px.MyTimetable.Data.SubjectTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Timetable extends SQLiteOpenHelper
{
   public static final String DATABASE_NAME = "timetable.db";
   public static final int DATABASE_VERSION = 1;
   
   List<Lecture> lectures;
   List<Subject> subjects;
   List<Place> places;
   List<Note> notes;
   private Calendar startCal;
   private Calendar endCal;
   
   public Timetable(Context context, Calendar startCal, Calendar endCal)
   {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
      SQLiteDatabase db = this.getReadableDatabase();
      //context.deleteDatabase(DATABASE_NAME);
      this.subjects = SubjectTable.getSubjects(db);
      this.places = PlaceTable.getPlaces(db);
      this.notes = FileTable.getNotes(db, startCal, endCal, subjects);
      this.lectures = LectureTable.getLectures(db, startCal, endCal, this.subjects, this.places);
      this.startCal = startCal;
      this.endCal = endCal;
      db.close();
   }
   
   /**
    * Retrieve the closest lecture after the given calendar, returns endOfTime lecture if no more lectures
    * @param cal when to start looking for lectures from
    * @return
    */
   public Lecture getNextLecture(Calendar cal)
   {
      int week = 1;
      Calendar endOfTime = Calendar.getInstance();
      // All remaining lectures should be after this
      endOfTime.set(Calendar.YEAR, endOfTime.getActualMaximum(Calendar.YEAR));
      List<Integer> weeks = new ArrayList<Integer>();
      weeks.add(1);
      Lecture lecture = new Lecture(0, 0, null, null, endOfTime, 1, weeks, null, null, endOfTime, 1, weeks, null, null, 0);

      for (Lecture l : lectures)
      {  
         int result = l.isBetween(startCal, endCal, cal, lecture.getCalendar(), week);
         
         if (result != -1)
         {
            // This lecture is before the previous closest, record it and look for better
            week = result;
            lecture = l;
         }
      }

      return lecture;
   }

   public Lecture getLectureFromPosition(int pos)
   {
      if (pos >= 0 && pos < lectures.size())
      {
         return lectures.get(pos);
      }
      
      return null;
   }
   
   public int getLecturePositionFromOID(long id)
   {
      for (int i = 0; i < lectures.size(); i++)
      {
         if (lectures.get(i).getoId() == id)
         {
            return i;
         }
      }
      
      return -1;
   }
   
   public int getLecturePositionFromID(long id)
   {
      for (int i = 0; i < lectures.size(); i++)
      {
         if (lectures.get(i).getID() == id)
         {
            return i;
         }
      }
      
      return -1;
   }
   
   /**
    * Retrieve a given lecture
    * @param week Week lecture appears in
    * @param day Day lecture is on
    * @param timeSlot Timeslot lecture is in
    * @return Lecture that fits all the criteria, null if non found
    */
   public Lecture getLecture(int week, int day, int timeSlot)
   {
      for (Lecture lecture : lectures)
      {
         if (lecture.getTimeSlot() == timeSlot && lecture.onDay(day) && lecture.onWeek(week))
         {
            return lecture;
         }
      }
      
      return null;
   }

   /**
    * Retrieve all lectures in a day
    * @param week Week of lectures
    * @param day Day lectures occur on
    * @return List of lectures occuring on the specified week and day, could be empty
    */
   public List<Lecture> getDaysLectures(int week, int day)
   {
      List<Lecture> daysLectures = new ArrayList<Lecture>();

      for (Lecture lecture : this.lectures)
      {
         if (lecture.onDay(day) && lecture.onWeek(week))
         {
            daysLectures.add(lecture);
         }
      }

      return daysLectures;
   }

   /**
    * Retrieve all lecture in a week
    * @param week Week to look in
    * @return List of all lectures in the given week, could be empty
    */
   public List<Lecture> getWeekLectures(int week)
   {
      List<Lecture> weeksLectures = new ArrayList<Lecture>();

      for (Lecture lecture : this.lectures)
      {
         if (lecture.onWeek(week))
         {
            weeksLectures.add(lecture);
         }
      }

      return weeksLectures;
   }
   
   public List<Note> getNotes() {
      return notes;
   }
   /**
    * Retrieve all lecture of a given subject
    * @param subject Subject associated with lectures
    * @return List of all lectures of the given subject
    */
   public List<Lecture> getLectureSeries(Subject subject, String type)
   {
      List<Lecture> lectureSeries = new ArrayList<Lecture>();
      for (Lecture lecture : this.lectures)
      {
         if (lecture.getSubject().equals(subject) && lecture.getType().equals(type))
         {
            lectureSeries.add(lecture);
         }
      }
      
      return lectureSeries;
   }

   public List<Lecture> getLectures() {
      return lectures;
   }
   
   public List<Lecture> getLectures(Subject subject) {
      List<Lecture> lectures = new ArrayList<Lecture>();
      for (Lecture l : this.lectures) {
         if (l.getSubject().isEqual(subject)) {
            lectures.add(l);
         }
      }
      return lectures;
   }
   
   public Lecture getLectureById(int id) {
      for (Lecture l : lectures) {
         if (l.getID() == id) {
            return l;
         }
      }
      return null;
   }
   
   /**
    * Add a lecture to the timetable
    * @param lecture Lecture to add
    */
   public Lecture addLecture(long refId,
         
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
         int attendance
         )
   {
      Lecture lecture = LectureTable.insert(this.getWritableDatabase(), refId, originalSubject, originalPlace, originalCal, originalDuration, originalWeeks, subject, place, cal, duration, weeks, type, lecturer, attendance);
      lectures.add(lecture);
      return lecture;
   }
   
   public Note addNote(Subject sub, Calendar cal, String path)
   {
      Note note = FileTable.insertRow(this.getWritableDatabase(), sub, cal, path);
      notes.add(note);
      return note;
   }

   //TODO not sure if works
   public void deleteLecture(int index)
   {
      if(getLectureFromPosition(index).isOriginal())
      {
         getLectureFromPosition(index).delete(getWritableDatabase());
      }
      else
      {
         LectureTable.delete(getWritableDatabase(), this.lectures.get(index).getID());
         this.lectures.remove(index);
      }
   }
   
   public int getLectureIndex(Subject subject, Place place, Calendar cal, int duration, String type, String lecturer)
   {
      for (int i = 0; i < this.lectures.size(); i++)
      {         
         if (lectures.get(i).isInstance(subject, place, cal, duration, type, lecturer))
         {
            return i;
         }
      }
      
      return -1;
   }
   
   public Subject addSubject(String code, String name, String depCode)
   {
      Subject subject = SubjectTable.insert(this.getWritableDatabase(), code, name, depCode);
      subjects.add(subject);
      return subject;
   }
   
   public void deleteSubject(Subject subject)
   {
      // TODO: find and remove subject from list
      SubjectTable.delete(getWritableDatabase(), subject.getUnitCode());
   }
   
   public int getSubjectIndex(String unitCode)
   {
      for (int i = 0; i < this.subjects.size(); i++)
      {
         if (this.subjects.get(i).getUnitCode().equals(unitCode))
         {
            return i;
         }
      }
      
      return -1;
   }
   
   public Subject getSubject(int i)
   {
      return this.subjects.get(i);
   }
   
   public Place addPlace(String shortAddress, String room, String building, String postcode)
   {
      Place place = PlaceTable.insert(this.getReadableDatabase(), shortAddress, room, building, postcode);
      places.add(place);
      return place;
   }
   
   public List<Subject> getSubjects() {
      return subjects;
   }
   
   public void deletePlace(Place place)
   {
      // TODO: find and remove place from list
      PlaceTable.delete(getWritableDatabase(), place.getId());
   }
   
   public Place getPlaceFromId(int id)
   {
      for (Place place : this.places)
      {
         if (place.getId() == id)
         {
            return place;
         }
      }
      
      return null;
   }
   
   public int getPlaceIndex(String room, String building, String postcode)
   {
      for (int i = 0; i < this.places.size(); i++)
      {
         if (this.places.get(i).getRoom().equals(room) && this.places.get(i).getBuilding().equals(building) && this.places.get(i).getPostcode().equals(postcode))
         {
            return i;
         }
      }
      
      return -1;
   }
   
   public Place getPlace(int i)
   {
      return this.places.get(i);
   }
   
   /**
    * Convert timeslot number to a time string
    * @param timeSlot timeslot to convert
    * @return string representation of timeslot
    */
   public static String timeSlotToTime(int timeSlot)
   {
      switch (timeSlot)
      {
      case 0:
         return "0.00am";
      case 1:
         return "1.00am";
      case 2:
         return "2.00am";
      case 3:
         return "3.00am";
      case 4:
         return "4.00am";
      case 5:
         return "5.00am";
      case 6:
         return "6.00am";
      case 7:
         return "7.00am";
      case 8:
         return "8.00am";
      case 9:
         return "9.00am";
      case 10:
         return "10.00am";
      case 11:
         return "11.00am";
      case 12:
         return "12.00pm";
      case 13:
         return "1.00pm";
      case 14:
         return "2.00pm";
      case 15:
         return "3.00pm";
      case 16:
         return "4.00pm";
      case 17:
         return "5.00pm";
      case 18:
         return "6.00pm";
      case 19:
         return "7.00pm";
      case 20:
         return "8.00pm";
      case 21:
         return "9.00pm";
      case 22:
         return "10.00pm";
      case 23:
         return "11.00pm";
      }
      return "";
   }

   /**
    * @return the startCal
    */
   public Calendar getStartCal()
   {
      return this.startCal;
   }

   /**
    * @return the endCal
    */
   public Calendar getEndCal()
   {
      return this.endCal;
   }
   
   public int getLectureCount()
   {
      return this.lectures.size();
   }

   @Override
   public void onCreate(SQLiteDatabase db)
   {
      SubjectTable.onCreate(db);
      PlaceTable.onCreate(db);
      LectureTable.onCreate(db);
      FileTable.onCreate(db);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      SubjectTable.onUpgrade(db, oldVersion, newVersion);
      PlaceTable.onUpgrade(db, oldVersion, newVersion);
      LectureTable.onUpgrade(db, oldVersion, newVersion);
      FileTable.onUpgrade(db, oldVersion, newVersion);
      onCreate(db);      
   }
   
   public ArrayList<String> getUnitNames()
   {
      ArrayList<String> unitNames = new ArrayList<String>();
      String temp1, temp2;
      for(Subject subject : subjects)
      {
         temp1 = subject.getUnitCode();
         temp2 = subject.getName();
         if(!temp1.equals("") && !temp2.equals(""))
         {
            unitNames.add(temp1 + " - " + temp2);
         }
      }
      return unitNames;
   }

   public ArrayList<String> getAddressDescriptions()
   {
      ArrayList<String> addressDescriptions = new ArrayList<String>();
      String temp1, temp2;
      for(Place place : places)
      {
         temp1 = place.getShortAddress();
         temp2 = place.getPostcode();
         if(!temp1.equals("") && !temp2.equals(""))
         {
            addressDescriptions.add(temp1 + "\n" + place.getRoom() + "\n" + place.getBuilding() + "\n" + temp2);
         }
      }
      return addressDescriptions;
   }
   
   public ArrayList<String> getShortAddressNames()
   {
      ArrayList<String> shortAddressNames = new ArrayList<String>();
      String temp;
      for(Place place : places)
      {
         temp = place.getShortAddress();
         if(!temp.equals(""))
         {
            shortAddressNames.add(temp);
         }
      }
      return shortAddressNames;
   }
   
   public ArrayList<String> getRoomNames()
   {
      ArrayList<String> roomNames = new ArrayList<String>();
      String temp;
      for(Place place : places)
      {
         temp = place.getRoom();
         if(!temp.equals(""))
         {
            roomNames.add(temp);
         }
      }
      return roomNames;
   }
   public ArrayList<String> getBuildingNames()
   {
      ArrayList<String> buildingNames = new ArrayList<String>();
      String temp;
      for(Place place : places)
      {
         temp = place.getBuilding();
         if(!temp.equals(""))
         {
            buildingNames.add(temp);
         }
      }
      return buildingNames;
   }
   public ArrayList<String> getPostcodeNames()
   {
      ArrayList<String> postcodeNames = new ArrayList<String>();
      String temp;
      for(Place place : places)
      {
         temp = place.getPostcode();
         if(!temp.equals(""))
         {
            postcodeNames.add(temp);
         }
      }
      return postcodeNames;
   }
   
   public List<Place> getPlaces()
   {
      return this.places;
   }
}
