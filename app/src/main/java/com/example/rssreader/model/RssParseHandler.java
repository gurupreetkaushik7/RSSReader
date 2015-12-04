package com.example.rssreader.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Никита on 01.12.2015.
 */
public class RssParseHandler extends DefaultHandler {
    static String ITEM = "item";
    static String TITLE = "title";
    static String DESCRIPTION = "description";
    static String LINK = "link";
    static String AUTHOR = "author";
    static String DATE = "pubDate";


    private List<RssItem> rssItems; // contains all rss items

    private RssItem currentRssItem; // used to reference item while parsing

    private Boolean isParsingTitle;
    private Boolean isParsingLink;
    private Boolean isParsingDescription;
    private Boolean isParsingAuthor;
    private Boolean isParsingDate;

    public RssParseHandler() {
        rssItems = new ArrayList<RssItem>();
        isParsingLink = false;
        isParsingTitle = false;
        isParsingDescription = false;
        isParsingAuthor = false;
        isParsingDate = false;
    }

    public List<RssItem> getItems() {
        return rssItems;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
                              // execute when parser reach opening tag
        if (qName.equals(ITEM)) {  // create new item if parser reach 'item' tag
            currentRssItem = new RssItem();
        } else if (qName.equals(TITLE)){
            isParsingTitle = true;
        } else if (qName.equals(LINK)) {
            isParsingLink = true;
        } else if (qName.equals(DESCRIPTION)) {
            isParsingDescription = true;
        } else if (qName.equals(AUTHOR)) {
            isParsingAuthor = true;
        } else if (qName.equals(DATE)) {
            isParsingDate = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
                              // execute when parser reach closing tag
        if (qName.equals(ITEM)) {  // push item to items list
            rssItems.add(currentRssItem);
            currentRssItem = null;
        } else if (qName.equals(TITLE)){
            isParsingTitle = false;
        } else if (qName.equals(LINK)) {
            isParsingLink = false;
        } else if (qName.equals(DESCRIPTION)) {
            isParsingDescription = false;
        } else if (qName.equals(AUTHOR)) {
            isParsingAuthor = false;
        } else if (qName.equals(DATE)) {
            isParsingDate = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentRssItem != null) {
            if (isParsingTitle) {
                currentRssItem.setTitle(new String(ch, start, length));
            } else if(isParsingLink) {
                currentRssItem.setLink(new String(ch, start, length));
            } else if(isParsingDescription) {
                currentRssItem.setDescription(new String(ch, start, length));
            } else if(isParsingAuthor) {
                currentRssItem.setAuthor(new String(ch, start, length));
            } else if(isParsingDate) {
                currentRssItem.setPubDate(new String(ch, start, length));
            }
        }
    }


}
