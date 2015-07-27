package com.pretolesi.easyscada.CustomControls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 *  Custom EditText
 */
public class NumericEditText extends EditText {

    private static final String TAG = "NumericEditText";

    RelativeLayout.LayoutParams m_rllp = null;

    private DataType m_dtDataType;
    private short m_shMin, m_shMax;
    private int m_iMin, m_iMax;
    private long m_lMin, m_lMax;
    private float m_fMin, m_fMax;
    private double m_dblMin, m_dblMax;

    public NumericEditText(Context context) {
        super(context);
        Init(DataType.SHORT, "");
    }

    public NumericEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(DataType.SHORT, "");
    }

    public NumericEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(DataType.SHORT, "");
    }

    public NumericEditText(Context context, DataType dtDataType, String strHint) {
        super(context);
        Init(dtDataType, strHint);
    }

    private void Init(DataType dtDataType, String strHint) {
        setGravity(Gravity.CENTER);
        setTextColor(Color.YELLOW);
        setSingleLine();
        setHint(strHint);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Set Input Limit
        if(dtDataType != null) {
            m_dtDataType = dtDataType;
        }
        switch (m_dtDataType) {
            case SHORT:
                setInputLimit(Short.MIN_VALUE, Short.MAX_VALUE);
                break;
            case INT:
                setInputLimit(Integer.MIN_VALUE, Integer.MAX_VALUE);
                break;
            case LONG:
                setInputLimit(Long.MIN_VALUE, Long.MAX_VALUE);
                break;
            case FLOAT:
                setInputLimit(-Float.MAX_VALUE, Float.MAX_VALUE);
                break;
            case DOUBLE:
                setInputLimit(-Double.MAX_VALUE, Double.MAX_VALUE);
                break;
        }

        m_rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(m_rllp);
    }

    public void setInputLimit(short shMin, short shMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dtDataType = DataType.SHORT;
        m_shMin = shMin;
        m_shMax = shMax;
    }

    public void setInputLimit(int iMin, int iMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dtDataType = DataType.INT;
        m_iMin = iMin;
        m_iMax = iMax;
    }

    public void setInputLimit(long lMin, long lMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dtDataType = DataType.LONG;
        m_lMin = lMin;
        m_lMax = lMax;
    }

    public void setInputLimit(float fMin, float fMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        m_dtDataType = DataType.FLOAT;
        m_fMin = fMin;
        m_fMax = fMax;
    }

    public void setInputLimit(double dblMin, double dblMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        m_dtDataType = DataType.DOUBLE;
        m_dblMin = dblMin;
        m_dblMax = dblMax;
    }

    public boolean validateInputLimit(){
        if(m_dtDataType != null) {
            switch (m_dtDataType) {
                case SHORT:
                    try {
                        short shValue = Short.parseShort(this.getText().toString());
                        if (!isInRange(m_shMin, m_shMax, shValue)) {
                            makeDialogAlert(Short.toString(m_shMin) + " - " + Short.toString(m_shMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case INT:
                    try {
                        int iValue = Integer.parseInt(this.getText().toString());
                        if (!isInRange(m_iMin, m_iMax, iValue)) {
                            makeDialogAlert(Integer.toString(m_iMin) + " - " + Integer.toString(m_iMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case LONG:
                    try {
                        long lValue = Integer.parseInt(this.getText().toString());
                        if (!isInRange(m_lMin, m_lMax, lValue)) {
                            makeDialogAlert(Long.toString(m_lMin) + " - " + Long.toString(m_lMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case FLOAT:
                    try {
                        float fValue = Float.parseFloat(this.getText().toString());
                        if (!isInRange(m_fMin, m_fMax, fValue)) {
                            makeDialogAlert(Float.toString(m_fMin) + " - " + Float.toString(m_fMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case DOUBLE:
                    try {
                        double dblValue = Double.parseDouble(this.getText().toString());
                        if (!isInRange(m_dblMin, m_dblMax, dblValue)) {
                            makeDialogAlert(Double.toString(m_dblMin) + " - " + Double.toString(m_dblMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }                    break;
            }
        }

        return true;
    }

    private boolean isInRange(short a, short b, short c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(long a, long b, long c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(float a, float b, float c) {
        int iac = Float.compare(c,a); // < 0 if c < a
        int ibc = Float.compare(c,b); // > 0 if c > b
        if(iac < 0 || ibc > 0){
            return false;
        }
        return true;
    }
    private boolean isInRange(double a, double b, double c) {
        int iac = Double.compare(c,a); // < 0 if c < a
        int ibc = Double.compare(c,b); // > 0 if c > b
        if(iac < 0 || ibc > 0){
            return false;
        }
        return true;
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
            if(v instanceof NumericEditText){
                if(!((NumericEditText)v).validateInputLimit())
                    return false;
            }

        }

        return true;
    }

    public void setPosition(int l, int t, int r, int b, int h, int w, boolean bVertical){
        if(m_rllp != null) {
            m_rllp.leftMargin = l - ((getWidth() - w)/2);
            if(!bVertical){
                m_rllp.topMargin = t + (h/2);
            } else {
                m_rllp.topMargin = t + (w/2);
            }
        }
    }
    public void setPositionDown(int l, int t, int r, int b, int h, int w, boolean bVertical){
        if(m_rllp != null) {
            m_rllp.leftMargin = l - ((getWidth() - w)/2);
            if(!bVertical){
                m_rllp.topMargin = t + (h/2) + 60;
            } else {
                m_rllp.topMargin = t + (w/2) + 60;
            }
        }
    }
}
