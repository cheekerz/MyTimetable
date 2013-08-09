package com.px.MyTimetable.TimetablePage;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;

public class DayFragment extends Fragment implements OnClickListener, OnLongClickListener
{
   List<Lecture> lectures;
   Calendar cal;
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this. cal = (Calendar) this.getArguments().getSerializable("Calendar");
      this.lectures = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getDaysLectures(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK));
   }
   
   @Override
   public void onResume()
   {
      super.onResume();
      this.lectures = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getDaysLectures(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK));
      LinearLayout timeSlotLayout = (LinearLayout) getView().findViewById(R.id.LinearLayout_day);
      timeSlotLayout.removeAllViews();
      createTimeSlots(LayoutInflater.from(getActivity()), (ViewGroup) getView(), timeSlotLayout);      
   }
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      View v = inflater.inflate(R.layout.activity_day_calendar, container, false);
      if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
         v.findViewById(R.id.DateTitle).setBackgroundResource(R.drawable.background_scheme1_tertiary);
      } else {
         v.findViewById(R.id.DateTitle).setBackgroundResource(R.drawable.background_scheme1_secondary);
      }
      ((TextView) v.findViewById(R.id.DateTitle)).setText(dayToString(cal, Calendar.LONG));
      LinearLayout timeSlotLayout = (LinearLayout) v.findViewById(R.id.LinearLayout_day);
      createTimeSlots(inflater, container, timeSlotLayout);
      return v;
   }

   /**
    * Run through each timeslot and check if a lecture fills that slot, if so populate it
    * @param inflater inflator to use
    * @param container view group container
    * @param timeSlotLayout timeslot layout the timeslots are within
    */
   private void createTimeSlots(LayoutInflater inflater, ViewGroup container, LinearLayout timeSlotLayout)
   {      
      for(int i = 8; i < 19; i++)
      {
         Lecture lecture = null;
         
         // Find lecture in timeslot
         for(Lecture l : lectures)
         {
            if(l.getTimeSlot() == i)
            {
               lecture = l;
               break;
            }
         }
         
         // Add lecture, maybe null, to time slot
         View timeSlot = inflater.inflate(R.layout.timeslot, container, false);
         populateTimeSlot(timeSlot, lecture, i);
         timeSlot.setOnClickListener(this);
         timeSlot.setOnLongClickListener(this);
         ((Button)timeSlot.findViewById(R.id.attendanceButton)).setOnClickListener(this);
         ((Button)timeSlot.findViewById(R.id.timeslot_edit_btn)).setOnClickListener(this);
         ((Button)timeSlot.findViewById(R.id.timeslot_details_btn)).setOnClickListener(this);
         ((Button)timeSlot.findViewById(R.id.timeslot_create_btn)).setOnClickListener(this);
//         View timeSlot = createExpandableTimeSlot(lecture, i, getActivity());
         timeSlotLayout.addView(timeSlot);
         
         if (lecture != null)
         {
            // If duration is more than 1 skip slots that this lecture covers
            i += lecture.getDuration() - 1;
         }
      }
   }
   
   /**
    * Time slot has been clicked, launch lecture page for whatever was in the slot
    */
   @Override
   public void onClick(View v)
   {
      
      if(v.getId() == R.id.attendanceButton)
      {
         Calendar cal = (Calendar)v.getTag();
         Lecture l = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getLecture(this.cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY));
         if (l != null) {
            l.changeAttendance(((TimetableAccess)this.getActivity().getApplication()).getTimetable().getWritableDatabase());
            v.setBackgroundColor(l.getAttendanceColor());
            v.invalidate();
         }
      }
      else if (v.getId() == R.id.timeslot_edit_btn)
      {
         editLecture((View)((View)v.getParent()).getParent());
      }
      else if (v.getId() == R.id.timeslot_details_btn)
      {
         lectureDetails((View)((View)v.getParent()).getParent());
      }
      else if (v.getId() == R.id.timeslot_create_btn)
      {
         createLecture((View)((View)v.getParent()).getParent());
      }
      else
      {
         Calendar timeSlotCal = (Calendar) this.cal.clone();
         timeSlotCal.set(Calendar.HOUR_OF_DAY, v.getId());
         Lecture l = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getLecture(this.cal.get(Calendar.WEEK_OF_YEAR), timeSlotCal.get(Calendar.DAY_OF_WEEK), timeSlotCal.get(Calendar.HOUR_OF_DAY));
        
         if (l != null)
         {
            final View v1 = v.findViewById(R.id.timeslot_more_info);
            if(!v1.isShown())
            {
               expand(v1);
            }
            else
            {
               collapse(v1);
            }
         }
         else
         {
            final View v2 = v.findViewById(R.id.timeslot_more_info_no_lecture);
            if(!v2.isShown())
            {
               expand(v2);
            }
            else
            {
               collapse(v2);
            }
         }
         
      }
