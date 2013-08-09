package com.px.MyTimetable.RSSReader;

import java.util.List;
import java.util.Vector;

public class RSSFeed {

	private String title = "";
	private String description= "";
	private String pubDate = ""; // can use date to perform intelligent updating
	private int itemCount = 0;
	private List<RSSItem> items;
	
	public RSSFeed() {
		items = new Vector<RSSItem>(0); // May use ArrayList here
	}
	
	public int addItem(RSSItem item) {
		items.add(item);
		itemCount++;
		return itemCount;
	}
	
	public RSSItem getItem(int index) {
		return items.get(index);
	}
	
	public List<RSSItem> getAllItems() {
		return items;
	}
	
	public int getItemCount() {
		return itemCount;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getPubDate() {
		return pubDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
