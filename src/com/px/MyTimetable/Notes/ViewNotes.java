package com.px.MyTimetable.Notes;

import java.util.ArrayList;
import java.util.List;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Entities.Note;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ViewNotes extends Activity
{
   private Spinner s;
   private Spinner n;
   private Button open;
  
   protected void onResume()
   {
      super.onResume();
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      setContentView(R.layout.notes_view);
      
      List<Note> lNotes = new ArrayList<Note>();
      ArrayAdapter<String> subjectArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
      final ArrayAdapter<String> noteArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
      int size;
      
      //Initiates Button and Spinners
      open = (Button)findViewById(R.id.bnOpen);
      s = (Spinner)findViewById(R.id.spSubject);
      n = (Spinner)findViewById(R.id.spNote);
      
      //Fills the list with all the Note objects saved in the database
      lNotes = ((TimetableAccess)getApplication()).getTimetable().getNotes();
      size = lNotes.size();
      subjectArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      
      //Pairs the spinners with the array adapters
      s.setAdapter(subjectArray);
      n.setAdapter(noteArray);
      s.setPrompt("Choose Subject");
      n.setPrompt("Choose Notes");
      
      //Fills the first spinner with all the possible subjects
      for(int i = 0; i < size; i++)
         subjectArray.add(lNotes.get(i).getSubject().getName());
      
      //Alerts the program that the spinner content has been changed, and updates the spinner
      subjectArray.notifyDataSetChanged();
      
      //Method to fill the second spinner based on the selected item in the first spinner
      s.setOnItemSelectedListener(new OnItemSelectedListener() {
         
         public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
            
            List<Note> lNotes = new ArrayList<Note>();
            lNotes = ((TimetableAccess)getApplication()).getTimetable().getNotes();
            noteArray.clear();
            
            
            //Goes through the list of Notes and matches tany selected subject in the first spinner with the corresponding Note/s
            for(int i = 0; i < lNotes.size(); i++) {
               if(lNotes.get(i).getSubject().getName().equals(s.getSelectedItem()))
               {
                  //Adds the name of the Note file to the second spinner
                     noteArray.add(lNotes.get(i).getName());
               }
            }
            
            //Updates the second spinner
            noteArray.notifyDataSetChanged();
         }    
         
         public void onNothingSelected(AdapterView<?> arg0) {                
         }
      });
      
      open.setOnClickListener(new View.OnClickListener() {
         public void onClick(View view) {
            List<Note> lNotes = new ArrayList<Note>();
            lNotes = ((TimetableAccess)getApplication()).getTimetable().getNotes();
            
            for(int i = 0; i < lNotes.size(); i++) {
               //Matches the Subject in the second spinner with the corresponding Note/s
               if(lNotes.get(i).getName().equals(n.getSelectedItem())){
                  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lNotes.get(i).getFilePath()));
                  intent.setType("application/pdf");
                  PackageManager pm = getPackageManager();
                  List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                  
                  //Opens the file or shows error message if the phone does not have a PDF viewer
                  if (activities.size() > 0)
                  {
                    startActivity(intent);
                  } else {
                     Toast.makeText(getParent(), "Error opening file", Toast.LENGTH_LONG).show();
                  }
               }
            }
            
         }
      });
      
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_notes, menu);
      return true;
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
            
          case android.R.id.home:
             this.finish();
             return true;
            
          default:
              return super.onOptionsItemSelected(item);
       }
   }
}
