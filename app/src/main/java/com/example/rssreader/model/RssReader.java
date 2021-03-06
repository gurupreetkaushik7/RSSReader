package com.example.rssreader.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.rssreader.data.DBHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Handle getting new feed item from url
 */
public class RssReader {
    private final String rssUrl;
    private Element entry;
    private final DBHandler databaseHandler;
    private Date lastPubDate;
    private Boolean isDataBaseWOLastPubDate;
    private static final int maxItemsCount = 50; // just for now (large feeds are bad)
    // (later can be used to swipe down to continue reading old feed)

    public RssReader(String url, Activity activity) {
        this.rssUrl = url;
        databaseHandler = new DBHandler(activity);
        String lastPubDateString = databaseHandler.getLastPubDate();

        // in case of db didn contain lastPubDate
        isDataBaseWOLastPubDate = false;
        if (lastPubDateString == null)
        {
            isDataBaseWOLastPubDate = true;
        } else {
            lastPubDate = new Date(lastPubDateString);
        }
    }

    // Checks that url contains xml with rss
    public Boolean isValid() throws Exception{
        HttpURLConnection conn;
        int responseCode;
        try {
            URL url = new URL(rssUrl);
            conn = (HttpURLConnection) url.openConnection();
            responseCode = conn.getResponseCode();
        } catch (Exception e)
        {
            Log.e("validatingUrl", e.getMessage());
            return false;
        }
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            Element element = document.getDocumentElement();
            return element.getTagName().equals("rss");
        }
        return false;
    }

    // Read and parse RSS FEED, push new items into database
    // Return count of new items
    public int updateDB() throws Exception {
        int newItemsCount = 0;
        URL url = new URL(rssUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();

            // DocumentBuilderFactory, DocumentBuilder are used for xml parsing
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //using Document Builder to parse xml data and assign it to Element
            Document document = documentBuilder.parse(is);
            Element element = document.getDocumentElement();

            //take rss nodes to NodeList
            NodeList nodeList = element.getElementsByTagName("item");
            if (nodeList.getLength() > 0) {
                List<RssItem> rssItems = new ArrayList<RssItem>();
                for (int i = 0; i < nodeList.getLength() && newItemsCount <= maxItemsCount;
                     i++, newItemsCount++) {
                    //take each entry (matches <item></item> tags in xml data
                    entry = (Element) nodeList.item(i);

                    //create RssItemObject by parsing every item from 'entry'
                    RssItem rssItem = new RssItem(
                            parseField("title"),
                            parseField("link"),
                            parseDescription(),
                            parseField("author"),
                            parseField("pubDate"),
                            parseImageBitmap());

                    // reading url until time in feed item > time from last item from database
                    // (news are actual)
                    if (!isActual(rssItem.getPubDate())) {
                        break;
                    }
                    rssItems.add(rssItem);
                }
                for (int i = rssItems.size() - 1; i >=0 ; i--)
                {
                    databaseHandler.addRssItem(rssItems.get(i));
                }
            }
        }
        return newItemsCount;
    }

    // Returns true if new pubDate is more that lastPubDate in database
    private Boolean isActual(String pubDate){
        if (isDataBaseWOLastPubDate) {
            return true;
        }
        Date pubTime = new Date(pubDate);
        return lastPubDate.compareTo(pubTime) < 0;
    }

    private String parseField(String fieldName) {
        String field = "";
        Element element = (Element)entry.getElementsByTagName(fieldName).item(0);
        if (element != null) {
            if (element.hasChildNodes()) {
                field = element.getFirstChild().getNodeValue();
            }
        }
        return field;
    }

    // Parse description
    private String parseDescription() {
        String description = "";
        Element descriptionElement = (Element)entry.getElementsByTagName("description").item(0);
        // in some xml inside tag <description> parser can find multiline text, that
        // handled via NodeList 'child', so parser need to find description string in NodeList
        if (descriptionElement.hasChildNodes()) {
            description = descriptionElement.getFirstChild().getNodeValue();
            if (description.charAt(0) == '\n') {
                NodeList nodesInDescription = descriptionElement.getChildNodes();
                for (int nodeIndex = 0; nodeIndex < nodesInDescription.getLength();
                     nodeIndex++) {
                    description = nodesInDescription.item(nodeIndex).getNodeValue();
                    if (description.charAt(0) != '\n') {
                        break;
                    }
                }
            }
            description = getClearDescription(description);
        }
        return description;
    }

    // parse image url and return its Bitmap implementation
    private Bitmap parseImageBitmap() {
        Element enclosureElement = (Element)entry.getElementsByTagName("enclosure").item(0);
        Element mediaContentElement = (Element)entry.getElementsByTagName("media:content").item(0);
        String imageUrl = "";
        if (enclosureElement != null)
        {
            imageUrl = enclosureElement.getAttribute("url");
        }
        else if (mediaContentElement != null)
        {
            imageUrl = mediaContentElement.getAttribute("url");
        }
        if (!imageUrl.equals("")) {
            return loadBitmapFromUrl(imageUrl);
        }
        else {
            return null;
        }
    }

    // loads image url into Bitmap
    private Bitmap loadBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // getting rid of <p>,<a href....> and other elements like this in Description string
    private String getClearDescription(String dirtyDescription) {
        Boolean isNeedClear = false;
        String clearDescription = "";
        for (int i = 0; i < dirtyDescription.length(); i++) {
            if (dirtyDescription.charAt(i) == '<') {
                isNeedClear = true;
                continue;
            }
            if (dirtyDescription.charAt(i) == '>') {
                isNeedClear = false;
                continue;
            }
            if (!isNeedClear)
            {
                clearDescription += dirtyDescription.charAt(i);
            }
        }
        return clearDescription;
    }
}
