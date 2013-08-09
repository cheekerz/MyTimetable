package com.px.MyTimetable.RSSReader;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Main.MainActivity;
import com.px.MyTimetable.Settings.MyTimetableSettings;

public class RSSReader extends Activity
{   
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.activity_rss_reader);

		RSSFragment initialFragment = RSSFragment.newInstance("http://www.bristol.ac.uk/news/news-feed.rss");
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, initialFragment);
		ft.commit();

		MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(this, R.layout.spinner_item_text, getResources().getStringArray(R.array.rss_categorys));

		OnNavigationListener onNavigationListener = new OnNavigationListener()
		{
			String[] strings = getResources().getStringArray(R.array.rss_urls);

			@Override
			public boolean onNavigationItemSelected(int position, long itemId)
			{
				RSSFragment fragment = RSSFragment.newInstance(strings[position]);

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, fragment);
				ft.commit();
				return true;
			}
		};

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(spinnerAdapter, onNavigationListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_rssreader, menu);
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
			
          case R.id.menu_settings:
            Intent intent = new Intent(this, MyTimetableSettings.class);
            startActivity(intent);
            return true;

			default:
			   return super.onOptionsItemSelected(item);
		}
	}
}