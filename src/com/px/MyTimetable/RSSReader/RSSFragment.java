package com.px.MyTimetable.RSSReader;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.px.MyTimetable.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

// Fragment containing title of feed and list of news items
public class RSSFragment extends Fragment {

   private static final int MAX_NUM_ITEMS = 20;
   private RSSFeed feed = null;
   private String feedUrl = null;
   private View fragmentView;
   private Activity activity;
   private boolean listShown;
   private View progressContainer;
   private View listContainer;
   private int numItems;
   private List<RSSItem> items = new Vector<RSSItem>(0);
	
   public static RSSFragment newInstance(String feedUrl) {
      RSSFragment f = new RSSFragment();
      
      Bundle args = new Bundle();
      args.putString("feedUrl", feedUrl);
      args.putInt("numItems", MAX_NUM_ITEMS);
      args.putBoolean("titleVisible", true);
      f.setArguments(args);
      
      return f;
   }
   
   public static RSSFragment newInstance(String feedUrl, int numItems) {
      RSSFragment f = new RSSFragment();
      
      Bundle args = new Bundle();
      args.putString("feedUrl", feedUrl);
      args.putInt("numItems", numItems);
      args.putBoolean("titleVisible", false);
      f.setArguments(args);
      
      return f;
   }
	
	// When the fragment is first displayed the layout defined in rss_news_fragment is shown which sets the
	// loadingContainer to visible and does not yet show the listContainer
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	   fragmentView = inflater.inflate(R.layout.rss_news_fragment, container, false);
      listContainer =  fragmentView.findViewById(R.id.listContainer);
      progressContainer = fragmentView.findViewById(R.id.loadingContainer);
      if (getArguments().getBoolean("titleVisible") == false) {
         fragmentView.findViewById(R.id.feedtitle).setVisibility(View.GONE);
         fragmentView.findViewById(R.id.feedtitle).invalidate();
      }
      listShown = false;
      numItems = getArguments().getInt("numItems");
      feedUrl = getArguments().getString("feedUrl");
      GetRSSDataTask task = new GetRSSDataTask();
      task.execute(feedUrl);
      return fragmentView;
	}
	
	// When the fragment is attached to an activity the feed is loaded in the background using the
	// given url
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		setRetainInstance(true);
	}
	
	// Asynchronous task loads the feed in the background so the user does not have to wait
	// and so the UI stays responsive
	private class GetRSSDataTask extends AsyncTask<String, Void, List<RSSItem> > {
	   @Override
      protected List<RSSItem> doInBackground(String... urls) {
	      // Load url and get news items
	      try {
	         feed = getFeed(urls[0]);
	         for(int i = 0; i < feed.getItemCount() && i < numItems; i++)
	         {
	            items.add(feed.getItem(i));
	         }
	         return items;
	            
         } catch (Exception e) {
         	e.printStackTrace();
         }
	         
         return null;
	   }
	   
	   @Override
	   protected void onPostExecute(List<RSSItem> result) {
	      updateDisplay(); // Populate list with news items
	      showList(true); // Shows the list
	   }
	}
	
	/*
	 * Uses a parser to retrieve the news feed found at the given url and
    * returns the result
	 */
	private RSSFeed getFeed(String urlToRSSFeed) {
		try {
			URL url = new URL(urlToRSSFeed);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			RSSHandler handler = new RSSHandler();
			InputSource is = new InputSource(url.openStream());
			
			xmlReader.setContentHandler(handler);
			xmlReader.parse(is);
			
			return handler.getFeed();
		}
		catch (Exception e) {
         Log.e("MyTimetable", "Exception", e);
			return null;
		}
	}
	
	/*
	 * Populates the view components defined in rss_news_feed with the news items
	 * and attaches an adapter to the list so that when an item is clicked an
	 * activity is launched that displays more detail about the item
	 */
	public void updateDisplay() {
		TextView feedtitle = (TextView) fragmentView.findViewById(R.id.feedtitle);
		ListView itemlist = (ListView) fragmentView.findViewById(R.id.itemlist);
		
		if (feed == null) {
			feedtitle.setText("No RSS Feed Available");
			return;
		}
		feedtitle.setText(feed.getTitle());
		
		ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(activity, R.layout.news_list_item, items);
		itemlist.setAdapter(adapter);
		itemlist.setSelection(0);
		itemlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent itemIntent = new Intent(activity, RSSDescription.class);
				
				Bundle b = new Bundle();
				b.putString("title", feed.getItem(position).getTitle());
				b.putString("description", feed.getItem(position).getDescription());
				b.putString("link", feed.getItem(position).getLink());
				b.putString("pubDate", feed.getItem(position).getPubDate());
				
				itemIntent.putExtra("android.intent.extra.INTENT", b);
				
				startActivity(itemIntent);
			}
			
		});
	}
	
	/*
	 * Toggles whether the list is shown
	 * If true the loadingContainer is hidden and the list shown
	 * If false the listContainer is hidden and the loadingContainer is shown
	 */
	public void showList(boolean show)
	{
      if (listShown == show)
      {
             return;
      }
      listShown = show;
      if (show)
      {
          progressContainer.setVisibility(View.GONE);
          listContainer.setVisibility(View.VISIBLE);
      }
      else
      {
          progressContainer.setVisibility(View.VISIBLE);
          listContainer.setVisibility(View.INVISIBLE);
      }
   }
}
