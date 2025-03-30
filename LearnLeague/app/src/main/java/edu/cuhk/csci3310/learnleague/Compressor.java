package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Compressor {
    /**
     * Compress the bitmap image
     * @param bitmap The image
     * @param quality How much quality do you want
     * @return A compressed bitmap image
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        byte[] compressedByteArray = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.length);
    }
}
