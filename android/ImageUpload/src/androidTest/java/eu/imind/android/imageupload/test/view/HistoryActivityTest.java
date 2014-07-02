package eu.imind.android.imageupload.test.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import eu.imind.android.imageupload.R;
import eu.imind.android.imageupload.model.DB;
import eu.imind.android.imageupload.model.UserImage;
import eu.imind.android.imageupload.view.HistoryActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;

import static eu.imind.android.imageupload.ImageUploadApplication.*;
import static org.mockito.Mockito.*;

public class HistoryActivityTest extends ActivityInstrumentationTestCase2<HistoryActivity> {

    private Instrumentation.ActivityMonitor mImageCaptureMonitor;

    public HistoryActivityTest() {
        super(HistoryActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpImageCapture();
        setUpDB();
        getActivity();
    }

    private void setUpImageCapture() {
        IntentFilter captureFilter = new IntentFilter(MediaStore.ACTION_IMAGE_CAPTURE);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());
        mImageCaptureMonitor = getInstrumentation().addMonitor(captureFilter, result, true);
    }

    private void setUpDB() {
        setDB(mock(DB.class));
    }

    @Override
    protected void tearDown() throws Exception {
        resetDB();
        super.tearDown();
    }

    @MediumTest
    public void testNew() {
        onView(withId(R.id.action_new)).perform(click());
        verify(getDB()).save(any(UserImage.class));
        getInstrumentation().checkMonitorHit(mImageCaptureMonitor, 1);
    }
}
