package com.pretolesi.easyscada.CustomControls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pretolesi.easyscada.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  Custom EditText
 */
public class StringEditText extends EditText {

    private static final String TAG = "StringEditText";

    private short m_shMin, m_shMax;

    // Default Constructor
    public StringEditText(Context context) {
        super(context);
    }

    public StringEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StringEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Custom Constructor
    public void setInputLimit(short shMin, short shMax) {
        m_shMin = shMin;
        m_shMax = shMax;
    }

    public boolean validateInputLimit(){
        if(m_shMin != 0 || m_shMax != 0) {
            try {
                short shValue = (short) this.getText().toString().length();
                if (!isInRange(m_shMin, m_shMax, shValue)) {
                    makeDialogAlert(Short.toString(m_shMin) + " - " + Short.toString(m_shMax));
                    requestFocus();
                    return false;
                }
            } catch (Exception ex) {
                makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                requestFocus();
                return false;
            }
        }
        return true;
    }

    private boolean isInRange(short a, short b, short c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    private void makeDialogAlert(String strMessage) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.text_edad_dlg_title_value_out_of_range);
        builder.setMessage(strMessage);

        builder.setPositiveButton(R.string.text_edad_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static boolean validateInputData(View viev){
        List<View> visited = new ArrayList<>();
        List<View> unvisited = new ArrayList<>();
        unvisited.add(viev);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        for(View v : visited){
            if(v instanceof StringEditText){
                if(!((StringEditText)v).validateInputLimit())
                    return false;
            }

        }

        return true;
    }
}
