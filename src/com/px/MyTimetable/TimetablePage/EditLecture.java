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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.DialogFactory;
import com.px.MyTimetable.Dialogs.MultipleDialogFactory;
import com.px.MyTimetable.Dialogs.MultipleDialogListener;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Place;
import com.px.MyTimetable.Entities.Subject;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;
import com.px.MyTimetable.Widget.NextLectureWidgetProvider;

public class EditLecture extends Activity implements MultipleDialogListener
{
   private Lecture lecture;
   private Calendar cal;
   private Subject subject = null;
   private Place place = null;
   private String type = null;
   private String lecturer = null;
   Spinner spinnerEventTypes;
   Spinner spinnerPlaces;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public void onResume()
   {
      super.onResume();
      setContentView(R.layout.activity_edit_lecture); 

      // Stop auto focus
      this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      
      // try and find this lecture in timetable 
      this.cal = (Calendar) this.getIntent().getSerializableExtra("Calendar");
      this.lecture = ((TimetableAccess)getApplication()).getTimetable().getLecture(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.HOUR_OF_DAY));
      
      // Check if lecture has been deleted
      if(lecture == null)
      {
         this.finish();
      }
      
      this.subject = lecture.getSubject();

      this.createEventTypesSpinner();
      this.createPlacesSpinner();
      
      // Show delete button as there is a lecture to delete
      ((Button)this.findViewById(R.id.deleteButton)).setVisibility(View.VISIBLE);
      if(lecture.isEdited())
      {
         ((Button)this.findViewById(R.id.restoreButton)).setVisibility(View.VISIBLE); // TODO : Check if lecture can be restored
      }
         // Populate text fields with lecture data
      ((TextView)this.findViewById(R.id.lecture_unit_code)).setText(this.subject.getUnitCode() + " - ");
      ((TextView)this.findViewById(R.id.lecture_unit_name)).setText(this.subject.getName());
      ((TextView)this.findViewById(R.id.enter_unit_director)).setText(this.lecture.getLecturer());
//      spinnerUnitDetails.setSelection(getIndex(spinnerUnitDetails, lecture.getSubject().getUnitCode() + " - " + lecture.getSubject().getName()));
      spinnerEventTypes.setSelection(getIndex(spinnerEventTypes, lecture.getType()));
      spinnerPlaces.setSelection(getIndex(
            spinnerPlaces,
            lecture.getPlace().getShortAddress() + "\n" + 
            lecture.getPlace().getRoom() + "\n" + 
            lecture.getPlace().getBuilding() + "\n" +
            lecture.getPlace().getPostcode()));
      
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
                startActivityForResult(createPlace, 1);
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
   
   private Spinner newSpinner(ArrayList<String> items, int resourceId)
   {
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
      adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
      Spinner spinner = (Spinner)findViewById(resourceId);
      spinner.setAdapter(adapter);
      return spinner;
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     
     if(requestCode == 1) {
        if (resultCode == 1) {
           this.createPlacesSpinner();           
           spinnerPlaces.setSelection(getIndex(spinnerPlaces, (String)data.getCharSequenceExtra("placeDetails")));
        }
        else {
           spinnerPlaces.setSelection(0);
        }
     }
     
   }
      
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_main, menu);
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
    * Display deletion options depending on whether this lecture is a singleton or part of a group of lectures
    * @param v
    */
   public void deleteLecture (View v)
   {
      if (lecture.getOcurrences() > 1)
      {
         MultipleDialogFactory.getDialog(R.string.DialogDeleteMultipleLecture, this, 1, this).show();
      }
      else
      {
         DialogFactory.getDialog(R.string.DialogDeleteLecture, this, 1, this).show();
      }
   }
   
   /**
    * Display restore options
    * @param v
    */
   public void restoreLecture (View v)
   {
      DialogFactory.getDialog(R.string.DialogRestoreLecture, this, 2, this).show();
   }
   
   /**
    * Display saving options depending on whether this lecture is a singleton or part of a group of lectures
    * @param v
    */
   public void saveEdits(View v)
   {

      lecturer = ((EditText)this.findViewById(R.id.enter_unit_director)).getText().toString();
      if (true)//lecture.getOcurrences() > 1)
      {
         MultipleDialogFactory.getDialog(R.string.DialogSaveLecture, this, 0, this).show();
         return;
      }
//      else
//      {
//         editLecture(this.lecture);
//      }
//      
//      Toast.makeText(this, R.string.Saved, Toast.LENGTH_LONG).show();
   }

   /**
    * Modify this lecture with values from text fields
    */
   private void editLecture(Lecture lectureToEdit)
   {
      if(lecturer != null)
      {
         lectureToEdit.setLecturer(((TimetableAccess)getApplication()).getTimetable().getWritableDatabase(), lecturer);
      }
//      if(subject != null && !subject.equals(lectureToEdit.getSubject()))
//      {
//         lectureToEdit.setSubject(((TimetableAccess)getApplication()).getTimetable().getWritableDatabase(), subject);
//      }
      if(type != null && !type.equals(lectureToEdit.getType()))
      {
         lectureToEdit.setType(type);
      }
      if(place != null && !place.equals(lectureToEdit.getPlace()))
      {
         lectureToEdit.setPlace(((TimetableAccess)getApplication()).getTimetable().getWritableDatabase(), place);
      }
      this.finish();   
   }
   
   private void editLectureSeries()
   {
      List<Lecture> lectures = ((TimetableAccess)getApplication()).getTimetable().getLectureSeries(lecture.getSubject(), lecture.getType());
      for(Lecture l : lectures)
      {
         editLecture(l);
      }
   }
   
   @Override
   public void onDialogPositiveClick(int id)
   {
      // Check which dialog called this
      if (id == 0)
      {
         // Edit all instances of lecture
         editLectureSeries();
      }
      else if (id == 1)
      {
         // Delete all instances of lecture
         long lectureId = lecture.getID();
         int index = ((TimetableAccess)getApplication()).getTimetable().getLecturePositionFromID(lectureId);
         ((TimetableAccess)getApplication()).getTimetable().deleteLecture(index);
//      TODO   ((TimetableAccess)getApplication()).getTimetable().deleteLecture(lecture);
         this.finish();
      }
      else if (id == 2)
      {
         // Restore to original
         lecture.restoreLecture(((TimetableAccess)getApplication()).getTimetable().getWritableDatabase());
         this.finish();
      }
   }

   @Override
   public void onDialogNeutralClick(int id)
   {
      // Check which dialog called this
      if (id == 0)
      {
         editLecture(this.lecture);
         this.finish();
      }
      else if (id == 1)
      {
         long lectureId = lecture.getID();
         int index = ((TimetableAccess)getApplication()).getTimetable().getLecturePositionFromID(lectureId);
         ((TimetableAccess)getApplication()).getTimetable().deleteLecture(index);
         this.finish();
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
           
         default:
             return super.onOptionsItemSelected(item);
      }
   }
}
