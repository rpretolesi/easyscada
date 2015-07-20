package com.pretolesi.easyscada.EULA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.pretolesi.easyscada.R;

/**
 * Thank you to Donn Felker for this code!
 */

/*
This is a good way for a EULA but if you update alot you get alot of overhead so it should be better to use
if(versionInfo.versionCode != prefs.getInt(EULA_PREFIX,0)){
and
editor.putInt(EULA_PREFIX, versionInfo.versionCode);
 */

public class EULA {
    private Activity mActivity;

    public EULA(Activity context) {

        mActivity = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
                pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
        }

        return pi;
    }

    public void show() {
        PackageInfo versionInfo = getPackageInfo();
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        String EULA_PREFIX = "eula_";
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if(!hasBeenShown){
            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.updates) + "\n\n" + mActivity.getString(R.string.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(eulaKey, true);
                        editor.apply();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();

                        }
                    });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override public boolean onKey(DialogInterface dialoginterface, int keyCode, KeyEvent event) {
                    return (keyCode != KeyEvent.KEYCODE_HOME);
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            }
        }
}

