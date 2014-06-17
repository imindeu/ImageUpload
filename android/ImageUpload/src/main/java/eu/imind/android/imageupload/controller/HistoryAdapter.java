package eu.imind.android.imageupload.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.imind.android.imageupload.R;
import eu.imind.android.imageupload.model.UserImage;
import eu.imind.android.imageupload.service.ImageUploadStatus;

import static eu.imind.android.imageupload.ImageUploadApplication.*;

public class HistoryAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UserImage> mItems = Collections.emptyList();

    public HistoryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void refresh() {
        mItems = getDB().findUserImageAll();
    }

    public void refreshAsync() {
        new RefreshTask().execute();
    }

    public void updateProgressAsync(ImageUploadStatus status) {
        new RefreshProgressTask(mItems).execute(status);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public UserImage getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.history_item, parent, false);
            assert convertView != null;
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.update(getItem(position), mContext);
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.thumbnail)
        ImageView mThumbnail;

        @InjectView(R.id.last_modified)
        TextView mLastModified;

        @InjectView(R.id.status)
        TextView mStatus;

        @InjectView(R.id.upload_progress)
        ProgressBar mUploadProgress;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void update(UserImage img, Context context) {
            Picasso.with(context)
                    .load(new File(img.getName()))
                    .resizeDimen(R.dimen.history_item_thumbnail_width, R.dimen.history_item_height)
                    .centerInside()
                    .into(mThumbnail);

            mLastModified.setText(DateUtils.formatDateTime(context,
                    img.getDate().getTime(),
                    DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));

            if (img.getUploaded() == 0L) {
                mStatus.setText(R.string.upload_status_waiting);
            } else if (img.getUploaded() == img.getSize()) {
                mStatus.setText(R.string.upload_status_finished);
            } else {
                mStatus.setText(R.string.upload_status_ongoing);
            }

            if (img.getUploaded() < img.getSize()) {
                mUploadProgress.setVisibility(View.VISIBLE);
                mUploadProgress.setProgress(
                        (int) Math.round(1.0 * img.getUploaded() / img.getSize() * 1000.0));
            } else {
                mUploadProgress.setVisibility(View.GONE);
            }
        }
    }

    class RefreshTask extends AsyncTask<Void, Void, List<UserImage>> {

        @Override
        protected List<UserImage> doInBackground(Void... params) {
            return getDB().findUserImageAll();
        }

        @Override
        protected void onPostExecute(List<UserImage> userImages) {
            mItems = userImages;
            notifyDataSetChanged();
        }
    }

    class RefreshProgressTask extends AsyncTask<ImageUploadStatus, Void, Void> {

        private List<UserImage> mItems;

        public RefreshProgressTask(List<UserImage> items) {
            mItems = items;
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        protected Void doInBackground(ImageUploadStatus... params) {
            ImageUploadStatus status = params[0];
            int pos = mItems.indexOf(status);
            if (pos != -1) {
                mItems.set(pos, getDB().findUserImageById(status.getId()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mItems == HistoryAdapter.this.mItems) {
                notifyDataSetChanged();
            }
        }
    }

}
