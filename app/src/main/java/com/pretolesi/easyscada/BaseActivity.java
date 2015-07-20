package com.pretolesi.easyscada;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.pretolesi.SQL.SQLHelper;

/**
 *
 */
public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";

    /**
     * Use to update
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    protected CharSequence mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SQL Instance
        SQLHelper.getInstance(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void onSectionAttached(int number) {

        switch (number) {
            case 0:
                mTitle = getString(R.string.app_name);
                break;
            case 1:
                mTitle = getString(R.string.settings_title_section_new_room);
                break;
            case 2:
                mTitle = getString(R.string.settings_title_section_open_room);
                break;
            case 3:
                mTitle = getString(R.string.settings_title_section_add_switch);
                break;
            case 4:
                mTitle = getString(R.string.settings_title_dialog_section_add_numeric_value);
                break;
            case 5:
                mTitle = getString(R.string.settings_title_section_add_raw_sensor_value);
                break;
            case 6:
                mTitle = getString(R.string.settings_title_section_add_calibr_sensor_value);
                break;
            case 7:
                mTitle = getString(R.string.settings_title_section_new_tcp_ip_client);
                break;
            case 8:
                mTitle = getString(R.string.settings_title_section_open_tcp_ip_client);
                break;
            case 9:
                mTitle = getString(R.string.settings_title_section_new_bluetooth_client);
                break;
            case 10:
                mTitle = getString(R.string.settings_title_section_open_bluetooth_client);
                break;
        }

    }

    public void onSectionSetTitle(String strTitle) {

        mTitle = strTitle;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }
}
