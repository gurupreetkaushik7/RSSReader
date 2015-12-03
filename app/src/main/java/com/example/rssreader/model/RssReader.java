package com.example.rssreader.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Никита on 01.12.2015.
 */
public class RssReader {
    private String rssUrl;

    public RssReader(String url) {
        this.rssUrl = url;
    }

    public List<RssItem> getItems() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        RssParseHandler handler = new RssParseHandler();

        URL url = new URL(rssUrl);
        InputStream rssInputStream = url.openStream();
        StringBuffer rssContents = new StringBuffer();
        int byteRead = -1;
        while ((byteRead = rssInputStream.read()) != -1) {
            char readedChar = (char)byteRead;
            if (readedChar != '’' && readedChar != '“') {
                rssContents.append(readedChar);
            }
        }
        InputStream rssValidInputStream = new ByteArrayInputStream(rssContents.toString().getBytes());
        saxParser.parse(rssValidInputStream, handler);
        return handler.getItems();
    }
}
