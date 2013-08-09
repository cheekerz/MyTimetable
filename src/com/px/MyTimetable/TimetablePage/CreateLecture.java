package com.px.MyTimetable.TimetablePage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Place;
import com.px.MyTimetable.Entities.Subject;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;
import com.px.MyTimetable.Widget.NextLectureWidgetProvider;

public class CreateLecture extends Activity
{
   private Calendar cal;
   private Subject subject = null;
   private Place place = null;
   private String type = null;
   private String lecturer = null;
   Spinner spinnerUnitDetails;
   Spinner spinnerEventTypes;
   Spinner spinnerPlaces;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      setContentView(R.layout.activity_create_lecture); 

      this.cal = (Calendar) this.getIntent().getSerializableExtra("Calendar");
      
      // Stop auto focus
      this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      
      this.createUnitDetailsSpinner();
      this.createEventTypesSpinner();
      this.createPlacesSpinner();
      
   }
   
   private void createUnitDetailsSpinner() {
      ArrayList<String> unitDetails = ((TimetableAccess)getApplication()).getTimetable().getUnitNames();
      unitDetails.add(0, "No selection");
      unitDetails.add("Create new unit");
      spinnerUnitDetails = newSpinner(unitDetails, R.id.spinner1);
      spinnerUnitDetails.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(temp.getText().equals("Create new unit"))
             {
                Intent createUnit = new Intent(getApplication(), CreateUnit.class);
                startActivityForResult(createUnit, 1);
             }
             else if(!temp.getText().equals("No selection"))
             {
                String unitCode = (String) temp.getText();
                String[] temp1 = unitCode.split("-");
                temp1[0] = temp1[0].trim();
                int index = ((TimetableAccess)getApplication()).getTimetable().getSubjectIndex(temp1[0]);
                subject = ((TimetableAccess)getApplication()).getTimetable().getSubject(index);
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
          }

      });

   }
   
   private void createEventTypesSpinner() {
      String [] eventTypesStrings = new String[]{"No selection", "Lecture","Lab/Practical","Other"};
      ArrayList<String> eventTypes = new ArrayList<String>(Arrays.asList(eventTypesStrings));
      spinnerEventTypes = newSpinner(eventTypes, R.id.spinner2);
      spinnerEventTypes.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(!temp.getText().equals("No selection"))
             {
                type = (String) temp.getText();
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
          }

      });
   }

   private void createPlacesSpinner() {
      ArrayList<String> places = ((TimetableAccess)getApplication()).getTimetable().getAddressDescriptions();
      places.add(0, "No selection");
      places.add("Create new location");
      spinnerPlaces = newSpinner(places, R.id.spinner3);
      spinnerPlaces.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(temp.getText().equals("Create new location"))
             {
                Intent createPlace = new Intent(getApplication(), CreatePlace.class);
                startActivityForResult(createPlace, 2);
             }
             else if(!temp.getText().equals("No selection"))
             {
                String shortDescription = (String) temp.getText();
                String[] temp1 = shortDescription.split("\n");
                temp1[0] = temp1[0].trim();
                int index = ((TimetableAccess)getApplication()).getTimetable().getPlaceIndex(temp1[1].trim(), temp1[2].trim(), temp1[3].trim());
                place = ((TimetableAccess)getApplication()).getTimetable().getPlace(index);
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
          }

      });
   }
   
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     if(requestCode == 1) {
        if (resultCode == 1) {
           this.createUnitDetailsSpinner();
           spinnerUnitDetails.setSelection(getIndex(spinnerUnitDetails, (String)data.getCharSequenceExtra("unitDetails")));
        }
        else {
           spinnerUnitDetails.setSelection(0);
        }
     }
     if(requestCode == 2) {
        if (resultCode == 1) {
           this.createPlacesSpinner();           
           spinnerPlaces.setSelection(getIndex(spinnerPlaces, (String)data.getCharSequenceExtra("placeDetails")));
        }
        else {
           spinnerPlaces.setSelection(0);
        }
     }
     
   }
   
   private int getIndex(Spinner spinner, String myString){

      int index = 0;

      for (int i=0;i<spinner.getCount();i++){
          if (spinner.getItemAtPosition(i).equals(myString)){
              index = i;
          }
      }
      return index;
   }
      
   @Override
   protected void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      
      savedInstanceState.putInt("spinnerUnitDetailsPosition", spinnerUnitDetails.getSelectedItemPosition());
      savedInstanceState.putInt("spinnerEventTypesPosition", spinnerEventTypes.getSelectedItemPosition());
      savedInstanceState.putInt("spinnerPlacesPosition", spinnerPlaces.getSelectedItemPosition());
      savedInstanceState.putCharSequence("lecturer", lecturer);  
   }
   
   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState) {
     super.onRestoreInstanceState(savedInstanceState);
     
     spinnerUnitDetails.setSelection(savedInstanceState.getInt("spinnerUnitDetailsPosition"));
     spinnerEventTypes.setSelection(savedInstanceState.getInt("spinnerEventTypesPosition"));
     spinnerPlaces.setSelection(savedInstanceState.getInt("spinnerPlacesPosition"));
     EditText lecturerTextBox = (EditText) findViewById(R.id.enter_unit_director);
     lecturerTextBox.setText(savedInstanceState.getCharSequence("lecturer"));
   }
  
   private Spinner newSpinner(ArrayList<String> items, int resourceId)
   {
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
      adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
      Spinner spinner = (Spinner)findViewById(resourceId);
      spinner.setAdapter(adapter);
      return spinner;
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.lecture_page, menu);
      return true;
   }
   
   @Override
   protected void onPause()
   {
      super.onPause();     
      
      Intent intent = new Intent(this, NextLectureWidgetProvider.class);
      intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
      // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
      // since it seems the onUpdate() is only fired on that:      
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, NextLectureWidgetProvider.class)));
      sendBroadcast(intent);
   }
   
   /**
    * Display saving options depending on whether this lecture is a singleton or part of a group of lectures
    * @param v
    */
   public void saveLecture (View v)
   {
//      name = ((EditText)this.findViewById(R.id.lecture_name)).getText().toString();
//      unitCode = ((EditText)this.findViewById(R.id.lecture_unitcode)).getText().toString();
//      type = ((EditText)this.findViewById(R.id6.lecture_type)).getText().toString();
//      shortAddress = ((EditText)this.findViewById(R.id.lecture_shortaddress)).getText().toString();
      //String address = ((EditText)this.findViewById(R.id.lecture_address)).getText().toString();

      lecturer = ((EditText)this.findViewById(R.id.enter_unit_director)).getText().toString();
      if(subject == null || place == null)
      {
         Toast.makeText(this, R.string.MissingInformation, Toast.LENGTH_LONG).show();
         return;
      }
      else
      {
         List<Integer> weeks = new ArrayList<Integer>();
         weeks.add(cal.get(Calendar.WEEK_OF_YEAR));
         ((TimetableAccess)getApplication()).getTimetable().addLecture(0, subject, place, cal, 1, weeks, subject, place, cal, 1, weeks, type, lecturer, 0);
      }
            
      Toast.makeText(this, R.string.Saved, Toast.LENGTH_LONG).show();
      this.finish();
   }
   
   public void resetInput(View v)
   {
      spinnerUnitDetails.setSelection(0);
      spinnerEventTypes.setSelection(0);
      spinnerPlaces.setSelection(0);
      subject = null;
      type = null;
      place = null;
      ((EditText)this.findViewById(R.id.enter_unit_director)).setText("");
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