//      else {
//         Log.v("DayFragment", "onClick from attendancebutton");
//         Calendar cal = (Calendar)v.getTag();
//         Lecture l = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getLecture(this.cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY));
//         if (l != null) {
//            l.changeAttendance(((TimetableAccess)this.getActivity().getApplication()).getTimetable().getWritableDatabase());
//            v.setBackgroundColor(l.getAttendanceColor());
//            v.invalidate();
//         }
//      }

   }
     
   public static void expand(final View v) {
      v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      final int targetHeight = v.getMeasuredHeight();

      v.getLayoutParams().height = 0;
      v.setVisibility(View.VISIBLE);
      Animation a = new Animation()
      {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
              v.getLayoutParams().height = interpolatedTime == 1
                      ? LayoutParams.WRAP_CONTENT
                      : (int)(targetHeight * interpolatedTime);
              v.requestLayout();
          }

          @Override
          public boolean willChangeBounds() {
              return true;
          }
      };

      // 1dp/ms
      a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
      v.startAnimation(a);
  }
  
  public static void collapse(final View v) {
      final int initialHeight = v.getMeasuredHeight();

      Animation a = new Animation()
      {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
              if(interpolatedTime == 1){
                  v.setVisibility(View.GONE);
              }else{
                  v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                  v.requestLayout();
              }
          }

          @Override
          public boolean willChangeBounds() {
              return true;
          }
      };

      // 1dp/ms
      a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
      v.startAnimation(a);
  }
   
   /**
    * Fill a timeslot with lecture information
    * @param timeSlot Timeslot to fill
    * @param lecture Lecture to get information from
    * @param time Time of the lecture
    */
   public static void populateTimeSlot(View timeSlot, Lecture lecture, int time)
   {
      // Use time as ID to use later for clicking time slots
      timeSlot.setId(time);    
      timeSlot.setTag("slot");

      
      ((TextView)timeSlot.findViewById(R.id.timeslot_time)).setText(Timetable.timeSlotToTime(time));
      // Choose border based on type on lecture
      if(lecture != null)
      {
         if(lecture.getType().equals("Lecture")) timeSlot.setBackgroundResource(R.drawable.customborder_1);
         else if(lecture.getType().equals("Lab/Practical")) timeSlot.setBackgroundResource(R.drawable.customborder_2);
         // Set text if lecture is not null
         ((TextView)timeSlot.findViewById(R.id.timeslot_subject)).setText(lecture != null ? lecture.getSubject().getName() : "");
         ((TextView)timeSlot.findViewById(R.id.timeslot_location)).setText(lecture != null ? lecture.getPlace().getShortAddress() : "");
         ((TextView)timeSlot.findViewById(R.id.timeslot_long_location)).setText(lecture != null ? lecture.getPlace().getFullAddress() : "");
      }
      else
      {
         ((TextView)timeSlot.findViewById(R.id.timeslot_subject)).setVisibility(View.GONE);
         ((TextView)timeSlot.findViewById(R.id.timeslot_location)).setVisibility(View.GONE);
         ((TextView)timeSlot.findViewById(R.id.timeslot_long_location)).setVisibility(View.GONE);
      }
      View attendanceButton = (Button)timeSlot.findViewById(R.id.attendanceButton);
      attendanceButton.setVisibility(lecture != null ? View.VISIBLE : View.INVISIBLE);
      View editButton = (Button)timeSlot.findViewById(R.id.timeslot_edit_btn);
      editButton.setVisibility(lecture != null ? View.VISIBLE : View.INVISIBLE);
      View detailsButton = (Button)timeSlot.findViewById(R.id.timeslot_details_btn);
      detailsButton.setVisibility(lecture != null ? View.VISIBLE : View.INVISIBLE);
      View createButton = (Button)timeSlot.findViewById(R.id.timeslot_create_btn);
      createButton.setVisibility(lecture != null ? View.INVISIBLE : View.VISIBLE);
//      attendanceButton.setVisibility(View.INVISIBLE);
      if (lecture != null) {
         if (lecture.getAttendance() != 0)
            Log.v("DayFragment", "Button date " + dayToString(lecture.getCalendar(), Calendar.SHORT) + " slot " + time + " id(" + lecture.getID() + ") with attendance " + lecture.getAttendance() + "");
         attendanceButton.setTag(lecture.getCalendar());
         attendanceButton.setBackgroundColor(lecture.getAttendanceColor());
         //attendanceButton.invalidate();
      }
      // TODO: get rid of margin definitions from code
      LinearLayout.LayoutParams rlayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      timeSlot.setLayoutParams(rlayout);
   }
   
   /**
    * Convert a date to a displayable string
    * @param calendar Calendar containing date
    * @param style
    * @return Date as a string
    */
   public static String dayToString(Calendar calendar, int style)
   {
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      StringBuilder date = new StringBuilder();
      date.append(calendar.getDisplayName(Calendar.DAY_OF_WEEK, style, Locale.getDefault()));
      date.append(" ");
      if (day == 1 || day == 21 || day == 31)
      {
         date.append(day + "st ");
      }
      else if (day == 2 || day == 22)
      {
         date.append(day + "nd ");
      }
      else if (day == 3 || day == 23)
      {
         date.append(day + "rd ");
      }
      else
      {
         date.append(day + "th ");
      }
      date.append(calendar.getDisplayName(Calendar.MONTH, style, Locale.getDefault()));
      return date.toString();
   }
   
   public void createLecture(View v)
   {
      Calendar timeSlotCal = (Calendar) this.cal.clone();
      timeSlotCal.set(Calendar.HOUR_OF_DAY, v.getId());
      Intent intent = new Intent(this.getActivity(), CreateLecture.class);
      intent.putExtra("Calendar", timeSlotCal);      
      startActivity(intent);
   }
   
   public void lectureDetails(View v)
   {
      Calendar timeSlotCal = (Calendar) this.cal.clone();
      timeSlotCal.set(Calendar.HOUR_OF_DAY, v.getId());
      Intent intent = new Intent(this.getActivity(), LecturePage.class);
      intent.putExtra("Calendar", timeSlotCal);      
      startActivity(intent);
   }
   
   public void editLecture(View v)
   {
      Calendar timeSlotCal = (Calendar) this.cal.clone();
      timeSlotCal.set(Calendar.HOUR_OF_DAY, v.getId());
      Intent intent = new Intent(getActivity(), EditLecture.class);
      intent.putExtra("Calendar", timeSlotCal);      
      startActivity(intent);
   }

   @Override
   public boolean onLongClick(View v)
   {
      Calendar timeSlotCal = (Calendar) this.cal.clone();
      timeSlotCal.set(Calendar.HOUR_OF_DAY, v.getId());
      Lecture l = ((TimetableAccess)this.getActivity().getApplication()).getTimetable().getLecture(this.cal.get(Calendar.WEEK_OF_YEAR), timeSlotCal.get(Calendar.DAY_OF_WEEK), timeSlotCal.get(Calendar.HOUR_OF_DAY));
     
      if (l != null)
      {
         lectureDetails(v);
         return true;
      }
      else
      {
         createLecture(v);
         return true;
      }
      
   }
   
   
   
}