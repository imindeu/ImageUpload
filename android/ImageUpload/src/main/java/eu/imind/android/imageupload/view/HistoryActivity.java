package eu.imind.android.imageupload.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import eu.imind.android.imageupload.R;

public class HistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, HistoryFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void initActionBar(ActionBar actionBar) {
        super.initActionBar(actionBar);
        actionBar.setTitle(R.string.history);
    }
}
