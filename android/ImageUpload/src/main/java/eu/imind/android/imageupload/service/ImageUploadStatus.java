package eu.imind.android.imageupload.service;

import eu.imind.android.imageupload.model.UserImage;

/**
 * Status indicator object for image upload.
 */
public class ImageUploadStatus {

    private Long mId;
    private long mSize, mUploaded;

    public ImageUploadStatus(UserImage img) {
        this(img.getId(), img.getSize(), img.getUploaded());
    }

    public ImageUploadStatus(Long mId, long mSize, long mUploaded) {
        this.mId = mId;
        this.mSize = mSize;
        this.mUploaded = mUploaded;
    }

    public Long getId() {
        return mId;
    }

    public long getSize() {
        return mSize;
    }

    public long getUploaded() {
        return mUploaded;
    }

    /**
     * Asymmetrically accepts {@link eu.imind.android.imageupload.model.UserImage} instances in
     * addition to {@link eu.imind.android.imageupload.service.ImageUploadStatus} instances.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ImageUploadStatus) {
            ImageUploadStatus that = (ImageUploadStatus) o;
            return mId != null ? mId.equals(that.mId) : that.mId == null;
        } else if (o instanceof UserImage) {
            UserImage that = (UserImage) o;
            return mId != null ? mId.equals(that.getId()) : that.getId() == null;
        } else {
            return false;
        }
    }

    /**
     * This method is equivalent to {@link eu.imind.android.imageupload.model.UserImage#hashCode()}.
     */
    @Override
    public int hashCode() {
        return mId != null ? mId.hashCode() : 0;
    }
}
