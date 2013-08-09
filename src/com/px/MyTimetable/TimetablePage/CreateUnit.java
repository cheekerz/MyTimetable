package com.px.MyTimetable.TimetablePage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;

public class CreateUnit extends Activity
{
   private String name;
   private String unitCode;
   private String department;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      setContentView(R.layout.activity_create_unit);     
      
      // Stop auto focus
      this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.lecture_page, menu);
      return true;
   }
   
   /**
    * Display saving options depending on whether this lecture is a singleton or part of a group of lectures
    * @param v
    */
   public void saveUnit (View v)
   {

      unitCode = ((EditText)this.findViewById(R.id.enter_unit_code)).getText().toString();
      name = ((EditText)this.findViewById(R.id.enter_subject_name)).getText().toString();
      department = ((EditText)this.findViewById(R.id.enter_department_name)).getText().toString();
      
      if(unitCode.equals("") || name.equals("") || department.equals(""))
      {
         Toast.makeText(this, R.string.MissingInformation, Toast.LENGTH_LONG).show();
      }
      else
      {
         ((TimetableAccess)getApplication()).getTimetable().addSubject(unitCode, name, department);
         
         Intent intent = this.getIntent();
         intent.putExtra("unitDetails", unitCode + " - " + name);
         this.setResult(1, intent);
         
         Toast.makeText(this, R.string.Saved, Toast.LENGTH_LONG).show();
         this.finish();
      }
      
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
           
         default:
             return super.onOptionsItemSelected(item);
      }
   }
}
