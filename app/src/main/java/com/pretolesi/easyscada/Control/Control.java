package com.pretolesi.easyscada.Control;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.easyscada.BaseFragment;
import com.pretolesi.easyscada.CustomControls.LabelTextView;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.CustomControls.NumericEditText;

/**
 *
 */
public class Control extends TextView implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private DataType m_dtDataType;

    private GestureDetectorCompat mDetector;
    private float m_LastTouchX;
    private float m_LastTouchY;

    private Handler m_TimerHandler;
    private long m_lRepeatingTime;

    private boolean m_bEditMode;
    private NumericEditText m_edEditText;
    private boolean m_bVertical;

    // Label for Show The Name
    private LabelTextView m_LabelTextView;

    // Label for Show The Alarm
    private LabelTextView m_AlarmTextView;

    public Control(Context context) {
        super(context);
        m_LabelTextView = null;
        m_AlarmTextView = null;
        m_bEditMode = false;
        m_dtDataType = DataType.SHORT;
        m_bVertical = false;

        setGravity(Gravity.CENTER);
        setTextSize(20.0f);
        setTextColor(Color.BLUE);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Label Text View
        ViewParent view = this.getParent();
        if(view != null && view instanceof RelativeLayout) {
            m_LabelTextView = new LabelTextView(getContext(), Color.GREEN);
            ((RelativeLayout) view).addView(m_LabelTextView);
            m_AlarmTextView = new LabelTextView(getContext(),Color.RED);
            ((RelativeLayout) view).addView(m_AlarmTextView);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        m_LabelTextView = null;
        m_AlarmTextView = null;
    }

    @Override
    protected void onLayout(boolean changed,  int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
        if(rllp != null) {
            if(m_LabelTextView != null) {
                m_LabelTextView.setPositionUp(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
                if(getTag() != null && getTag() instanceof String){
                    m_LabelTextView.setText((String) getTag());
                }
            }
            if(m_AlarmTextView != null) {
                m_AlarmTextView.setPositionDown(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
            }
            if (m_edEditText != null) {
                if(m_AlarmTextView != null && m_AlarmTextView.getText() != null && m_AlarmTextView.getText().length() > 0){
                    m_edEditText.setPositionDown(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
                } else {
                    m_edEditText.setPosition(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
                }
            }
        }
    }

    protected void setEditMode(boolean bEditMode){
        m_bEditMode = bEditMode;
    }

    protected void setNumericDataType(DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    public void setVertical(boolean bVertical) { this.m_bVertical = bVertical; }

    protected boolean getEditMode(){
        return m_bEditMode;
    }

    protected DataType getNumericDataType(){
        return m_dtDataType;
    }

    public boolean getVertical() { return m_bVertical; }

    public void setTextAlarm(String strTextAlarm){
        if(m_AlarmTextView != null) {
            m_AlarmTextView.setText(strTextAlarm);
        }
    }

    /*
     * Begin
     * Timer variable and function
     */
    protected void setTimer(long lRepeatingTime) {
        m_lRepeatingTime = lRepeatingTime;
        m_TimerHandler = new Handler();
        m_TimerHandler.postDelayed(m_TimerRunnable, lRepeatingTime);
    }

    protected void resetTimer() {
        if(m_TimerHandler != null){
            m_TimerHandler.removeCallbacks(m_TimerRunnable);
        }
    }

    private Runnable m_TimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(m_TimerHandler != null) {
                onTimer();

                m_TimerHandler.postDelayed(m_TimerRunnable, m_lRepeatingTime);
            }
        }
    };

    protected void onTimer() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (m_bEditMode) {
            if (mDetector == null) {
                // Instantiate the gesture detector with the
                // application context and an implementation of
                // GestureDetector.OnGestureListener
                mDetector = new GestureDetectorCompat(getContext(), this);
                // Set the gesture detector as the double tap
                // listener.
                mDetector.setOnDoubleTapListener(this);
            }
            final int action = MotionEventCompat.getActionMasked(event);

            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    onTouchActionUp(m_bEditMode);

                    break;
                }
            }

            this.mDetector.onTouchEvent(event);

            return true;
        } else {
            final int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {

                    return true;

                }
                case MotionEvent.ACTION_UP: {
                    onTouchActionUp(m_bEditMode);
                 break;
                }
            }
        }

        super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (this.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) this.getLayoutParams();
            m_LastTouchX = e.getRawX() - rllp.leftMargin;
            m_LastTouchY = e.getRawY() - rllp.topMargin;
        }

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final float x = e2.getRawX();
        final float y = e2.getRawY();
        // Calculate the distance moved
        final float dx = x - m_LastTouchX;
        final float dy = y - m_LastTouchY;

        BaseFragment.setViewPosition(this, (int) dx, (int) dy);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    protected void onTouchActionUp(boolean bEditMode) {
    }

    protected void openInputField(String strHint) {
        if(m_edEditText == null){
            // Create....
            ViewParent view = this.getParent();
            if(view != null && view instanceof RelativeLayout) {
//                m_edEditText = new NumericEditText(getContext(), m_dtDataType, getText().toString());
                m_edEditText = new NumericEditText(getContext(), m_dtDataType, strHint);
                ((RelativeLayout) view).addView(m_edEditText);
                m_edEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null){
                    imm.showSoftInput(m_edEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                // Set Listener
                m_edEditText.setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if (m_edEditText != null) {
                                m_edEditText.clearFocus();
                            }
                            return true;
                        }

                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (m_edEditText != null) {
                                if (m_edEditText.validateInputLimit()) {
                                    OnWriteInputField(m_edEditText.getText().toString());
                                }
                            }
                            return true;
                        }

                        return false;
                    }
                });

                // Set Listener
                m_edEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            removeInputField();
                        }
                    }
                });
                invalidate();
            }
        }
    }

    private void removeInputField() {
        if(m_edEditText != null){
            ViewParent view = this.getParent();
            if (view instanceof RelativeLayout) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (m_edEditText.getWindowToken() != null) {
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(m_edEditText.getWindowToken(), 0);
                    }
                    ((RelativeLayout) view).removeView(m_edEditText);
                }
            }
        }
        m_edEditText = null;
    }

    protected void closeInputField(){
        if (m_edEditText != null) {
            m_edEditText.clearFocus();
        }
    }

    protected void setErrorInputField(String strError){
        if (m_edEditText != null) {
            m_edEditText.setError(strError);
        }
    }

    protected void OnWriteInputField(String strValue) {

    }

    // Helper
    public String centerString(String strToCenter, int iMinNrOfCharToShow) {
        if(strToCenter == null || strToCenter.isEmpty()){
            return "";
        }

        if(iMinNrOfCharToShow < strToCenter.length()){
            iMinNrOfCharToShow = strToCenter.length();
        }

        int padSize = iMinNrOfCharToShow - strToCenter.length();
        int padStart = strToCenter.length() + padSize / 2;

        strToCenter = String.format("%" + padStart + "s", strToCenter);
        strToCenter = String.format("%-" + iMinNrOfCharToShow  + "s", strToCenter);

        return strToCenter;
    }
}