package com.example.rssreader.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Perform conversations between byte[] and Bitmap
 */
public class BitmapCompressor {
    public byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    public Bitmap getImage(byte[] image) {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } else {
            return null;
        }
    }
}
