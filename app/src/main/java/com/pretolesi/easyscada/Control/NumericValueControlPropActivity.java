package com.pretolesi.easyscada.Control;

import android.os.Bundle;
import android.widget.CheckBox;

import com.pretolesi.easyscada.CustomControls.NumericEditText;
import com.pretolesi.easyscada.CustomControls.StringEditText;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.dialogs.DialogActionID;
import com.pretolesi.easyscada.dialogs.OkDialogFragment;

/**
 *
 */
public class NumericValueControlPropActivity extends ControlDataPropActivity {
    private static final String TAG = "NumericValuePropAct";

    private NumericEditText m_id_et_min_nr_char_to_show;
    private NumericEditText m_id_et_nr_of_decimal;
    private StringEditText m_id_et_um;
    private NumericEditText m_id_et_update_millis;
    private CheckBox m_id_cb_read_only;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeric_value_property_activity);

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
        m_id_et_update_millis = (NumericEditText)findViewById(R.id.id_et_update_millis);
        m_id_et_update_millis.setInputLimit(ControlData.ValueUpdateMillisMinValue, ControlData.ValueUpdateMillisMaxValue);
        m_id_et_update_millis.setText(ControlData.ValueUpdateMillisDefaulValue);
        m_id_cb_read_only = (CheckBox)findViewById(R.id.id_cb_read_only);

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
        if (m_id_et_update_millis != null) {
            m_id_et_update_millis.setText(Integer.toString(m_cd.getValueUpdateMillis()));
        }
        if (m_id_cb_read_only != null) {
            m_id_cb_read_only.setChecked(m_cd.getValueReadOnly());
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
            if (m_id_et_update_millis != null) {
                m_cd.setValueUpdateMillis(Integer.parseInt(m_id_et_update_millis.getText().toString()));
            }
            if (m_id_cb_read_only != null) {
                m_id_cb_read_only.setChecked(m_cd.getValueReadOnly());
            }

        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        return bRes;
    }
}
