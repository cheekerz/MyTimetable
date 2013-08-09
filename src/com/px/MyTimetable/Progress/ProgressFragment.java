package com.px.MyTimetable.Progress;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;

public class ProgressFragment extends Fragment implements OnClickListener
{
   private List<Integer> lectureIds;
   private List<Integer> lectureAttendances;
   private String subjectName;
   private int statsVisibility;
   private boolean isShown = true;
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.subjectName = (String)this.getArguments().getSerializable("Subject");
      this.lectureIds = (List<Integer>)this.getArguments().getSerializable("LectureIds");
      Timetable timetable = ((TimetableAccess)this.getActivity().getApplication()).getTimetable();
      this.lectureAttendances = new ArrayList<Integer>();
      
      for (int id : lectureIds)
      {
         lectureAttendances.add(timetable.getLectureById(id).getAttendance());
      }
   }
   
   @Override
   public void onResume()
   {
      super.onResume();
      Timetable timetable = ((TimetableAccess)this.getActivity().getApplication()).getTimetable();
      Lecture l;
      this.lectureAttendances = new ArrayList<Integer>();
         for (int id : lectureIds) {
            l = timetable.getLectureById(id);
            if(l != null)
            {
               lectureAttendances.add(l.getAttendance()); 
            }
         }
   }
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      View v = inflater.inflate(R.layout.progress_view, container, false);
      populateProgressView(v,  this.subjectName, (int)percentAttendance(), lectureAttendances.size(), attended(), missed());
      this.statsVisibility = View.VISIBLE;
      showHideStats(v);
      v.setOnClickListener(this);
      if (attended() == 0 && missed() == 0) {
         v.setVisibility(View.GONE);
         this.isShown = false;
      }
      return v;
   }
   
   public boolean isShown()
   {
      return this.isShown;
   }

   /**
    * progressView has been clicked
    */
   @Override
   public void onClick(View v)
   {
      showHideStats(v);
   }
   
   public void showHideStats(View v) {
      this.statsVisibility = (this.statsVisibility == View.VISIBLE) ? View.GONE : View.VISIBLE;
      ((TextView)v.findViewById(R.id.text_view_attended)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_attended_num)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_missed)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_missed_num)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_unmarked)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_unmarked_num)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_total)).setVisibility(statsVisibility);
      ((TextView)v.findViewById(R.id.text_view_total_num)).setVisibility(statsVisibility);
   }
   
   /**
    * Fill a progressView with stat information
    * @param progressView progressView to fill
    * @param lecture Lecture to get information from
    */
   public static void populateProgressView(View progressView, String name, int percentAttendance, int total, int attended, int missed)
   {
      // Use time as ID to use later for clicking time slots
      progressView.setId(1);    
      progressView.setTag("progressView");
      ((TextView)progressView.findViewById(R.id.progress_view_title)).setText(name);
      ((TextView)progressView.findViewById(R.id.progress_view_percent)).setText(Integer.toString(percentAttendance) + "%");
      ((TextView)progressView.findViewById(R.id.text_view_attended_num)).setText(Integer.toString(attended));
      ((TextView)progressView.findViewById(R.id.text_view_missed_num)).setText(Integer.toString(missed));
      ((TextView)progressView.findViewById(R.id.text_view_unmarked_num)).setText(Integer.toString(total-attended-missed));
      ((TextView)progressView.findViewById(R.id.text_view_total_num)).setText(Integer.toString(total));
      // TODO: get rid of margin definitions from code
      LinearLayout.LayoutParams rlayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      rlayout.setMargins(4, 2, 4, 2);
      
      progressView.setLayoutParams(rlayout);
      ProgressBar pg = (ProgressBar)progressView.findViewById(R.id.progress_bar);
      /*ShapeDrawable pgDrawable = new ShapeDrawable(new RectShape());
      String green = "#00bb00";
      pgDrawable.getPaint().setColor(Color.parseColor(green));
      ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
      pg.setProgressDrawable(progress); 
      pg.setMax(100);
      Log.v("ProgressFragment", "percent = " + percentAttendance);
      //pg.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));*/
      pg.setProgress(percentAttendance);
      pg.setSecondaryProgress(100);
   }
   
   private double percentAttendance() {
      double totalMarkedAttendance = 0;
      double totalAttended = 0;
      for (int a : this.lectureAttendances) {
         if (a != 0) {
            totalMarkedAttendance++;
            if (a == 1) {
               totalAttended++;
            }
         }
      }
      return (totalMarkedAttendance == 0.0) ? 0.0 : (100.0*totalAttended/totalMarkedAttendance);
   }
   
   private int attended() {
      int attended = 0;
      for (int a : this.lectureAttendances) {
         if (a == 1) {
            attended++;
         }
      }
      return attended;
   }
   
   private int missed() {
      int missed = 0;
      for (int a : this.lectureAttendances) {
         if (a == 2) {
            missed++;
         }
      }
      return missed;
   }
}