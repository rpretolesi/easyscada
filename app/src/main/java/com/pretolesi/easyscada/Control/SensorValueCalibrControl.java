package com.pretolesi.easyscada.Control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.pretolesi.easyscada.Control.ControlData.SensorTypeCalibr;
import java.util.List;

/**
 *
 */
public class SensorValueCalibrControl extends SensorValueBaseControl {

    private static final String TAG = "SensorValueCalibr";

    // Sensors & SensorManager
    private SensorManager m_SensorManager = null;
    private Sensor m_SensorAccelerometer = null;
    private Sensor m_SensorMagnetometer = null;

    // Storage for Sensor readings
    private float[] m_Gravity = null;
    private float[] m_Geomagnetic = null;
    private float[] m_RotationMatrix = null;
    private float[] m_OrientationMatrix = null;
    private float[] m_RotationInDegress = null;

    public SensorValueCalibrControl(Context context) {
        super(context);
    }

    public SensorValueCalibrControl(Context context, ControlData bvd, int iMsgID, boolean bEditMode) {
        super(context, bvd, iMsgID, bEditMode);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Create array for value
        m_Gravity = new float[3];
        m_Geomagnetic = new float[3];
        m_RotationMatrix = new float[9];
        m_OrientationMatrix = new float[3];
        m_RotationInDegress = new float[6];

        // Sensor
        boolean bSensorOk = false;
        if(m_bvd != null && !m_bvd.getSensorEnableSimulation()) {
            // Get reference to SensorManager
            m_SensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (m_SensorManager != null) {
                List<SensorTypeCalibr> alstc = SensorTypeCalibr.getListSensorTypeCalibr();
                if(alstc != null) {
                    SensorTypeCalibr stc = alstc.get((int) m_bvd.getSensorTypeID());
                    switch (stc) {
                        case COMPASS:
                            // Get reference to Sensor
                            // Get a reference to the accelerometer
                            m_SensorAccelerometer = m_SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                            // Get a reference to the magnetometer
                            m_SensorMagnetometer = m_SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                            // Exit unless both sensors are available
                            if (m_SensorAccelerometer != null && m_SensorMagnetometer != null){
                                m_SensorManager.registerListener(this, m_SensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                                m_SensorManager.registerListener(this, m_SensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
                                bSensorOk = true;
                            }
                            break;
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
        m_Gravity = null;
        m_Geomagnetic = null;
        m_RotationMatrix = null;
        m_OrientationMatrix = null;
        m_RotationInDegress = null;
    }

    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        if(m_bvd != null){
            // Acquire accelerometer event data
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if(m_Gravity != null){
                    System.arraycopy(event.values, 0, m_Gravity, 0, 3);
                }
            }

            // Acquire magnetometer event data
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                if(m_Geomagnetic != null){
                    System.arraycopy(event.values, 0, m_Geomagnetic, 0, 3);
                }
            }

            List<SensorTypeCalibr> alstc = SensorTypeCalibr.getListSensorTypeCalibr();
            if(alstc != null) {
                SensorTypeCalibr stc = alstc.get((int) m_bvd.getSensorTypeID());
                switch (stc) {
                    case COMPASS:
                        if (m_Gravity != null && m_Geomagnetic != null && m_RotationMatrix != null && m_OrientationMatrix != null && m_RotationInDegress != null) {
                            // Users the accelerometer and magnetometer readings
                            // to compute the device's rotation with respect to
                            // a real world coordinate system
                            boolean bRes = SensorManager.getRotationMatrix(m_RotationMatrix, null, m_Gravity, m_Geomagnetic);
                            if (bRes) {
                                // Returns the device's orientation given
                                // the rotationMatrix
                                SensorManager.getOrientation(m_RotationMatrix, m_OrientationMatrix);

                                // Get the rotation, measured in radians, around the Z-axis
                                // Note: This assumes the device is held flat and parallel
                                // to the ground
                                // Convert from radians to degrees
                                m_RotationInDegress[0] = (float)Math.toDegrees(m_OrientationMatrix[0]);
                                m_RotationInDegress[1] = (float)Math.toDegrees(m_OrientationMatrix[1]);
                                m_RotationInDegress[2] = (float)Math.toDegrees(m_OrientationMatrix[2]);
                                setOutputFilter(m_RotationInDegress);
                            }
                        }
                        break;
                }
            }
        }
    }
}
