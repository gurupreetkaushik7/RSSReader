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
    private List<RssItem> rssItems; // contains all rss items

    private RssItem currentRssItem; // used to reference items while parsing

    private Boolean isParsingTitle;
    private Boolean isParsingLink;

    public RssParseHandler() {
        rssItems = new ArrayList<RssItem>();
    }

    public List<RssItem> getItems() {
        return rssItems;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
                              // execute when parser reach opening tag
        if ("item".equals(qName)) {  // create new item if parser reach 'item' tag
            currentRssItem = new RssItem();
        } else if ("title".equals(qName)){
            isParsingTitle = true;
        } else if ("link".equals(qName)) {
            isParsingLink = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
                              // execute when parser reach closing tag
        if ("item".equals(qName)) {  // push item to items list
            rssItems.add(currentRssItem);
            currentRssItem = null;
        } else if ("title".equals(qName)){
            isParsingTitle = false;
        } else if ("link".equals(qName)) {
            isParsingLink = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isParsingTitle) {
            if (currentRssItem != null) {
                currentRssItem.setTitle(new String(ch, start, length));
            }
        } else if (isParsingLink) {
            if (currentRssItem != null) {
                currentRssItem.setLink(new String(ch, start, length));
                isParsingLink = false;
            }
        }
    }


}
