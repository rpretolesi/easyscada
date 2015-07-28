package com.pretolesi.easyscada.Control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.pretolesi.easyscada.CommClientData.BaseCommClient;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.TcpIpClient.TCPIPClient;
import com.pretolesi.easyscada.TcpIpClient.CommClientHelper;
import com.pretolesi.easyscada.IO.ClientWriteStatus;

/**
 *
 */
public class SensorValueBaseControl extends Control implements
        TCPIPClient.TcpIpClientWriteStatusListener,
        SensorEventListener {

    private static final String TAG = "SensorValueBase";

    protected ControlData m_bvd;
    private int m_iMsgID;
    private int m_iTIDWrite;

    // Storage for Sensor readings
    private float[] m_SensorValueFiltered = null;
    private float[] m_SensorValueOut = null;

    private long m_lTimeLast;

    public SensorValueBaseControl(Context context) {
        super(context);
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDWrite = -1;
    }

    public SensorValueBaseControl(Context context, ControlData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDWrite = m_iMsgID + 1;
            this.setTag(bvd.getTag());

            setNumericDataType(DataType.getDataType(m_bvd.getTranspProtocolDataType()));
            setEditMode(bEditMode);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Create array for value
        m_SensorValueFiltered = new float[6];
        m_SensorValueOut = new float[6];

        // Set default value
        setText(getDefaultValue());

        // Listener
        if(m_bvd != null){
            if(!getEditMode() && m_bvd.getTranspProtocolEnable()) {
                BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getTranspProtocolID());
                if(bcc != null){
                    bcc.registerTcpIpClientWriteSwitchStatus(this);
                }
                if(!m_bvd.getSensorEnableSimulation()) {
                    setTimer(m_bvd.getSensorWriteUpdateTimeMillis());
                }
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(!getEditMode()) {
            resetTimer();
        }

        // Listener
        if(m_bvd != null){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getTranspProtocolID());
            if(bcc != null){
                bcc.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Create array for value
        m_SensorValueFiltered = null;
        m_SensorValueOut = null;
    }
    @Override
    protected void onDraw (Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    protected void OnWriteInputField(String strValue){
        super.OnWriteInputField(strValue);
        WriteInputField(strValue);
    }

    private String getDefaultValue(){
        String strDefaultValue = "";

        if(m_bvd != null){
             for(int iIndice = m_bvd.getValueMinNrCharToShow() + m_bvd.getValueNrOfDecimal(); iIndice > 0; iIndice--){
                 if(iIndice == m_bvd.getValueNrOfDecimal()){
                     strDefaultValue = strDefaultValue + ".";
                 }
                 strDefaultValue = strDefaultValue + "#";
             }
            if(m_bvd.getValueUM() != null && !m_bvd.getValueUM().equals("")){
                strDefaultValue = strDefaultValue + " " + m_bvd.getValueUM();
            }
        } else {
            strDefaultValue = ControlData.ValueDefaulValue;
        }

        return strDefaultValue;
    }
    private String getErrorValue(int iErrorCode){
        return "Error Code: " + iErrorCode;
    }

    private String getTimeoutValue(){
        return "Timeout";
    }

    @Override
    public void onWriteValueStatusCallback(ClientWriteStatus ticws) {
        if(ticws != null && m_bvd != null){
            if(ticws.getServerID() == m_bvd.getTranspProtocolID()){
                if(ticws.getTID() == m_iTIDWrite) {
                    if(ticws.getStatus() == ClientWriteStatus.Status.OK){
                        if(m_bvd.getSensorEnableSimulation()){
                            // Write Ok, i can close the Input
                            closeInputField();
                        } else {
                            setError(null);
                            this.setTextAlarm(null);
                        }
                    } else {
                        if(m_bvd.getSensorEnableSimulation()){
                            // Write Ok, i can close the Input
                            setErrorInputField(ticws.getErrorMessage());
                        } else {
                            requestFocus();
                            setError(ticws.getErrorMessage());
                            this.setTextAlarm(ticws.getErrorMessage());
                        }
                    }
                }
            }
        }
    }

    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onTouchActionUp(boolean bEditMode){
        super.onTouchActionUp(bEditMode);
        if(m_bvd != null) {
            if(bEditMode) {
                m_bvd.setSaved(false);
                m_bvd.setPosX((int)getX());
                m_bvd.setPosY((int)getY());
                Intent intent = SensorValueControlPropActivity.makeBaseValuePropActivityByValueData(this.getContext(), SensorValueControlPropActivity.class, m_bvd);
                this.getContext().startActivity(intent);
            } else {
                if(m_bvd.getSensorEnableSimulation()){
                    openInputField(getDefaultValue());
                }
            }
        }
    }

    @Override
    protected synchronized void onTimer(){
        super.onTimer();
        if(m_bvd != null && !m_bvd.getSensorEnableSimulation()) {
            // Write Sensor Data
            String strData = convertFormatValueToString(getNumericDataType(), m_SensorValueOut[(int) m_bvd.getSensorValueID()]);
            WriteInputField(strData);
        }
    }

    protected void WriteInputField(String strValue){
        if(m_bvd != null && m_bvd.getTranspProtocolEnable()) {
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getTranspProtocolID());
            if (bcc != null) {
                bcc.writeValue(getContext(), m_iTIDWrite, m_bvd.getTranspProtocolUI(), m_bvd.getTranspProtocolDataAddress(), getNumericDataType(), strValue);
            }
        }
    }

    protected void setOutputFilter(float[] afSensorValue ){
        long lTimeNow = System.currentTimeMillis();
        if(afSensorValue != null && m_bvd != null && m_SensorValueFiltered != null && m_SensorValueOut!= null) {
            if (lTimeNow - m_lTimeLast > m_bvd.getSensorSampleTimeMillis()) {
                m_lTimeLast = lTimeNow;

                // Apply low-pass filter
                for (int i = 0; i < afSensorValue.length; i++) {
                    m_SensorValueFiltered[i] = lowPass(afSensorValue[i], m_SensorValueFiltered[i], m_bvd.getSensorLowPassFilterK());
                    m_SensorValueOut[i] = m_SensorValueFiltered[i] * m_bvd.getSensorAmplK();
                }
                // Show Sensor Data
                String strValue = "";
                if(m_bvd.getValueMinNrCharToShow() > 0){
                    strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + "." + m_bvd.getValueNrOfDecimal() + "f %s", (double) m_SensorValueOut[(int) m_bvd.getSensorValueID()], m_bvd.getValueUM());
                } else {
                    strValue = String.format("%." + m_bvd.getValueNrOfDecimal() + "f %s", (double) m_SensorValueOut[(int) m_bvd.getSensorValueID()], m_bvd.getValueUM());
                }
                setText(strValue);
            }
        }
    }

    private String convertFormatValueToString(DataType dtDataType, double dblValueToConvert){
        String strValue = "0";
        if(dtDataType != null){
            long lValue = 0;
            try{
                lValue = Math.round(dblValueToConvert);
            } catch (Exception ignore){
            }

            switch (dtDataType) {
                case SHORT:
                    try {
                        strValue = Integer.toString((int)lValue);

                    } catch (Exception ignore) {
                    }
                    break;

                case INT:
                    try {
                        strValue = Integer.toString((int)lValue);
                    } catch (Exception ignore) {
                    }
                    break;

                case LONG:
                    try {
                        strValue = Long.toString(lValue);
                    } catch (Exception ignore) {
                    }
                    break;

                case FLOAT:
                    try {
                        strValue = Float.toString((float) dblValueToConvert);
                    } catch (Exception ignore) {
                    }
                    break;

                case DOUBLE:
                    try {
                        strValue = Double.toString(dblValueToConvert);
                    } catch (Exception ignore) {
                    }
                    break;

            }
        }

        return strValue;
    }

    // Deemphasize transient forces
    public static float lowPass(float current, float last, float alpha) {
        return last * alpha + current * ((float)1.0 - alpha);
    }
}
