package eu.imind.android.imageupload;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import eu.imind.android.imageupload.model.DB;
import eu.imind.android.imageupload.service.ImageUploadService;

public class ImageUploadApplication extends Application {
    public static final String TAG = "ImageUploadApplication";

    private static ImageUploadApplication mInst;
    private Bus mApplicationBus;
    private DB mDB;

    public static ImageUploadApplication getInst() {
        return mInst;
    }

    public static Bus getBus() {
        assert mInst != null;
        return mInst.mApplicationBus;
    }

    public static DB getDB() {
        assert mInst != null;
        return mInst.mDB;
    }

    public static void setDB(DB db) {
        assert mInst != null;
        mInst.mDB = db;
    }

    public static void resetDB() {
        if (mInst != null) {
            mInst.mDB = new DB(mInst);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInst = this;
        if (BuildConfig.USE_CRASHLYTICS) {
            Crashlytics.start(this);
        }

        mApplicationBus = new Bus(ThreadEnforcer.ANY);
        mDB = new DB(this);
        startService(new Intent(getApplicationContext(), ImageUploadService.class));
    }
}
