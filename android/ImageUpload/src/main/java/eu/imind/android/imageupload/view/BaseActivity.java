package eu.imind.android.imageupload.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar(getSupportActionBar());
    }

    protected void initActionBar(ActionBar actionBar) {
    }
}
