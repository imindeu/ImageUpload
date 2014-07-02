package eu.imind.android.imageupload.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserImageDAO {

    public void save(UserImage img, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NAME, img.getName());
        values.put(TABLE_ROW_DATE, img.getDate().getTime() / 1000L);
        values.put(TABLE_ROW_SIZE, img.getSize());
        values.put(TABLE_ROW_UPLOADED, img.getUploaded());

        Long id = img.getId();
        if (id == null) {
            id = db.insert(TABLE_NAME, null, values);
        } else {
            db.update(TABLE_NAME, values, SQL_WHERE_CLAUSE_ID, new String[] { id.toString() });
        }
    }

    public void remove(UserImage img, SQLiteDatabase db) {
        Long id = img.getId();
        if (id != null) {
            db.delete(TABLE_NAME, SQL_WHERE_CLAUSE_ID, new String[] { id.toString() });
            img.setId(id);
        }
    }

    public UserImage findById(long id, SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, TABLE_ROWS,
                SQL_WHERE_CLAUSE_ID, new String[]{ String.valueOf(id) },
                null, null, null, "1");
        try {
            if (cursor.moveToNext()) {
                return new UserImage(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    public List<UserImage> findPending(SQLiteDatabase db) {
        return findBy(SQL_WHERE_CLAUSE_PENDING, null, "100", db);
    }

    public List<UserImage> findAll(SQLiteDatabase db) {
        return findBy(null, null, "100", db);
    }

    protected List<UserImage> findBy(String selection, String[] selectionArgs, String limit,
                                          SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, TABLE_ROWS,
                selection, selectionArgs,
                null, null,
                TABLE_ROW_DATE + " DESC", limit);
        try {
            List<UserImage> pending = new ArrayList<UserImage>();
            while (cursor.moveToNext()) {
                pending.add(new UserImage(cursor));
            }
            return pending;
        } finally {
            cursor.close();
        }
    }

    protected static final String TABLE_NAME = "UserImage";
    protected static final String TABLE_ROW_ID = "_id";
    protected static final String TABLE_ROW_NAME = "name";
    protected static final String TABLE_ROW_DATE = "date";
    protected static final String TABLE_ROW_SIZE = "size";
    protected static final String TABLE_ROW_UPLOADED = "uploaded";

    protected static final String[] TABLE_ROWS = new String[] {
            TABLE_ROW_ID,
            TABLE_ROW_NAME,
            TABLE_ROW_DATE,
            TABLE_ROW_SIZE,
            TABLE_ROW_UPLOADED
    };

    protected static final String SQL_WHERE_CLAUSE_ID = TABLE_ROW_ID + " = ?";
    protected static final String SQL_WHERE_CLAUSE_PENDING =
            TABLE_ROW_UPLOADED + " < " + TABLE_ROW_SIZE;

    protected static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            TABLE_ROW_ID + " INTEGER PRIMARY KEY, " +
            TABLE_ROW_NAME + " TEXT, " +
            TABLE_ROW_DATE + " INTEGER, "+
            TABLE_ROW_SIZE + " BIGINTEGER, " +
            TABLE_ROW_UPLOADED + " BIGINTEGER " +
            ")";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
