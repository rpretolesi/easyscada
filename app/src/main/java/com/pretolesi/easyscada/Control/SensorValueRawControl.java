package com.pretolesi.easyscada.Control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.List;

/**
 *
 */
public class SensorValueRawControl extends SensorValueBaseControl {

    private static final String TAG = "SensorValue";

    // Sensors & SensorManager
    private SensorManager m_SensorManager = null;
    private Sensor m_Sensor = null;
    private int m_iSensorType;

    // Storage for Sensor readings
    private float[] m_SensorValue = null;

    public SensorValueRawControl(Context context) {
        super(context);
    }

    public SensorValueRawControl(Context context, ControlData bvd, int iMsgID, boolean bEditMode) {
        super(context, bvd, iMsgID, bEditMode);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Create array for value
        m_SensorValue = new float[6];

        // Sensor
        boolean bSensorOk = false;
        if(m_bvd != null && !m_bvd.getSensorEnableSimulation()) {
            // Get reference to SensorManager
            m_SensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (m_SensorManager != null) {
                // Get reference to Sensor
                List<Sensor> ls = m_SensorManager.getSensorList(Sensor.TYPE_ALL);
                if(ls != null){
                    m_iSensorType = ls.get((int) m_bvd.getSensorTypeID()).getType();
                    if(m_iSensorType > 0){
                        m_Sensor = m_SensorManager.getDefaultSensor(m_iSensorType);
                        if (m_Sensor != null) {
                            // Create array for value
                            // Sensor Available
                            m_SensorManager.registerListener(this, m_Sensor, SensorManager.SENSOR_DELAY_UI);
                            bSensorOk = true;
                        }
                    }
                }
            }
        }
        if(bSensorOk){
            this.setError(null);
        } else {
            this.setError("");
        }

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Sensor
        // Get reference to SensorManager
        if(m_SensorManager != null) {
            m_SensorManager.unregisterListener(this);
        }
        // Create array for value
        m_SensorValue = null;
    }

    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        if(m_SensorValue != null && m_bvd != null){
            if(event.sensor.getType() == m_iSensorType){
                System.arraycopy(event.values, 0, m_SensorValue, 0, event.values.length);
                setOutputFilter(m_SensorValue);
            }
        }
    }

}
