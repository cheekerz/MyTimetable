package com.px.MyTimetable.Main;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.DialogFactory;
import com.px.MyTimetable.Dialogs.DialogListener;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Notes.ViewNotes;
import com.px.MyTimetable.Progress.MyTimetableProgress;
import com.px.MyTimetable.RSSReader.RSSFragment;
import com.px.MyTimetable.RSSReader.RSSReader;
import com.px.MyTimetable.Settings.MyTimetableSettings;
import com.px.MyTimetable.TimetablePage.DayFragment;
import com.px.MyTimetable.TimetablePage.DaySelector;
import com.px.MyTimetable.TimetablePage.LecturePage;

public class MainActivity extends Activity implements OnClickListener, DialogListener
{
   Calendar nextLectureCal;
     
   @Override
   protected void onResume()
   {
      super.onResume();
      // Uses activity_main.xml to display home screen
      setContentView(R.layout.activity_main);
      
      // Display next lecture
      Lecture nextLecture = ((TimetableAccess)getApplication()).getTimetable().getNextLecture(Calendar.getInstance());
      
      if (nextLecture.getCalendar().get(Calendar.YEAR) != nextLecture.getCalendar().getActualMaximum(Calendar.YEAR))
      {
         this.nextLectureCal = (Calendar) nextLecture.getCalendar().clone();
         ((TextView) findViewById(R.id.nextLectureTitle)).setText(this.getString(R.string.NextLectureOn) + DayFragment.dayToString(this.nextLectureCal, Calendar.LONG));
         View timeSlot = findViewById(R.id.nextLectureTimeSlot);
         timeSlot.setOnClickListener(this);
         ((Button)timeSlot.findViewById(R.id.attendanceButton)).setOnClickListener(this);
         DayFragment.populateTimeSlot(timeSlot, nextLecture, nextLecture.getTimeSlot());
      }
      else 
      {
         ((TextView) findViewById(R.id.nextLectureTitle)).setText(this.getString(R.string.EndOfLectures));
         findViewById(R.id.nextLectureTimeSlot).setVisibility(View.GONE);
      }
	  
      // Display RSS Items
      RSSFragment newsFragment = RSSFragment.newInstance("http://www.bristol.ac.uk/news/news-feed.rss", 2);
      FragmentTransaction ft = getFragmentManager().beginTransaction();
      ft.add(R.id.home_news_frag_container, newsFragment);
      ft.commit();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_main, menu);
      return true;
   }
   
   @Override
   public void onClick(View v)
   {
      if (v.getTag().equals("slot")) {
         // Set calendar to this lectures time and send so this instance can be referenced when editing or deleting
         Intent intent = new Intent(this, LecturePage.class);
         Calendar timeSlotCal = (Calendar) this.nextLectureCal.clone();
         intent.putExtra("Calendar", timeSlotCal);      
         startActivity(intent);
         }
      else {
         Log.v("MainActivity", "onClick from attendancebutton");
         Lecture l = ((TimetableAccess)getApplication()).getTimetable().getLecture(this.nextLectureCal.get(Calendar.WEEK_OF_YEAR), this.nextLectureCal.get(Calendar.DAY_OF_WEEK), this.nextLectureCal.get(Calendar.HOUR_OF_DAY));
         if (l != null) {
            l.changeAttendance(((TimetableAccess)getApplication()).getTimetable().getWritableDatabase());
            v.setBackgroundColor(l.getAttendanceColor());
            v.invalidate();
         }
      }
   }
   
   /**
    *  Called when the timetable button is clicked, directs the user to  a screen where they can select a week
    * @param view
    */
   public void startDaySelector(View view)
   {
      Intent intent = new Intent(this, DaySelector.class);
      startActivity(intent);      
   }
   
   /**
    * Launches blackboard if it can be found otherwise link to play store
    * @param view
    */
   public void startBlackboard(View view)
   {
      final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
      // Get all installed apps on phone and try to find blackboard in list
      final List<ResolveInfo> pkgAppsList = this.getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, 0);
      String packageName = "com.blackboard.android";
      
      for (ResolveInfo item : pkgAppsList)
      {
         if (item.activityInfo.packageName.equals(packageName))
         { 
            // Blackboard is installed run it
            Intent intent = new Intent(Intent.ACTION_VIEW); 
            intent.addCategory(Intent.CATEGORY_LAUNCHER);  
            intent.setClassName(packageName, "com.blackboard.android.activity.SchoolListActivity");
            startActivity(intent); 
            return;
         }
      }
      
      // Blackboard wasn't found launch dialog to see if user wants to go to play store to download
      DialogFactory.getDialog(R.string.DialogBlackboard, this, 0, this).show();
   }
   /**
    * Runs Progress activity
    * @param view 
    */
   public void startProgress(View view)
   {
      Intent intent = new Intent(this, MyTimetableProgress.class);
      startActivity(intent);
   }
   
   /**
    * Runs ViewNotes activity
    * @param view 
    */
   public void startNotes(View view)
   {
      Intent intent = new Intent(this, ViewNotes.class);
      startActivity(intent);
   }
   
   /**
    * Runs RSS activity
    * @param view 
    */
   public void startRSSReader(View view)
   {
      Intent intent = new Intent(this, RSSReader.class);
      startActivity(intent);
   }

   @Override
   public void onDialogPositiveClick(int id)
   {
      if (id == 0)
      {
         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.blackboard.android")));
      }
   }

   @Override
   public void onDialogNegativeClick(int id)
   {
      // cancel was clicked do nothing
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
       // Handle item selection
       switch (item.getItemId())
       {
          case R.id.menu_settings:
            Intent intent = new Intent(this, MyTimetableSettings.class);
            startActivity(intent);
            return true;
            
          default:
              return super.onOptionsItemSelected(item);
       }
   }
}
