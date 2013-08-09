package com.px.MyTimetable.RSSReader;

public class RSSItem {

	private String title = "";
	private String description = "";
	private String link = "";
	private String category = "";
	private String pubDate = "";
	
	public RSSItem() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	
	/**
	 *  Used to limit how much text is displayed
	 */
	public String toString() {
	   if (title.length() > 42) {
			return title.substring(0, 42) + "...";
		}
		return title;
	}
}