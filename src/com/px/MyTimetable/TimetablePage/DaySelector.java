package com.px.MyTimetable.TimetablePage;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.DialogFactory;
import com.px.MyTimetable.Dialogs.DialogListener;
import com.px.MyTimetable.Main.MainActivity;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;

public class DaySelector extends FragmentActivity implements DatePickerDialog.OnDateSetListener, DialogListener
{   
   // Due to bug in android 4.0+ that still isn't fixed, ondateset is called twice when clicking done in date picker
   // this variable stops the second call doing anything
   private boolean opened = false;
   private final int millisecondsInDay = 86400000;
   private ViewPager pager;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_week);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      this.pager = (ViewPager) findViewById(R.id.pager);
      FragmentManager fm = getSupportFragmentManager();      
      MyFragmentStatePagerAdapter pagerAdapter = new MyFragmentStatePagerAdapter(fm, ((TimetableAccess)getApplication()).getTimetable());
      this.pager.setAdapter(pagerAdapter);
      this.setDay(Calendar.getInstance());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_day_fragment, menu);
      return true;
   }
   
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:
             // app icon in action bar clicked; go home
             Intent intentHome = new Intent(this, MainActivity.class);
             intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intentHome);
             return true;
             
         case R.id.menu_jump_to:            
            this.opened = false;
            showDatePickerDialog();
            return true;

         case R.id.menu_today:
            this.setDay(Calendar.getInstance());
            return true;
            
         case R.id.menu_settings:
           Intent intent = new Intent(this, MyTimetableSettings.class);
           startActivity(intent);
           return true;
           
         default:
             return super.onOptionsItemSelected(item);
     }
   }

   /**
    * Displays date picker dialog set to the current day
    */
   private void showDatePickerDialog()
   {
      Calendar cal = Calendar.getInstance();            
      new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
   }
   
   /**
    * Sets the current page to the date input
    * @param year 
    * @param month 
    * @param day of month
    */
   private void goToDay(int year, int month, int day)
   {
      Calendar cal = Calendar.getInstance();
      cal.set(year, month, day);
      this.setDay(cal);
      this.opened = true;    
   }
   
   /**
    * Converts a calendar into a page number for the timetable and sets the page to that number
    * @param cal Calendar to use
    */
   private void setDay(Calendar cal)
   {
      this.pager.setCurrentItem((int)((cal.getTimeInMillis() - ((TimetableAccess)getApplication()).getTimetable().getStartCal().getTimeInMillis())/millisecondsInDay));      
   }
   
   /**
    * Checks selected date is between the start and end dates of the timetable
    * If so jumps to that day, if not displays dialog where user can pick new date or cancel
    */
   @Override
   public void onDateSet(DatePicker view, int year, int month, int day)
   {            
      if (!opened)
      {
         // Find which year of the timetable the selected date is
         if (((TimetableAccess)getApplication()).getTimetable().getStartCal().get(Calendar.YEAR) == year)
         {
            // Check if month is after start date
            if (((TimetableAccess)getApplication()).getTimetable().getStartCal().get(Calendar.MONTH) < month)
            {
               goToDay(year, month, day);
               return;
            }
            else if (((TimetableAccess)getApplication()).getTimetable().getStartCal().get(Calendar.MONTH) == month)
            {
               // Check day is after start day
               if (((TimetableAccess)getApplication()).getTimetable().getStartCal().get(Calendar.DAY_OF_MONTH) <= day)
               {
                  goToDay(year, month, day);
                  return;               
               }
            }
         }
         else if (((TimetableAccess)getApplication()).getTimetable().getEndCal().get(Calendar.YEAR) == year)
         {
            // Check if month is before end date
            if (((TimetableAccess)getApplication()).getTimetable().getEndCal().get(Calendar.MONTH) > month)
            {
               goToDay(year, month, day);
               return;
            }
            else if (((TimetableAccess)getApplication()).getTimetable().getEndCal().get(Calendar.MONTH) == month)
            {
               // Check day is before end day
               if (((TimetableAccess)getApplication()).getTimetable().getEndCal().get(Calendar.DAY_OF_MONTH) >= day)
               {
                  goToDay(year, month, day);
                  return;               
               }
            }
         }
         
         // Date was out of range alert user
         DialogFactory.getDialog(R.string.DateOutOfRange, this, 0, this).show();
         // don't run this again when method is called again by bug
         this.opened = true;
      }
   }

   @Override
   public void onDialogPositiveClick(int id)
   {      
      if (id == 0)
      {
         // Show date picker dialog again, resetting flag
         this.opened = false;
         showDatePickerDialog();
      }
   }

   @Override
   public void onDialogNegativeClick(int id)
   {      
   }
}
