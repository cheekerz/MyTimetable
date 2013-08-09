package com.px.MyTimetable.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class MyTimetableSettings extends Activity
{   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      
      // Display the fragment as the main content.
      getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
   }
         
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:            
            this.finish();
            return true;
   
         default:
            return super.onOptionsItemSelected(item);
      }
   }
}
