package com.pretolesi.easyscada.Control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.pretolesi.easyscada.BaseFragment;
import com.pretolesi.easyscada.CommClientData.BaseCommClient;
import com.pretolesi.easyscada.CustomControls.LabelTextView;
import com.pretolesi.easyscada.CustomControls.NumericDataType;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.IO.ClientReadStatus;
import com.pretolesi.easyscada.IO.ClientWriteStatus;
import com.pretolesi.easyscada.TcpIpClient.TCPIPClient;
import com.pretolesi.easyscada.TcpIpClient.CommClientHelper;

/**
 *
 */
public class LightSwitchControl extends Switch implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ToggleButton.OnClickListener,
        TCPIPClient.TcpIpClientReadValueStatusListener,
        TCPIPClient.TcpIpClientWriteStatusListener {

    private NumericDataType.DataType m_dtDataType;

    private GestureDetectorCompat mDetector;
    private float mLastTouchX;
    private float mLastTouchY;

    private ControlData m_cd;
    private int m_iMsgID;
    private boolean m_iTIDReadClicked;
    private int m_iTIDRead;
    private int m_iTIDOFF;
    private int m_iTIDOFFON;
    private int m_iTIDONOFF;
    private int m_iTIDON;

    private boolean m_bEditMode;
    private boolean m_bVertical;

    private Handler m_TimerHandler;
    private long m_lRepeatingTime;

    // Label for Switch
    private LabelTextView m_LabelTextView;

    public LightSwitchControl(Context context) {
        super(context);
        m_LabelTextView = null;
        this.m_cd = null;
        this.m_iMsgID = -1;
        this.m_iTIDReadClicked = false;
        this.m_iTIDRead = -1;
        this.m_iTIDOFF = -1;
        this.m_iTIDOFFON = -1;
        this.m_iTIDONOFF = -1;
        this.m_iTIDON = -1;
        setNumericDataType(DataType.SHORT);
        setEditMode(false);
        setVertical(false);
        setTextOff("0");
        setTextOn("1");
    }

    public LightSwitchControl(Context context, ControlData cd, int iMsgID, boolean bEditMode) {
        super(context);
        m_LabelTextView = null;
        if(cd != null) {
            this.m_cd = cd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDReadClicked = false;
            this.m_iTIDRead = m_iMsgID + 1;
            this.m_iTIDOFF = m_iMsgID + 2;
            this.m_iTIDOFFON = m_iMsgID + 3;
            this.m_iTIDONOFF = m_iMsgID + 4;
            this.m_iTIDON = m_iMsgID + 5;
            this.setTag(cd.getTag());
            setNumericDataType(DataType.SHORT);
            setEditMode(bEditMode);
            setVertical(m_cd.getVertical());
            if(cd.getValueReadOnly()){
                this.setClickable(false);
            }
        }
        setTextOff("0");
        setTextOn("1");
    }

    protected void setEditMode(boolean bEditMode){
        m_bEditMode = bEditMode;
    }

    protected void setNumericDataType(NumericDataType.DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    public void setVertical(boolean bVertical) { this.m_bVertical = bVertical; }

    protected boolean getEditMode(){
        return m_bEditMode;
    }

    protected NumericDataType.DataType getNumericDataType(){
        return m_dtDataType;
    }

    public boolean getVertical() { return m_bVertical; }

    /*
     * End
     * Timer variable and function
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Label Text View
        ViewParent view = this.getParent();
        if(view != null && view instanceof RelativeLayout) {
            m_LabelTextView = new LabelTextView(getContext());
            ((RelativeLayout) view).addView(m_LabelTextView);
        }

        // Listener
        if(m_cd != null){
            if(!getEditMode() && m_cd.getTranspProtocolEnable()) {
                BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_cd.getTranspProtocolID());
                if(bcc != null){
                    bcc.registerTcpIpClientReadValueStatus(this);
                    bcc.registerTcpIpClientWriteSwitchStatus(this);
                }
                setTimer(m_cd.getValueUpdateMillis());
            }
        }

        setOnClickListener(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        setOnClickListener(null);

        if(!getEditMode()) {
            resetTimer();
        }

        // Listener
        if(m_cd != null){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_cd.getTranspProtocolID());
            if(bcc != null){
                bcc.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Delete
        m_LabelTextView = null;
    }

//    private synchronized void readValue(){
    private void readValue(){
        if(m_cd != null && m_cd.getTranspProtocolEnable()){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_cd.getTranspProtocolID());
            if(bcc != null){
                bcc.readValue(getContext(), m_iTIDRead, m_cd.getTranspProtocolUI(), m_cd.getTranspProtocolDataAddress(), getNumericDataType());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed,  int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b); }

    @Override
    protected void onDraw (Canvas canvas){
        super.onDraw(canvas);
        RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
        if(rllp != null) {
            if(m_LabelTextView != null) {
                m_LabelTextView.setPosition(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
                if(getTag() != null && getTag() instanceof String){
                    m_LabelTextView.setText((String) getTag());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        m_iTIDReadClicked = true;
        writeSwitchValue(((Switch) v).isChecked());
    }

    private void writeSwitchValue(boolean bValue){
        if(m_cd != null && m_cd.getTranspProtocolEnable()){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_cd.getTranspProtocolID());
            if(bcc != null){
                Short sh;
                if(bValue) {
                    sh = (short) m_cd.getWriteValueON();
                    bcc.writeValue(getContext(), m_iTIDON, m_cd.getTranspProtocolUI(), m_cd.getTranspProtocolDataAddress(), sh);
                 } else {
                    sh = (short) m_cd.getWriteValueOFF();
                    bcc.writeValue(getContext(), m_iTIDON, m_cd.getTranspProtocolUI(), m_cd.getTranspProtocolDataAddress(), sh);
                }
            }
        }
    }

    @Override
    public void onReadValueStatusCallback(ClientReadStatus ticrs) {
        if(ticrs != null && m_cd != null){
            if(ticrs.getServerID() == m_cd.getTranspProtocolID()){
                if(ticrs.getTID() == m_iTIDRead) {
                    if(!m_iTIDReadClicked) {
                        // Only Short
                        if(ticrs.getStatus() == ClientReadStatus.Status.OK) {
                            if (ticrs.getValue() != null) {
                                if(ticrs.getValue() instanceof Short){
                                    Short sh = (Short)ticrs.getValue();
                                    if(sh == m_cd.getWriteValueON()){
                                        setChecked(true);
                                    }
                                    if(sh == m_cd.getWriteValueOFF()){
                                        setChecked(false);
                                    }
                                    this.setError(null);
                                }
                                if(ticrs.getValue() instanceof Integer){
                                    Integer i = (Integer)ticrs.getValue();
                                    this.setError(null);
                                }
                                if(ticrs.getValue() instanceof Long){
                                    Long l = (Long)ticrs.getValue();
                                    this.setError(null);
                                }

                                if(ticrs.getValue() instanceof Float){
                                    Float f = (Float)ticrs.getValue();
                                    this.setError(null);
                                }

                                if(ticrs.getValue() instanceof Double){
                                    Double dbl = (Double)ticrs.getValue();
                                    this.setError(null);
                                }
                            } else {
                                this.setError(ticrs.getErrorMessage());
                            }
                        } else {
                            this.setError(ticrs.getErrorMessage());
                        }
                    }
                    m_iTIDReadClicked = false;
                }
            }
        }
    }

    @Override
    public void onWriteValueStatusCallback(ClientWriteStatus ticws) {

        if(ticws != null && m_cd != null){
            if(ticws.getTID() == m_iTIDOFF || ticws.getTID() == m_iTIDOFFON || ticws.getTID() == m_iTIDONOFF || ticws.getTID() == m_iTIDON) {
                if(ticws.getServerID() == m_cd.getTranspProtocolID()) {
                    if(ticws.getStatus() == ClientWriteStatus.Status.OK){
                        this.setError(null);
                    } else {
                        this.setError(ticws.getErrorMessage());
                    }
                }
            }
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
        if(m_cd != null && !m_cd.getValueWriteOnly()) {
            readValue();
        }
     }
    /*
     * End
     * Timer variable and function
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        if(m_bEditMode){
            if(mDetector == null){
                // Instantiate the gesture detector with the
                // application context and an implementation of
                // GestureDetector.OnGestureListener
                mDetector = new GestureDetectorCompat(getContext(),this);
                // Set the gesture detector as the double tap
                // listener.
                mDetector.setOnDoubleTapListener(this);
            }

            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    if(m_cd != null) {
                        m_cd.setSaved(false);
                        Intent intent = LightSwitchControlPropActivity.makeBaseValuePropActivityByValueData(this.getContext(), LightSwitchControlPropActivity.class, m_cd);
                        this.getContext().startActivity(intent);
                    }
                    break;
                }
            }

            this.mDetector.onTouchEvent(event);
            return true;
        } else {
            if(m_cd != null && m_cd.getValueReadOnly()) {
              if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP ){
                   return true;
                }
            }
            // Disable Sliding
            if(action == MotionEvent.ACTION_MOVE){
                return true;
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
    public boolean onDown(MotionEvent event) {
        if(m_cd != null) {
            m_cd.setSaved(false);
        }

        if(this.getLayoutParams() instanceof RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
            mLastTouchX = event.getRawX() - rllp.leftMargin;
            mLastTouchY = event.getRawY() - rllp.topMargin;
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
        final float dx = x - mLastTouchX;
        final float dy = y - mLastTouchY;

        if(m_cd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_cd.setPosX((int) dx);
            m_cd.setPosY((int) dy);

        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return false;
    }

}
