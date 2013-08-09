package com.px.MyTimetable.TimetablePage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.DialogFactory;
import com.px.MyTimetable.Dialogs.DialogListener;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Note;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.FileChooser.FileChooser;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Progress.MyTimetableProgress;
import com.px.MyTimetable.Settings.MyTimetableSettings;
import com.px.MyTimetable.Widget.WidgetUpdater;

public class LecturePage extends Activity implements DialogListener, OnClickListener
{
   private Lecture lecture;
   private Calendar cal;
   private String path;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.lecture_page, menu);
      return true;
   }

   @Override
   public void onResume()
   {
      super.onResume();      
      setContentView(R.layout.activity_lecture_page); 
      
      // Stop auto focus
      this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      
      // try and find this lecture in timetable 
      this.cal = (Calendar) this.getIntent().getSerializableExtra("Calendar");
      this.lecture = ((TimetableAccess)getApplication()).getTimetable().getLecture(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY));
      if(lecture == null)
      {
         this.finish();
      }
      // Check if this is lecture creation or edit
      if (lecture != null)
      {
         
         if (lecture.getType().equals("Lecture")) {
            this.findViewById(R.id.lecture_title).setBackgroundResource(R.drawable.background_scheme1_secondary);
         }
         else if (lecture.getType().equals("Lab/Practical"))
         {
            this.findViewById(R.id.lecture_title).setBackgroundResource(R.drawable.background_scheme1_tertiary);
         }
         else {
            this.findViewById(R.id.lecture_title).setBackgroundResource(R.drawable.background_scheme1_tertiary);
         }
         
         ((TextView)this.findViewById(R.id.lecture_title)).setText(this.lecture.getSubject().getName() + "\n" + this.lecture.getSubject().getUnitCode());
         String type = this.lecture.getType();
         if (type.equals("Lecture") || type.equals("Lab/Practical"))
         {
            ((TextView)this.findViewById(R.id.lecture_title)).append("\n" + type);
         }
         
         ((Button)this.findViewById(R.id.addNotesButton)).setVisibility(View.VISIBLE);
         
         //Checks if lecture has notes attached ****
         List<Note> n = new ArrayList<Note>();
         ArrayList<CharSequence> csList = new ArrayList<CharSequence>();
         Boolean found = false;
         n = ((TimetableAccess)getApplication()).getTimetable().getNotes();
         int size = n.size();
         if(n != null)
         {
            for(int i = 0; i < size; i++)
            {
               if(n.get(i).getSubject().equals(lecture.getSubject()))
                  found = true;
                  csList.add(n.get(i).getName());
            }
         } 
         
         if(found)
         {
            ((Button)this.findViewById(R.id.openNotesButton)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_notes_label)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_notes_list)).setVisibility(View.VISIBLE);
            String notesList = (String)csList.get(0);
            for (int i = 1; i < size; i++)
            {
               notesList += "\n" + csList.get(i);
            }            
            ((TextView)this.findViewById(R.id.lecture_notes_list)).setText(notesList);
         }
            
         
         if (!this.lecture.getLecturer().equals(""))
         {
            ((TextView)this.findViewById(R.id.lecture_unit_director_label)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_unit_director)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_unit_director)).setText(this.lecture.getLecturer());
         }
         
         if (!this.lecture.getPlace().getShortAddress().equals(""))
         {
            ((TextView)this.findViewById(R.id.lecture_shortaddresslabel)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_shortaddress)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_shortaddress)).setText(this.lecture.getPlace().getShortAddress());
         }
         
         if (!this.lecture.getPlace().getRoom().equals(""))
         {
            ((TextView)this.findViewById(R.id.lecture_addresslabel)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_address_1)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_address_1)).setText(this.lecture.getPlace().getRoom());
         }
         
         if (!this.lecture.getPlace().getBuilding().equals(""))
         {
            ((TextView)this.findViewById(R.id.lecture_address_2)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_address_2)).setText(this.lecture.getPlace().getBuilding());
         }
         
         if (!this.lecture.getPlace().getPostcode().equals(""))
         {
            ((TextView)this.findViewById(R.id.lecture_address_3)).setVisibility(View.VISIBLE);
            ((TextView)this.findViewById(R.id.lecture_address_3)).setText(this.lecture.getPlace().getPostcode());
         }
      
         // Display progress stats
         ArrayList<Integer> lectureIds = new ArrayList<Integer>();
         for (Lecture l : ((TimetableAccess)this.getApplication()).getTimetable().getLectures(lecture.getSubject())) {
            lectureIds.add((int) l.getID());
         }
            LinearLayout attendanceLayout = (LinearLayout) findViewById(R.id.lecture_attendance_layout);

            Timetable timetable = ((TimetableAccess) this.getApplication())
                    .getTimetable();
            List<Integer> lectureAttendances = new ArrayList<Integer>();
            for (int id : lectureIds)
                {
                lectureAttendances.add(timetable.getLectureById(id)
                        .getAttendance());
                }

            View view = getLayoutInflater().inflate(R.layout.progress_view,
                    attendanceLayout, false);
            MyTimetableProgress.populateProgressView(view, lecture.getSubject()
                    .getName(), (int) MyTimetableProgress
                    .percentAttendance(lectureAttendances), lectureAttendances
                    .size(), MyTimetableProgress.attended(lectureAttendances),
                    MyTimetableProgress.missed(lectureAttendances));
            view.setOnClickListener(this);
            attendanceLayout.addView(view);

            /*
             * FragmentTransaction fragmentTransaction =
             * getFragmentManager().beginTransaction(); Bundle data = new
             * Bundle(); ProgressFragment fragment = new ProgressFragment();
             * data.putSerializable("Subject", lecture.getSubject().getName());
             * data.putSerializable("LectureIds", lectureIds);
             * fragment.setArguments(data);
             * fragmentTransaction.add(R.id.lecture_attendance_layout, fragment,
             * lecture.getSubject().getName()); fragmentTransaction.commit();
             */
      }
   }
   
   @Override
   protected void onPause()
   {
      super.onPause();
      WidgetUpdater.updateWidget(this);
   }
   
 
   //Allows user to browse their files for lecture notes
   public void startViewFiles(View v)
   {
      Intent intent = new Intent(this, FileChooser.class);
      startActivityForResult(intent, 1);
   }
   
   //Receives the path of the document to be associated with a lecture
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      if(resultCode != 0)
      {
         DialogFactory.getDialog(R.string.DialogAddNotes, this, 3, this).show();
         if (data.getExtras().containsKey("path info"))
            path = data.getStringExtra("path info");
         Toast.makeText(this, path, Toast.LENGTH_LONG).show();
      }
      
   }
   
   //Opens dialog with list of applicable notes, choose the correct file and it opens ****
   public void openNotes(View v)
   {
      List<Note> n = new ArrayList<Note>();
      final List<Note> choice = new ArrayList<Note>();
      Context context = this;
      AlertDialog.Builder files = new AlertDialog.Builder(context);
      ArrayList<CharSequence> csList = new ArrayList<CharSequence>();
      int size;
      
      n = ((TimetableAccess)getApplication()).getTimetable().getNotes();
      size = n.size();
      
      for(int i = 0; i < size; i++)
      {
         if (n.get(i).getSubject() == lecture.getSubject())
         {
            csList.add(n.get(i).getName());
            choice.add(n.get(i));
         }
      }
          
      CharSequence[] cs = csList.toArray(new CharSequence[csList.size()]);
      
      files.setTitle("Choose a file");
      files.setItems(cs, new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(choice.get(which).getFilePath()));
            intent.setType("application/pdf");
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities.size() > 0)
            {
              startActivity(intent);
            } else {
               Toast.makeText(getParent(), "Error opening file", Toast.LENGTH_LONG).show();
            }
         }
      });
      
      files.show();
      
   }
   
   /**
    * Modify this lecture
    */
   private void editLecture()
   {
      Intent intentEdit = new Intent(this, EditLecture.class);
      intentEdit.putExtra("Calendar", cal);      
      startActivity(intentEdit);
   }
   
   public void onClick(View v) {
      MyTimetableProgress.showHideStats(v);
      ((ScrollView) findViewById(R.id.lecture_page_scroll_view)).postDelayed(
            new Runnable() {
               @Override
               public void run() {
                  ((ScrollView) findViewById(R.id.lecture_page_scroll_view))
                        .fullScroll(ScrollView.FOCUS_DOWN);
               }
            }, 500);
   }
   
   @Override
   public void onDialogPositiveClick(int id)
   {
      // Check which dialog called this
      if (id == 0)
      {
         editLecture();
      }
      else if (id == 3)
      {
         //Saves lecture notes in table
         ((TimetableAccess)getApplication()).getTimetable().addNote(lecture.getSubject(), cal, path);
         Toast.makeText(this, "Lecture Note Added", Toast.LENGTH_LONG).show();
         this.onResume();
      }   
   }


   @Override
   public void onDialogNegativeClick(int id)
   {
   }
   
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:
             // app icon in action bar clicked; go home
             Intent intentHome = new Intent(this, DaySelector.class);
             intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intentHome);
             return true;

         case R.id.menu_settings:
           Intent intent = new Intent(this, MyTimetableSettings.class);
           startActivity(intent);
           return true;
           
         case R.id.edit_lecture:
            if(lecture.isOriginal())
            {
               DialogFactory.getDialog(R.string.DialogEditWarning , this, 0, this).show();
            }
            else
            {
               editLecture();
            }
            return true;
           
         default:
             return super.onOptionsItemSelected(item);
      }
   }
}
