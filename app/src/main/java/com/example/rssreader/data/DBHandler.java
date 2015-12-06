package com.example.rssreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rssreader.model.RssItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple ORM-like SQLite database handler
 * Images will be save outside DB
 * TODO : Save images outside DB using index in name
 */
public class DBHandler extends SQLiteOpenHelper {
    static String DATABASE_NAME = "feedDB";
    static int DATABASE_VERSION = 1;
    static String TABLE_FEED = "Feed";
    static String KEY_ID = "id";
    static String KEY_TITLE = "title";
    static String KEY_LINK = "link";
    static String KEY_DESCRIPTION = "description";
    static String KEY_AUTHOR = "author";
    static String KEY_PUB_DATE = "pubDate";
    static String KEY_IMAGE = "image";
    private BitmapCompressor compressor;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        compressor = new BitmapCompressor();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_FEED + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_LINK + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_AUTHOR + " TEXT,"
                + KEY_PUB_DATE + " TEXT,"
                + KEY_IMAGE + " BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED);
        onCreate(db);
    }

    public void addRssItem(RssItem item) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, item.getTitle());
        contentValues.put(KEY_LINK, item.getLink());
        contentValues.put(KEY_DESCRIPTION, item.getDescription());
        contentValues.put(KEY_AUTHOR, item.getAuthor());
        contentValues.put(KEY_PUB_DATE, item.getPubDate());
        contentValues.put(KEY_IMAGE, compressor.getBytes(item.getImage()));
        database.insert(TABLE_FEED, null, contentValues);
        database.close();
    }

    public RssItem getRssItem(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                TABLE_FEED,
                new String[]{KEY_ID, KEY_TITLE, KEY_LINK, KEY_DESCRIPTION, KEY_AUTHOR,
                        KEY_PUB_DATE, KEY_IMAGE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        RssItem item = new RssItem(null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            item = new RssItem(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    compressor.getImage(cursor.getBlob(6)));
        }
        database.close();
        cursor.close();
        return  item;
    }

    public List<RssItem> getAllRssItems() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FEED;
        List<RssItem> items = new ArrayList<RssItem>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToLast()) {
            do {
                RssItem item = new RssItem( cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        compressor.getImage(cursor.getBlob(6)));
                items.add(item);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        database.close();
        return items;
    }

    public void deleteAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FEED, null, null);
        database.close();
    }

    public int length() {
        String countQuery = "SELECT  * FROM " + TABLE_FEED;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(countQuery, null);
        int length = cursor.getCount();
        cursor.close();
        database.close();
        return length;
    }

    public String getLastPubDate() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_FEED + " WHERE " + KEY_ID + " = " +
                "(SELECT MAX(" + KEY_ID + ") FROM " + TABLE_FEED + ")";
        Cursor cursor = database.rawQuery(selectQuery, null);
        String lastPubDate = null;
        if (cursor.moveToFirst()) {
            lastPubDate = cursor.getString(5);
        }
        cursor.close();
        database.close();
        return lastPubDate;
    }
}
