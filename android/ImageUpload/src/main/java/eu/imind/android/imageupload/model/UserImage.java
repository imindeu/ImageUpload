package eu.imind.android.imageupload.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserImage {
    public static final String PARAM_ID = "UserImage.id";

    private Long mId;
    private String mName;
    private Date mDate;
    private long mSize, mUploaded;

    public UserImage(String name, Date date, long size, long uploaded) {
        this(null, name, date, size, uploaded);
    }

    public UserImage(File imageFile) {
        this(imageFile.getAbsolutePath(), new Date(imageFile.lastModified()), imageFile.length(), 0);
    }

    UserImage(Cursor cursor) {
        this(cursor.getLong(0), cursor.getString(1), new Date(cursor.getLong(2) * 1000L),
                cursor.getLong(3), cursor.getLong(4));
    }

    UserImage(Long id, String name, Date date, long size, long uploaded) {
        mId = id;
        mName = name;
        mDate = date;
        mSize = size;
        mUploaded = uploaded;
    }

    public Long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public long getUploaded() {
        return mUploaded;
    }

    public void setUploaded(long uploaded) {
        this.mUploaded = uploaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImage userImage = (UserImage) o;

        if (mId != null ? !mId.equals(userImage.mId) : userImage.mId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mId != null ? mId.hashCode() : 0;
    }

    void save(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NAME, mName);
        values.put(TABLE_ROW_DATE, mDate.getTime() / 1000L);
        values.put(TABLE_ROW_SIZE, mSize);
        values.put(TABLE_ROW_UPLOADED, mUploaded);

        if (mId == null) {
            mId = db.insert(TABLE_NAME, null, values);
        } else {
            db.update(TABLE_NAME, values, SQL_WHERE_CLAUSE_ID, new String[] { mId.toString() });
        }

        assert mId != null;
    }

    void remove(SQLiteDatabase db) {
        db.delete(TABLE_NAME, SQL_WHERE_CLAUSE_ID, new String[] { mId.toString() });
        mId = null;
    }

    static UserImage findById(long id, SQLiteDatabase db) {
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

    static List<UserImage> findPending(SQLiteDatabase db) {
        return findBy(SQL_WHERE_CLAUSE_PENDING, null, "100", db);
    }

    static List<UserImage> findAll(SQLiteDatabase db) {
        return findBy(null, null, "100", db);
    }

    private static List<UserImage> findBy(String selection, String[] selectionArgs, String limit,
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

    private static final String TABLE_NAME = "UserImage";
    private static final String TABLE_ROW_ID = "_id";
    private static final String TABLE_ROW_NAME = "name";
    private static final String TABLE_ROW_DATE = "date";
    private static final String TABLE_ROW_SIZE = "size";
    private static final String TABLE_ROW_UPLOADED = "uploaded";

    private static final String[] TABLE_ROWS = new String[] {
            TABLE_ROW_ID,
            TABLE_ROW_NAME,
            TABLE_ROW_DATE,
            TABLE_ROW_SIZE,
            TABLE_ROW_UPLOADED
    };

    private static final String SQL_WHERE_CLAUSE_ID = TABLE_ROW_ID + " = ?";
    private static final String SQL_WHERE_CLAUSE_PENDING =
            TABLE_ROW_UPLOADED + " < " + TABLE_ROW_SIZE;

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            TABLE_ROW_ID + " INTEGER PRIMARY KEY, " +
            TABLE_ROW_NAME + " TEXT, " +
            TABLE_ROW_DATE + " INTEGER, "+
            TABLE_ROW_SIZE + " BIGINTEGER, " +
            TABLE_ROW_UPLOADED + " BIGINTEGER " +
            ")";

    static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
