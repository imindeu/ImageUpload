package eu.imind.android.imageupload.test;

import android.content.ComponentName;
import android.content.Intent;
import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

import java.util.HashSet;
import java.util.Set;

import eu.imind.android.imageupload.ImageUploadApplication;
import eu.imind.android.imageupload.service.ImageUploadService;

public class ImageUploadApplicationTest extends ApplicationTestCase<ImageUploadApplication> {

    private Set<String> mServicesStarted;

    public ImageUploadApplicationTest() {
        super(ImageUploadApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mServicesStarted = new HashSet<String>();
        setContext(new RenamingDelegatingContext(getContext(), "test") {
            @Override
            public ComponentName startService(Intent service) {
                mServicesStarted.add(service.getComponent().getClassName());
                return service.getComponent();
            }
        });
        createApplication();
    }

    @MediumTest
    public void testImageUploadServiceStarted() {
        assertTrue("Image upload service should have been started!",
                mServicesStarted.contains(ImageUploadService.class.getName()));
    }

    public void testDBInstantiated() {
        assertNotNull(ImageUploadApplication.getDB());
    }
}
