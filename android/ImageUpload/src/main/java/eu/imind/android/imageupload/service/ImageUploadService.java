package eu.imind.android.imageupload.service;

import android.app.IntentService;
import android.content.Intent;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import eu.imind.android.imageupload.Variant;
import eu.imind.android.imageupload.model.UserImage;

import static eu.imind.android.imageupload.util.Util.*;
import static eu.imind.android.imageupload.ImageUploadApplication.*;

public class ImageUploadService extends IntentService {
    private static final int CHUNK_SIZE = 10240;

    private OkHttpClient mClient;
    private OkUrlFactory mUrlFactory;
    private MediaType mGenericType = MediaType.parse("application/octet-stream");

    public ImageUploadService() {
        super(ImageUploadService.class.getName());
        mClient = new OkHttpClient();
        mUrlFactory = new OkUrlFactory(mClient);
        getBus().register(this);
    }

    @Override
    public void onDestroy() {
        getBus().unregister(this);
        mChunk.remove();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isConnectionOn(getApplicationContext())) {
            long id = intent.getLongExtra(UserImage.PARAM_ID, -1);
            if (id != -1) {
                UserImage img = getDB().findUserImageById(id);
                if (img != null && img.getUploaded() < img.getSize()) {
                    process(img);
                }
            } else {
                for (UserImage img : getDB().findUserImagePending()) {
                    if (img.getUploaded() < img.getSize()) {
                        process(img);
                    }
                }
            }
        }
    }

    private void process(UserImage img) {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            // Get the chunk from the file
            long begin = img.getUploaded();
            byte[] chunk = mChunk.get();
            int count = readFilePart(img.getName(), begin, chunk);

            // Upload chunk
            conn = mUrlFactory.open(new URL(
                    Variant.API_BASE + "begin=" + begin + "&count=" + count + "&size=" + img.getSize()));
            conn.setRequestMethod("POST");
            out = conn.getOutputStream();
            out.write(chunk, 0, count);
            out.close();
            out = null;

            // Check API response
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + conn.getResponseCode() + " " + conn.getResponseMessage());
            }
            in = conn.getInputStream();
            String firstLine = readFirstLine(in);

            if (firstLine != null && "ok".equals(firstLine.trim())) {
                // Register change
                img.setUploaded(begin + count);
                getDB().save(img, false);
                getBus().post(new ImageUploadStatus(img));

                getDB().uploadAsync(img); // Continue
            }
        } catch (IOException ex) {
            getDB().remove(img);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    private ThreadLocal<byte[]> mChunk = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[CHUNK_SIZE];
        }
    };
}
