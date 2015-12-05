package com.example.rssreader.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Reading and parsing RSS FEED
 */
public class RssReader {
    private String rssUrl;
    private ArrayList<RssItem> rssItems;
    private Element entry;

    public RssReader(String url) {
        this.rssUrl = url;
        rssItems = new ArrayList<RssItem>();
    }

    public List<RssItem> getItems() throws Exception {
        URL url = new URL(rssUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();

            // DocumentBuilderFactory, DocumentBuilder are used for xml parsing
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //using db (Document Builder) parse xml data and assign it to Element
            Document document = documentBuilder.parse(is);
            Element element = document.getDocumentElement();

            //take rss nodes to NodeList
            NodeList nodeList = element.getElementsByTagName("item");
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    //take each entry (matches <item></item> tags in xml data
                    entry = (Element) nodeList.item(i);

                    //create RssItemObject and add it to the ArrayList
                    RssItem rssItem = new RssItem(
                            parseTitle(),
                            parseLink(),
                            parseDescription(),
                            parseAuthor(),
                            parseDate(),
                            parseImageBitmap());
                    rssItems.add(rssItem);
                }
            }
        }
        return rssItems;
    }

    private String parseTitle() {
        String title = "";
        Element titleElement = (Element)entry.getElementsByTagName("title").item(0);
        if (titleElement.hasChildNodes()) {
            title = titleElement.getFirstChild().getNodeValue();
        }
        return title;
    }

    private String parseDescription() {
        String description = "";
        Element descriptionElement = (Element)entry.getElementsByTagName("description").item(0);
        // in some xml inside tag <description> parser can find multiline text, that
        // handled via NodeList 'childs', so parser need to find description string in NodeList
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

    private String parseLink() {
        String link = "";
        Element linkElement = (Element) entry.getElementsByTagName("link").item(0);
        if (linkElement.hasChildNodes()) {
            link = linkElement.getFirstChild().getNodeValue();
        }
        return link;
    }

    private String parseAuthor() {
        String author = "";
        Element authorElement = (Element) entry.getElementsByTagName(
                "author").item(0);
        if (authorElement != null) {
            if (authorElement.hasChildNodes()) {
                author = authorElement.getFirstChild().getNodeValue();
            }
        }
        return author;
    }

    private Date parseDate() {
        Date pubDate = null;
        Element pubDateElement = (Element)entry.getElementsByTagName("pubDate").item(0);
        if (pubDateElement != null) {
            if (pubDateElement.hasChildNodes()) {
                pubDate = new Date(pubDateElement.getFirstChild().getNodeValue());
            }
        }
        return pubDate;
    }

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
        return loadBitmapFromUrl(imageUrl);
    }

    private Bitmap loadBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
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
