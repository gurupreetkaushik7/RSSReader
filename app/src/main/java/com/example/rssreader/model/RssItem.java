package com.example.rssreader.model;

import android.graphics.Bitmap;

/**
 * Represents RSS item
 */
public class RssItem {
    private int dbIndex;
    private final String title;
    private final String description;
    private final String link;
    private final String author;
    private final String pubDate;
    private final Bitmap imageBitmap;

    public RssItem(String title, String link, String description, String author,
                   String pubDate, Bitmap imageBitmap) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.pubDate = pubDate;
        this.imageBitmap = imageBitmap;
    }

    public RssItem(int dbIndex, String title, String link, String description, String author,
                   String pubDate, Bitmap imageBitmap) {
        this.dbIndex = dbIndex;
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.pubDate = pubDate;
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImage() {
        return imageBitmap;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}
