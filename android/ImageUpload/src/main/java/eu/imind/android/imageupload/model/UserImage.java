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

    void setId(Long id) {
        this.mId = id;
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

}
