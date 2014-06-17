package eu.imind.android.imageupload.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import static eu.imind.android.imageupload.ImageUploadApplication.*;

public final class Util {

    public static File createImageFile() throws IOException {
        return createImageFile(getInst().getExternalFilesDir(null));
    }

    public static File createImageFile(File storageDir) throws IOException {
        String timeStamp = mImageFileNameDateFormat.get().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir.mkdirs();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static void logNonfatal(int stringResId, Throwable t) {
        Log.d(TAG, "Nonfatal exception", t);
        Crashlytics.logException(t);
        Toast.makeText(getInst(), stringResId, Toast.LENGTH_LONG).show();
    }

    public static boolean isConnectionOn(Context context) {
        NetworkInfo netInfo =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static int readFilePart(String file, long offset, byte[] data) throws IOException {
        RandomAccessFile in = new RandomAccessFile(file, "r");
        try {
            in.seek(offset);
            return in.read(data, 0, data.length);
        } finally {
            closeQuietly(in);
        }
    }

    public static String readFirstLine(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        return reader.readLine();
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                // do nothing
            }
        }
    }

    private static ThreadLocal<SimpleDateFormat> mImageFileNameDateFormat =
            new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd_HHmmss");
        }
    };

    private Util() {
    }
}
