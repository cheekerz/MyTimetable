package com.px.MyTimetable.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.DialogFactory;
import com.px.MyTimetable.Dialogs.DialogListener;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.TimetablePage.DayFragment;

public class CalendarWarning extends Activity implements DialogListener
{
   private int lecturePosition;
   private String subject;
   private int place;
   private Calendar cal;
   private int duration;
   private int[] weeks;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.lecturePosition = this.getIntent().getIntExtra("LecturePosition", -1);
      int purpose = this.getIntent().getIntExtra("Purpose", 3);
      Lecture lecture = ((TimetableAccess)getApplication()).getTimetable().getLectureFromPosition(lecturePosition);
      StringBuilder builder = new StringBuilder();
      builder.append("Lecture ");
      builder.append(lecture.getSubject().getName());
      builder.append(" at ");
      builder.append(Timetable.timeSlotToTime(lecture.getTimeSlot()));
      builder.append(" on the ");
      builder.append(DayFragment.dayToString(lecture.getCalendar(), Calendar.SHORT));
      // TODO deal with subjects changing as well well as instances
      
      if (purpose == 0)
      {
         builder.append(this.getString(R.string.NotificationDeleteWarning));
         DialogFactory.getDialog(builder.toString(), this, purpose, this).show();
      }
      else if (purpose == 1)
      {
         builder.append(this.getString(R.string.NotificationAddWarning));
         DialogFactory.getDialog(builder.toString(), this, purpose, this).show();
      }
      else if (purpose == 2)
      {
         this.subject = this.getIntent().getStringExtra("Subject");
         this.place = this.getIntent().getIntExtra("Place", -1);
         this.cal = (Calendar) this.getIntent().getSerializableExtra("Calendar");
         this.duration = this.getIntent().getIntExtra("Duration", -1);
         this.weeks = this.getIntent().getIntArrayExtra("Weeks");
         builder.append(" has been modified:\n");

         // TODO create dialog showing all fields that have changed
         if (subject != null)
         {
            
         }
         if (place >= 0)
         {
            
         }
         if (cal != null)
         {

         }
         if (duration >= 0)
         {

         }
         if (weeks != null)
         {

         }
         
         builder.append(this.getString(R.string.NotificationUpdateWarning));
         DialogFactory.getDialog(builder.toString(), this, purpose, this).show();         
      }
   }
   
   @Override
   public void onDialogPositiveClick(int id)
   {
      Timetable timetable = ((TimetableAccess)getApplication()).getTimetable();
      
      if (id == 2)
      {
         List<Integer> w = new ArrayList<Integer>();
   
         for (int week : weeks)
         {
            w.add(week);
         }
         
         if (subject != null)
         {
            timetable.getLectureFromPosition(lecturePosition).setSubject(timetable.getWritableDatabase(), timetable.getSubject(timetable.getSubjectIndex(this.subject)));
         }
         if (place >= 0)
         {
            timetable.getLectureFromPosition(lecturePosition).setPlace(timetable.getWritableDatabase(), timetable.getPlaceFromId(this.place));            
         }
         if (cal != null)
         {
            timetable.getLectureFromPosition(lecturePosition).setCal(timetable.getWritableDatabase(), this.cal);            
         }
         if (duration >= 0)
         {
            timetable.getLectureFromPosition(lecturePosition).setDuration(timetable.getWritableDatabase(), this.duration);
         }
         if (weeks != null)
         {
            timetable.getLectureFromPosition(lecturePosition).setWeeks(timetable.getWritableDatabase(), w);
         }
      }
      else if (id == 0)
      {
         timetable.deleteLecture(this.lecturePosition);
      }
      
      this.finish();
   }

   @Override
   public void onDialogNegativeClick(int id)
   {
      Timetable timetable = ((TimetableAccess)getApplication()).getTimetable();
      
      if (id == 0)
      {
         timetable.getLectureFromPosition(this.lecturePosition).eraseOriginal(timetable.getWritableDatabase());
      }
      else if (id == 1)
      {
         timetable.getLectureFromPosition(this.lecturePosition).setWeeks(timetable.getWritableDatabase(), new ArrayList<Integer>());
      }
      
      this.finish();
   }   
}