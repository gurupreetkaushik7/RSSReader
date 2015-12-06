package com.example.rssreader.model;

import android.graphics.Bitmap;
import android.media.Image;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents RSS item
 */
public class RssItem {
    private int dbIndex;
    private String title;
    private String description;
    private String link;
    private String author;
    private String pubDate;
    private Bitmap imageBitmap;

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

    public void setImage(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
