package eu.imind.android.imageupload.model;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import eu.imind.android.imageupload.service.ImageUploadService;

public class DB extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    private static final String NAME = "ImageUpload";

    private Context mContext;
    private UserImageDAO userImageDAO = new UserImageDAO();

    public DB(Context context) {
        super(context, NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        userImageDAO.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        userImageDAO.onUpgrade(db, oldVersion, newVersion);
    }

    public void save(UserImage entity) {
        save(entity, true);
    }

    public void save(UserImage entity, boolean upload) {
        userImageDAO.save(entity, getWritableDatabase());
        if (upload) {
            uploadAsync(entity);
        }
    }

    public void uploadAsync(UserImage entity) {
        Intent intent = new Intent(mContext, ImageUploadService.class);
        intent.putExtra(UserImage.PARAM_ID, entity.getId());
        mContext.startService(intent);
    }

    public void remove(UserImage entity) {
        userImageDAO.remove(entity, getWritableDatabase());
    }

    public UserImage findUserImageById(long id) {
        return userImageDAO.findById(id, getReadableDatabase());
    }

    public List<UserImage> findUserImagePending() {
        return userImageDAO.findPending(getReadableDatabase());
    }

    public List<UserImage> findUserImageAll() {
        return userImageDAO.findAll(getReadableDatabase());
    }
}
