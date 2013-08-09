package com.px.MyTimetable.RSSReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSHandler extends DefaultHandler {
	
	private RSSFeed feed;
	private RSSItem item;
	
	private final int RSS_TITLE = 1;
	private final int RSS_LINK = 2;
	private final int RSS_DESCRIPTION = 3;
	private final int RSS_CATEGORY = 4;
	private final int RSS_PUBDATE = 5;
	
	private boolean getFeedDetails = false;
	private int depth = 0;
	private int currentElementState = 0;
	
	public RSSHandler() {
	}
	
	/**
	 * Returns feed when parsing is complete
	 */
	public RSSFeed getFeed() {
		return feed;
	}

	public void startDocument() throws SAXException {
		feed = new RSSFeed();
		item = new RSSItem();
	}
	
	public void endDocument() throws SAXException {
	}
	
	/**
	 * If channel information is being read a flag is set so that this information
	 * is stored in the RSSFeed rather than an RSSItem
	 * 
	 * When a new element is found the state is updated so that any data read following 
	 * this element is stored in the correct place
	 */
	public void startElement(String namespaceURI, String localName, 
			String qName, Attributes atts) throws SAXException {
		
		depth++;

		// Checks whether channel information is being parsed
		getFeedDetails = (depth == 3 ? true : false);
		
		if (localName.equals("item")) {
			item = new RSSItem();
			return;
		}
		
		if (localName.equals("title")) {
			currentElementState = RSS_TITLE;
			return;
		}
		
		if (localName.equals("description")) {
			currentElementState = RSS_DESCRIPTION;
			return;
		}
		
		if (localName.equals("link")) {
			currentElementState = RSS_LINK;
			return;
		}
		
		if (localName.equals("category")) {
			currentElementState = RSS_CATEGORY;
			return;
		}
		
		if (localName.equals("pubDate")) {
			currentElementState = RSS_PUBDATE;
			return;
		}
		
		currentElementState = 0;
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		
		depth--;
		
		if (localName.equals("item")) {
			// item has been read so add to list
			feed.addItem(item);
			return;
		}
		
	}
	
	/**
	 * The parser may return all contiguous characters in a single chunk, or may split
	 * it into several chunks therefore the data is concatenated to the string stored in the
	 * RSSItem or RSSFeed
	 */
	public void characters(char ch[], int start, int length) {

		String chars = new String(ch, start, length);
		chars = chars.trim();
		
		switch (currentElementState) {
		case RSS_TITLE:
			if (getFeedDetails) {
				feed.setTitle(feed.getTitle().concat(chars));
			} else {
				item.setTitle(item.getTitle().concat(chars));
			}
			break;
		case RSS_LINK:
			item.setLink(item.getLink().concat(chars));
			break;
		case RSS_DESCRIPTION:
			if (getFeedDetails) {
				feed.setDescription(feed.getDescription().concat(chars));
			} else {
				item.setDescription(item.getDescription().concat(chars));
			}
			break;
		case RSS_CATEGORY:
			item.setCategory(item.getCategory().concat(chars));
			break;
		case RSS_PUBDATE:
			if (getFeedDetails) {
				feed.setPubDate(feed.getPubDate().concat(chars));
			} else {
				item.setPubDate(item.getPubDate().concat(chars));
			}
			break;
		default: // Do nothing with text data
			break;
		}

	}	
}
