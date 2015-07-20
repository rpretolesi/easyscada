package com.pretolesi.easyscada.Control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.pretolesi.easyscada.Control.ControlData.SensorTypeCalibr;
import com.pretolesi.easyscada.CustomControls.NumericEditText;
import com.pretolesi.easyscada.CustomControls.StringEditText;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.dialogs.DialogActionID;
import com.pretolesi.easyscada.dialogs.OkDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SensorValueControlPropActivity extends ControlDataPropActivity {

    private NumericEditText m_id_et_min_nr_char_to_show;
    private NumericEditText m_id_et_nr_of_decimal;
    private StringEditText m_id_et_um;

    // Sensor
    private Spinner m_id_spn_sensor_type;
    private Spinner m_id_spn_sensor_value;
    private CheckBox m_id_cb_sensor_enable_simulation;
    private NumericEditText m_id_et_sensor_ampl_k;
    private NumericEditText m_id_et_sensor_low_pass_filter_k;
    private NumericEditText m_id_et_sensor_sample_time_millis;
    private NumericEditText m_id_et_sensor_write_update_millis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_value_property_activity);

        // Base
        onBaseCreate();

        m_id_et_min_nr_char_to_show = (NumericEditText)findViewById(R.id.id_et_min_nr_char_to_show);
        m_id_et_min_nr_char_to_show.setInputLimit(ControlData.ValueMinNrCharToShowMinValue, ControlData.ValueMinNrCharToShowMaxValue);
        m_id_et_min_nr_char_to_show.setText(ControlData.ValueMinNrCharToShowDefaulValue);
        m_id_et_nr_of_decimal = (NumericEditText)findViewById(R.id.id_et_nr_of_decimal);
        m_id_et_nr_of_decimal.setInputLimit(ControlData.ValueNrOfDecimalMinValue, ControlData.ValueNrOfDecimalMaxValue);
        m_id_et_nr_of_decimal.setText(ControlData.ValueNrOfDecimalDefaulValue);
        m_id_et_um = (StringEditText)findViewById(R.id.id_et_um);
        m_id_et_um.setInputLimit(ControlData.ValueUMMinValue, ControlData.ValueUMMaxValue);
        m_id_et_um.setText(ControlData.ValueUMDefaulValue);

        // Sensor
        m_id_spn_sensor_type = (Spinner)findViewById(R.id.id_spn_sensor_type);
        m_id_spn_sensor_type.setSelection(-1);
        m_id_spn_sensor_value = (Spinner)findViewById(R.id.id_spn_sensor_value);
        m_id_spn_sensor_value.setSelection(-1);
        m_id_cb_sensor_enable_simulation = (CheckBox)findViewById(R.id.id_cb_sensor_enable_simulation);
        m_id_et_sensor_ampl_k = (NumericEditText)findViewById(R.id.id_et_sensor_ampl_k);
        m_id_et_sensor_ampl_k.setInputLimit(ControlData.SensorAmplKMinValue, ControlData.SensorAmplKMaxValue);
        m_id_et_sensor_ampl_k.setText(ControlData.SensorAmplKDefaulValue);
        m_id_et_sensor_low_pass_filter_k = (NumericEditText)findViewById(R.id.id_et_sensor_low_pass_filter_k);
        m_id_et_sensor_low_pass_filter_k.setInputLimit(ControlData.SensorLowPassFilterKMinValue, ControlData.SensorLowPassFilterKMaxValue);
        m_id_et_sensor_low_pass_filter_k.setText(ControlData.SensorLowPassFilterKDefaulValue);
        m_id_et_sensor_sample_time_millis = (NumericEditText)findViewById(R.id.id_et_sensor_sample_time);
        m_id_et_sensor_sample_time_millis.setInputLimit(ControlData.SensorSampleTimeMinValue, ControlData.SensorSampleTimeMaxValue);
        m_id_et_sensor_sample_time_millis.setText(ControlData.SensorSampleTimeDefaulValue);
        m_id_et_sensor_write_update_millis = (NumericEditText)findViewById(R.id.id_et_sensor_write_update_time);
        m_id_et_sensor_write_update_millis.setInputLimit(ControlData.SensorWriteUpdateTimeMinValue, ControlData.SensorWriteUpdateTimeMaxValue);
        m_id_et_sensor_write_update_millis.setText(ControlData.SensorWriteUpdateTimeDefaulValue);

        // Get sensor list
        int iTypeParameter;
        if(m_cdParameter != null){
            iTypeParameter = m_cdParameter.getTypeID();
        } else {
            iTypeParameter = m_lControlTypeParameter;
        }

        if(iTypeParameter == ControlData.ControlType.RAW_SENSOR.getID()){
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sm != null){
                List<Sensor> ls = sm.getSensorList(Sensor.TYPE_ALL);
                m_id_spn_sensor_type.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, ls));
            }
        } else if(iTypeParameter == ControlData.ControlType.CAL_SENSOR.getID()){
            ArrayList<SensorTypeCalibr> alstc = SensorTypeCalibr.getListSensorTypeCalibr();
            m_id_spn_sensor_type.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, alstc));

        }
        m_id_spn_sensor_value.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ControlData.SensorValue.values()));

    }

    @Override
    protected void getBaseValue() {
        super.getBaseValue();

        // Dati
        if (m_cd == null) {
            return ;
        }

        if (m_id_et_min_nr_char_to_show != null) {
            m_id_et_min_nr_char_to_show.setText(Integer.toString(m_cd.getValueMinNrCharToShow()));
        }
        if (m_id_et_nr_of_decimal != null) {
            m_id_et_nr_of_decimal.setText(Integer.toString(m_cd.getValueNrOfDecimal()));
        }
        if (m_id_et_um != null) {
            m_id_et_um.setText(m_cd.getValueUM());
        }

        // Sensor
        if(m_id_spn_sensor_type != null) {
            long lItem = -1;
            try{
                lItem = m_cd.getSensorTypeID();
            } catch (Exception ignore) { }
            m_id_spn_sensor_type.setSelection((int) lItem);
        }
        if(m_id_spn_sensor_value != null) {
            long lItem = -1;
            try{
                lItem = m_cd.getSensorValueID();
            } catch (Exception ignore) { }
            m_id_spn_sensor_value.setSelection((int) lItem);
        }
        if(m_id_cb_sensor_enable_simulation != null) {
            m_id_cb_sensor_enable_simulation.setChecked(m_cd.getSensorEnableSimulation());
        }
        if (m_id_et_sensor_ampl_k != null) {
            m_id_et_sensor_ampl_k.setText(Float.toString(m_cd.getSensorAmplK()));
        }
        if (m_id_et_sensor_low_pass_filter_k != null) {
            m_id_et_sensor_low_pass_filter_k.setText(Float.toString(m_cd.getSensorLowPassFilterK()));
        }
        if (m_id_et_sensor_sample_time_millis != null) {
            m_id_et_sensor_sample_time_millis.setText(Integer.toString(m_cd.getSensorSampleTimeMillis()));
        }
        if (m_id_et_sensor_write_update_millis != null) {
            m_id_et_sensor_write_update_millis.setText(Integer.toString(m_cd.getSensorWriteUpdateTimeMillis()));
        }
    }

    @Override
    protected boolean setBaseData(int iDialogOriginID){
        boolean bRes = super.setBaseData(iDialogOriginID);
        // Dati
        if (m_cd == null) {
            return false;
        }

        try {
            if (m_id_et_min_nr_char_to_show != null) {
                m_cd.setValueMinNrCharToShow(Integer.parseInt(m_id_et_min_nr_char_to_show.getText().toString()));
            }
            if (m_id_et_nr_of_decimal != null) {
                m_cd.setValueNrOfDecimal(Integer.parseInt(m_id_et_nr_of_decimal.getText().toString()));
            }
            if (m_id_et_um != null) {
                m_cd.setValueUM(m_id_et_um.getText().toString());
            }

            // Sensor
            if(m_id_spn_sensor_type != null) {
                m_cd.setSensorTypeID(m_id_spn_sensor_type.getSelectedItemId());
            }

            if(m_id_spn_sensor_value != null) {
                m_cd.setSensorValueID(m_id_spn_sensor_value.getSelectedItemId());
            }
            if(m_id_cb_sensor_enable_simulation != null) {
                m_cd.setSensorEnableSimulation(m_id_cb_sensor_enable_simulation.isChecked());
            }
            if (m_id_et_sensor_ampl_k != null) {
                m_cd.setSensorAmplK(Float.parseFloat(m_id_et_sensor_ampl_k.getText().toString()));
            }
            if (m_id_et_sensor_low_pass_filter_k != null) {
                m_cd.setSensorLowPassFilterK(Float.parseFloat(m_id_et_sensor_low_pass_filter_k.getText().toString()));
            }
            if (m_id_et_sensor_sample_time_millis != null) {
                m_cd.setSensorSampleTimeMillis(Integer.parseInt(m_id_et_sensor_sample_time_millis.getText().toString()));
            }
            if (m_id_et_sensor_write_update_millis != null) {
                m_cd.setSensorWriteUpdateTimeMillis(Integer.parseInt(m_id_et_sensor_write_update_millis.getText().toString()));
            }

        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        return bRes;
    }
}
