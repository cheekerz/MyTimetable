package com.px.MyTimetable.TimetablePage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.px.MyTimetable.Main.TimetableAccess;

public class CreatePlace extends Activity
{
   private String shortAddress;
   private String room;
   private String building;
   private String postcode;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      setContentView(R.layout.activity_create_place);      
      
      // Stop auto focus
      this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      
      ArrayList<String> shortAddressNames = ((TimetableAccess)getApplication()).getTimetable().getShortAddressNames();
      shortAddressNames.add(0, "No selection");
      Spinner spinnerShortAddress = newSpinner(shortAddressNames, R.id.create_place_spinner1);
      spinnerShortAddress.setPrompt("Select short address");
      spinnerShortAddress.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(!temp.getText().equals("No selection"))
             {
                shortAddress = (String) temp.getText();
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
           // TODO Auto-generated method stub
         
          }

      });
      
      ArrayList<String> roomNames = ((TimetableAccess)getApplication()).getTimetable().getRoomNames();
      roomNames.add(0, "No selection");
      Spinner spinnerRoom = newSpinner(roomNames, R.id.create_place_spinner2);
      spinnerRoom.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(!temp.getText().equals("No selection"))
             {
                room = (String) temp.getText();
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
           // TODO Auto-generated method stub
         
          }

      });
      
      ArrayList<String> buildingNames = ((TimetableAccess)getApplication()).getTimetable().getBuildingNames();
      buildingNames.add(0, "No selection");
      Spinner spinnerBuilding = newSpinner(buildingNames, R.id.create_place_spinner3);
      spinnerBuilding.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(!temp.getText().equals("No selection"))
             {
                building = (String) temp.getText();
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
           // TODO Auto-generated method stub
         
          }

      });
      
      ArrayList<String> postcodeNames = ((TimetableAccess)getApplication()).getTimetable().getPostcodeNames();
      postcodeNames.add(0, "No selection");
      Spinner spinnerPostcode = newSpinner(postcodeNames, R.id.create_place_spinner4);
      spinnerPostcode.setOnItemSelectedListener(new OnItemSelectedListener()
      {

          public void onItemSelected(AdapterView<?> arg0, View arg1,
            int arg2, long arg3) {
             TextView temp = (TextView)arg1;
             if(!temp.getText().equals("No selection"))
             {
                postcode = (String) temp.getText();
             }
          }
         
          public void onNothingSelected(AdapterView<?> arg0) {
           // TODO Auto-generated method stub
         
          }
      });
      
   }

   private Spinner newSpinner(ArrayList<String> items, int resourceId)
   {
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

   /**
    * Display saving options depending on whether this lecture is a singleton or part of a group of lectures
    * @param v
    */
   public void saveLecture (View v)
   {
      if(shortAddress == null)
      {
         shortAddress = ((EditText)this.findViewById(R.id.enter_short_address)).getText().toString();
      }
      if(room == null)
      {
         room = ((EditText)this.findViewById(R.id.enter_room)).getText().toString();
      }
      if(building == null)
      {
         building = ((EditText)this.findViewById(R.id.enter_building)).getText().toString();
      }
      if(postcode == null)
      {
         postcode = ((EditText)this.findViewById(R.id.enter_postcode)).getText().toString();
      }
      ((TimetableAccess)getApplication()).getTimetable().addPlace(shortAddress, room, building, postcode);
      
      Intent intent = this.getIntent();
      intent.putExtra("placeDetails", shortAddress + "\n" + room + "\n" + building + "\n" + postcode);
      this.setResult(1, intent);
      
      Toast.makeText(this, R.string.Saved, Toast.LENGTH_LONG).show();
      this.finish();
   }

}
