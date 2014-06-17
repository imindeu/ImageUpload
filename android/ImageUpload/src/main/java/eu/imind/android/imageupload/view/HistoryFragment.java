package eu.imind.android.imageupload.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.imind.android.imageupload.ImageUploadApplication;
import eu.imind.android.imageupload.R;
import eu.imind.android.imageupload.controller.HistoryAdapter;
import eu.imind.android.imageupload.model.UserImage;
import eu.imind.android.imageupload.service.ImageUploadStatus;

import static eu.imind.android.imageupload.util.Util.*;

public class HistoryFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    @InjectView(R.id.history_list)
    ListView mList;

    @InjectView(R.id.history_empty)
    TextView mEmpty;

    File mImageFile;
    HistoryAdapter mAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new HistoryAdapter(getActivity());
        mAdapter.refreshAsync();
        ImageUploadApplication.getBus().register(this);
    }

    @Override
    public void onDestroy() {
        ImageUploadApplication.getBus().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, rootView);
        mList.setEmptyView(mEmpty);
        mList.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                captureImage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void captureImage() {
        try {
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mImageFile = createImageFile();
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));
            startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (IOException ex) {
            logNonfatal(R.string.error, ex);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    UserImage img = new UserImage(mImageFile);
                    ImageUploadApplication.getDB().save(img);
                    mAdapter.refreshAsync();
                } else {
                    mImageFile.delete();
                }
                mImageFile = null;
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe
    public void updateProgress(ImageUploadStatus status) {
        mAdapter.updateProgressAsync(status);
    }
}
