package com.example.rssreader.model;

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

    public RssReader(String url) {
        this.rssUrl = url;
    }

    public List<RssItem> getItems() throws Exception {
        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
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
                    Element entry = (Element) nodeList.item(i);

                    Element titleElement = (Element) entry.getElementsByTagName(
                            "title").item(0);
                    Element descriptionElement = (Element) entry
                            .getElementsByTagName("description").item(0);
                    Element pubDateElement = (Element) entry
                            .getElementsByTagName("pubDate").item(0);
                    Element linkElement = (Element) entry.getElementsByTagName(
                            "link").item(0);
                    Element authorElement = (Element) entry.getElementsByTagName(
                            "author").item(0);

                    String title = "";
                    String description = "";
                    String link = "";
                    String pubDate = "";
                    String author = "";

                    // in some xml inside tag <description> parser can find multiline text, that
                    // handled via NodeList 'childs' in element
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
                    if (titleElement.hasChildNodes()) {
                        title = titleElement.getFirstChild().getNodeValue();
                    }
                    if (linkElement.hasChildNodes()) {
                        link = linkElement.getFirstChild().getNodeValue();
                    }

                    if (pubDateElement != null) {
                        if (pubDateElement.hasChildNodes()) {
                            pubDate = pubDateElement.getFirstChild().getNodeValue();
                        }
                    }
                    if (authorElement != null) {
                        if (authorElement.hasChildNodes()) {
                            author = authorElement.getFirstChild().getNodeValue();
                        }
                    }

                    //create RssItemObject and add it to the ArrayList
                    RssItem rssItem = new RssItem(title, link, description, author, pubDate);
                    rssItems.add(rssItem);
                }
            }
        }
        return rssItems;
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
