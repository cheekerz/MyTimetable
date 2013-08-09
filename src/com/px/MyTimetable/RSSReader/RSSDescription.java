package com.px.MyTimetable.RSSReader;

import com.px.MyTimetable.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class RSSDescription extends Activity {
	
   String description = null;
   String title = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      this.getActionBar().setHomeButtonEnabled(true);
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.rss_description);
		
		Intent intent = getIntent();
		if (intent != null) {
			Bundle b = intent.getBundleExtra("android.intent.extra.INTENT");
			if (b == null) {
			   Log.e("MyTimetable", "Empty Bundle");
				description = "No description found";
			}
			else {
			   title =  b.getString("title");
				description = "";
			
				if (b.getString("pubdate") != null) {
					description += b.getString("pubdate") + "\n\n";
				}
				description += b.getString("description").replace('\n',' ')
						+ "\n\nMore information:\n" + b.getString("link");
			}
		}
		else {
		   title = "News Story";
			description = "Information Not Found.";
		}
		TextView titleView = (TextView) findViewById(R.id.rss_description_title);
		TextView db= (TextView) findViewById(R.id.story);
		titleView.setText(title);
		db.setText(description);
	}
   
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:
             // app icon in action bar clicked; go home
             Intent intentHome = new Intent(this, RSSReader.class);
             intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intentHome);
             return true;
   
         default:
             return super.onOptionsItemSelected(item);
     }
   }
	
}
