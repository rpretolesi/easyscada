package com.pretolesi.easyscada.Control;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easyscada.CustomControls.NumericDataType;
import com.pretolesi.easyscada.CustomControls.NumericEditText;
import com.pretolesi.easyscada.CustomControls.StringEditText;
import com.pretolesi.easyscada.LoadersUtils.Loaders;
import com.pretolesi.easyscada.Settings.Orientation;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.CommClientData.TranspProtocolData;
import com.pretolesi.easyscada.dialogs.DialogActionID;
import com.pretolesi.easyscada.dialogs.DialogOriginID;
import com.pretolesi.easyscada.dialogs.OkDialogFragment;
import com.pretolesi.easyscada.dialogs.YesNoDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class ControlDataPropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{

    protected static final String ROOM_ID = "RoomID";
    protected static final String CONTROL_TYPE = "ControlType";
    protected static final String CONTROL_ID = "ControlID";
    protected static final String CONTROL_DATA = "ControlData";

 //   private CharSequence mTitle;

    protected Spinner m_id_spn_room;
    protected SimpleCursorAdapter m_SCAdapter;

    protected StringEditText m_id_et_name;
    protected EditText m_id_et_position_x;
    protected EditText m_id_et_position_y;
    protected EditText m_id_et_position_z;
    protected RadioButton m_id_rb_horizontal;
    protected RadioButton m_id_rb_vertical;

    protected CheckBox m_id_cb_enable_transp_protocol;
    protected Spinner m_id_spn_transp_protocol;

    protected NumericEditText m_id_et_protocol_ui;
    protected NumericEditText m_id_et_protocol_data_addr;

    protected Spinner m_id_spn_protocol_data_type;

    protected ControlData m_cd;
    protected int m_lControlTypeParameter;
    protected long m_lRoomIDParameter;
    protected long m_lControlIDParameter;
    protected ControlData m_cdParameter;
    protected SimpleCursorAdapter m_TranspProtocolAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onBaseCreate() {
        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room_name);
        m_id_et_name = (StringEditText)findViewById(R.id.id_et_name);
        m_id_et_name.setInputLimit(ControlData.TAGMinChar, ControlData.TAGMaxChar);
        m_id_et_name.setText(ControlData.TAGDefaultValue);
        m_id_rb_horizontal = (RadioButton)findViewById(R.id.id_rb_horizontal);
        m_id_rb_horizontal.setChecked(true);
        m_id_rb_vertical = (RadioButton)findViewById(R.id.id_rb_vertical);
        m_id_rb_vertical.setChecked(false);
        m_id_et_position_x = (EditText)findViewById(R.id.id_et_position_x);
        m_id_et_position_x.setText(ControlData.PosXDefaultValue);
        m_id_et_position_y = (EditText)findViewById(R.id.id_et_position_y);
        m_id_et_position_y.setText(ControlData.PosYDefaultValue);
        m_id_et_position_z = (EditText)findViewById(R.id.id_et_position_z);
        m_id_et_position_z.setText(ControlData.PosZDefaultValue);

        m_id_cb_enable_transp_protocol = (CheckBox)findViewById(R.id.id_cb_enable_tcp_ip_client_protocol);
        m_id_cb_enable_transp_protocol.setEnabled(false);
        m_id_cb_enable_transp_protocol.setChecked(false);
        m_id_spn_transp_protocol = (Spinner)findViewById(R.id.id_spn_tcp_ip_client_protocol);
        m_id_spn_transp_protocol.setEnabled(false);

        m_id_cb_enable_transp_protocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_id_spn_transp_protocol.setEnabled(isChecked);
                m_id_et_protocol_ui.setEnabled(isChecked);
                m_id_et_protocol_data_addr.setEnabled(isChecked);
            }
        });

        m_id_spn_transp_protocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set Limit for address
                // and check again
                TranspProtocolData.CommProtocolType p = null;
                Cursor cursor = SQLContract.TranspProtocolEntry.load(id);
                ArrayList<TranspProtocolData> alticd = SQLContract.TranspProtocolEntry.get(cursor);
                if (alticd != null && !alticd.isEmpty()) {
                    p = alticd.get(0).getCommProtocolType();
                }
                if (p != null && p == TranspProtocolData.CommProtocolType.MODBUS_ON_SERIAL) {
                    if (m_id_et_protocol_ui != null) {
                        m_id_et_protocol_ui.setInputLimit(1, 247);
                    }
                }
                if (p != null && p == TranspProtocolData.CommProtocolType.MODBUS_ON_TCP_IP) {
                    if (m_id_et_protocol_ui != null) {
                        m_id_et_protocol_ui.setInputLimit(0, 247);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_id_et_protocol_ui = (NumericEditText)findViewById(R.id.id_et_protocol_ui);
        m_id_et_protocol_ui.setInputLimit(ControlData.ProtTcpIpClientValueIDMinValue, ControlData.ProtTcpIpClientValueIDMaxValue);
        m_id_et_protocol_ui.setText(ControlData.ProtTcpIpClientValueIDDefaulValue);
        m_id_et_protocol_ui.setEnabled(false);
        m_id_et_protocol_data_addr = (NumericEditText)findViewById(R.id.id_et_protocol_data_addr);
        m_id_et_protocol_data_addr.setInputLimit(ControlData.ProtTcpIpClientValueAddressMin, ControlData.ProtTcpIpClientValueAddressMax);
        m_id_et_protocol_data_addr.setText(ControlData.ProtTcpIpClientValueAddressDefaul);
        m_id_et_protocol_data_addr.setEnabled(false);

        m_id_spn_protocol_data_type = (Spinner)findViewById(R.id.id_spn_protocol_data_type);
        m_id_spn_protocol_data_type.setSelection(ControlData.ProtTcpIpClientValueDataTypeDefaul);

        Intent intent = getIntent();
        if(intent != null) {
            m_lControlTypeParameter = intent.getIntExtra(CONTROL_TYPE, -1);
            m_lRoomIDParameter = intent.getLongExtra(ROOM_ID, -1);
            m_lControlIDParameter = intent.getIntExtra(CONTROL_ID, -1);
            m_cdParameter = intent.getParcelableExtra(CONTROL_DATA);
        }

        m_id_spn_transp_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TranspProtocolData.CommProtocolType.values()));

        m_SCAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_room.setAdapter(m_SCAdapter);

        m_TranspProtocolAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TranspProtocolEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_transp_protocol.setAdapter(m_TranspProtocolAdapter);

        m_id_spn_protocol_data_type.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, NumericDataType.DataType.values()));

        setActionBar();
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            long lControlTypeParameter;
            if(m_cdParameter != null){
                lControlTypeParameter = m_cdParameter.getTypeID();
            } else {
                lControlTypeParameter = m_lControlTypeParameter;
            }

            if(lControlTypeParameter == ControlData.ControlType.SWITCH.getID()) {
                actionBar.setTitle(getString(R.string.settings_title_section_edit_switch));
            }
            if(lControlTypeParameter == ControlData.ControlType.VALUE.getID()) {
                actionBar.setTitle(getString(R.string.settings_title_section_edit_numeric_value));
            }
            if(lControlTypeParameter == ControlData.ControlType.RAW_SENSOR.getID() || lControlTypeParameter == ControlData.ControlType.CAL_SENSOR.getID()) {
                actionBar.setTitle(getString(R.string.settings_title_section_edit_sensor_value));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Primo
        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.ROOM_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.CONTROL_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.TRANSP_PROTOCOL_LOADER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_light_switch_property_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case R.id.id_item_menu_delete:
                // Delete Data
                delete(DialogOriginID.ORIGIN_MENU_BUTTON_ID);
                return true;

            case R.id.id_item_menu_save:
                // Save Data
                save(DialogOriginID.ORIGIN_MENU_BUTTON_ID);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean bAskForSave = false;
        if (m_cd != null) {
            if(!m_cd.getSaved()){
                bAskForSave = true;
            }
        } else {
            bAskForSave = true;
        }
        if(bAskForSave){
            YesNoDialogFragment.newInstance(DialogOriginID.ORIGIN_BACK_BUTTON_ID,
                    DialogActionID.SAVE_ITEM_NOT_SAVED_CONFIRM_ID,
                    getString(R.string.text_yndf_title_data_not_saved),
                    getString(R.string.text_yndf_message_data_save_confirmation),
                    getString(R.string.text_yndf_btn_yes),
                    getString(R.string.text_yndf_btn_no)
            ).show(getFragmentManager(), "");

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == Loaders.ROOM_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.RoomEntry.load();
                }
            };
        }

        if(id == Loaders.CONTROL_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    Cursor cursor = null;
                    if(m_cdParameter != null){
                        cursor = SQLContract.ControlEntry.loadFromBaseValueData(m_cdParameter);
                    } else if (m_lControlIDParameter > 0){
                        cursor = SQLContract.ControlEntry.loadByID(m_lControlIDParameter);
                    }
                    return cursor;
                }
            };
        }

        if(id == Loaders.TRANSP_PROTOCOL_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TranspProtocolEntry.load();
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // The list should now be shown.
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(cursor);

            // Secondo
            getLoaderManager().initLoader(Loaders.CONTROL_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.CONTROL_LOADER_ID) {
            ArrayList<ControlData> albve = SQLContract.ControlEntry.get(cursor);
            if(albve != null && !albve.isEmpty()){
                m_cd = albve.get(0);
            }
            if(m_id_spn_room != null) {
                long lRoomID;
                if(m_cd != null) {
                    lRoomID = m_cd.getRoomID();
                } else {
                    lRoomID = m_lRoomIDParameter;
                }
                for (int i = 0; i < m_id_spn_room.getCount(); i++) {
                    if (m_id_spn_room.getItemIdAtPosition(i) == lRoomID) {
                        m_id_spn_room.setSelection(i);
                    }
                }
            }

            // Terzo
            getLoaderManager().initLoader(Loaders.TRANSP_PROTOCOL_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.TRANSP_PROTOCOL_LOADER_ID) {
            m_TranspProtocolAdapter.swapCursor(cursor);
            getBaseValue();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(null);
        }
        if(loader.getId() == Loaders.TRANSP_PROTOCOL_LOADER_ID) {
            m_TranspProtocolAdapter.swapCursor(null);
        }
    }

    @Override
    public void onYesNoDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID, boolean bYes, boolean bNo) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.ControlEntry.save(m_cd)){
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        } else {
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        }
                    }
                }
                if(bNo) {
                    // no action
                }
            }

            if(iDialogActionID == DialogActionID.DELETE_CONFIRM_ID) {
                if(bYes) {
                    // Delete
                    deleteBaseValueData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.ControlEntry.save(m_cd)){
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        } else {
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        }
                    }
                }
                if(bNo) {
                    super.onBackPressed();
                }
            }
            if(iDialogActionID == DialogActionID.SAVE_ITEM_NOT_SAVED_CONFIRM_ID) {
                if(bYes) {
                    // Save
                    save(iDialogOriginID);
                }
                if(bNo) {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onOkDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID){
            if(iDialogActionID == DialogActionID.SAVING_OK_ID) {
                // Save ok, exit
                finish();
            }
            if(iDialogActionID == DialogActionID.DELETING_OK_ID){
                // Save ok, exit
                finish();
            }
        }
        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID){
            if(iDialogActionID == DialogActionID.SAVING_OK_ID) {
                // Save ok, exit
                super.onBackPressed();
            }
        }
    }

    protected void getBaseValue() {
        // Stato
        if(m_id_spn_transp_protocol != null){
            if(m_id_spn_transp_protocol.getCount() > 0){
                m_id_cb_enable_transp_protocol.setEnabled(true);
            }
        }

        // Dati
        if (m_cd == null) {
            return ;
        }
        if (m_id_et_name != null) {
            m_id_et_name.setText(m_cd.getTag());
        }

        if (m_cd.getVertical()) {
            if (m_id_rb_vertical != null) {
                m_id_rb_vertical.setChecked(true);
            }
            if (m_id_rb_horizontal != null) {
                m_id_rb_horizontal.setChecked(false);
            }
        } else {
            if (m_id_rb_vertical != null) {
                m_id_rb_vertical.setChecked(false);
            }
            if (m_id_rb_horizontal != null) {
                m_id_rb_horizontal.setChecked(true);
            }
        }

        if (m_id_et_position_x != null) {
            m_id_et_position_x.setText(Float.toString(m_cd.getPosX()));
        }
        if (m_id_et_position_y != null) {
            m_id_et_position_y.setText(Float.toString(m_cd.getPosY()));
        }
        if (m_id_et_position_z != null) {
            m_id_et_position_z.setText(Float.toString(m_cd.getPosZ()));
        }

        if (m_id_spn_transp_protocol != null && m_id_cb_enable_transp_protocol != null) {
            for (int i = 0; i < m_id_spn_transp_protocol.getCount(); i++) {
                Cursor value = (Cursor) m_id_spn_transp_protocol.getItemAtPosition(i);
                if (value != null) {
                    long id = value.getLong(value.getColumnIndex("_id"));
                    if (id == m_cd.getTranspProtocolID()) {
                        m_id_cb_enable_transp_protocol.setChecked(m_cd.getTranspProtocolEnable());
                        m_id_spn_transp_protocol.setSelection(i);
                    }
                }
            }
        }

        if (m_id_et_protocol_ui != null) {
            m_id_et_protocol_ui.setText(Integer.toString(m_cd.getTranspProtocolUI()));
        }
        if (m_id_et_protocol_data_addr != null) {
            m_id_et_protocol_data_addr.setText(Integer.toString(m_cd.getTranspProtocolDataAddress()));
        }

        if(m_id_spn_protocol_data_type != null) {
            long lItem = -1;
            try{
                lItem = m_cd.getTranspProtocolDataType();
            } catch (Exception ignore) { }
            m_id_spn_protocol_data_type.setSelection((int) lItem);
        }
    }

    private void save(int iDialogOriginID) {
        if(!NumericEditText.validateInputData(findViewById(android.R.id.content))){ return; }
        if(!StringEditText.validateInputData(findViewById(android.R.id.content))){ return; }

        if(m_id_et_name != null){

            // Se non e' null, ed ha l'ID impostato,
            // oppure
            // Se l'ID e' maggiore di 0
            // il Record esiste Gia'
            if(!((m_cd != null && (m_cd.getID() > 0)) || (m_lControlIDParameter > 0))){
                if(setBaseData(iDialogOriginID)){
                    if(SQLContract.ControlEntry.save(m_cd)){
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    } else {
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    }
                }
            } else {
                YesNoDialogFragment.newInstance(iDialogOriginID,
                        DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID,
                        getString(R.string.text_yndf_title_base_value_name_already_exist),
                        getString(R.string.text_yndf_message_base_value_name_already_exist_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                ).show(getFragmentManager(), "");
            }
        }
    }

    protected boolean setBaseData(int iDialogOriginID){
        if (m_cd == null) {
            m_cd = new ControlData(m_lControlTypeParameter);
        }

        if((m_id_spn_room == null) || (m_id_spn_room.getSelectedItemId() == AdapterView.INVALID_ROW_ID)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }
        if (m_id_et_name == null || m_id_et_name.getText().toString().equals("")) {
            OkDialogFragment odf = OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_name_error), getString(R.string.text_odf_message_name_not_valid), getString(R.string.text_odf_message_ok_button));
            odf.show(getFragmentManager(), "");
            return false;
        }

        if(getOrientation() == Orientation.UNDEFINED ) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ORIENTATION_ERROR_ID, getString(R.string.text_odf_title_orientation_not_set), getString(R.string.text_odf_message_orientation_not_set), getString(R.string.text_odf_message_ok_button))
            .show(getFragmentManager(), "");
            return false;
        }

        // Set the selected Room
        m_cd.setRoomID(m_id_spn_room.getSelectedItemId());

        if (m_id_et_name != null) {
            m_cd.setTAG(m_id_et_name.getText().toString());
        }

        if(getOrientation() == Orientation.PORTRAIT ){
            m_cd.setVertical(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_cd.setVertical(true);
        }


        try {
            if (m_id_et_position_x != null) {
                m_cd.setPosX(Float.parseFloat(m_id_et_position_x.getText().toString()));
            }
            if (m_id_et_position_y != null) {
                m_cd.setPosY(Float.parseFloat(m_id_et_position_y.getText().toString()));
            }
            if (m_id_et_position_z != null) {
                m_cd.setPosZ(Float.parseFloat(m_id_et_position_z.getText().toString()));
            }
        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }
        if(m_id_cb_enable_transp_protocol != null) {
            m_cd.setTranspProtocolEnable(m_id_cb_enable_transp_protocol.isChecked());
        }

        if(m_id_spn_transp_protocol != null) {
            m_cd.setTranspProtocolID(m_id_spn_transp_protocol.getSelectedItemId());
        }

        try {

            if (m_id_et_protocol_ui != null) {
                m_cd.setTranspProtocolUI(Integer.parseInt(m_id_et_protocol_ui.getText().toString()));
            }
            if (m_id_et_protocol_data_addr != null) {
                m_cd.setTranspProtocolDataAddress(Integer.parseInt(m_id_et_protocol_data_addr.getText().toString()));
            }

        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        if(m_id_spn_protocol_data_type != null) {
            m_cd.setTranspProtocolDataType((int) m_id_spn_protocol_data_type.getSelectedItemId());
        }

        return true;
    }

    protected void setDataType(NumericDataType.DataType dtDataType, boolean bCanByModified){
        if(m_id_spn_protocol_data_type != null) {
            m_id_spn_protocol_data_type.setEnabled(bCanByModified);
            if(dtDataType != null){
                m_id_spn_protocol_data_type.setSelection(dtDataType.getTypeID());
            }
        }
    }

    private void delete(int iDialogOriginID){
        YesNoDialogFragment.newInstance(
                iDialogOriginID,
                DialogActionID.DELETE_CONFIRM_ID,
                getString(R.string.text_yndf_title_data_delete),
                getString(R.string.text_yndf_message_data_delete_confirmation),
                getString(R.string.text_yndf_btn_yes),
                getString(R.string.text_yndf_btn_no)
        ).show(getFragmentManager(), "");
    }
    private void deleteBaseValueData(int iDialogOriginID) {
        long lID = -1;
        if(m_cd != null && m_cd.getID() > 0) {
            lID = m_cd.getID();
        } else {
            lID = m_lControlIDParameter;
        }
        if(lID > 0){
            if(SQLContract.ControlEntry.deleteByID(lID)){
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return;
            }
        }

        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_ERROR_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_error), getString(R.string.text_odf_message_ok_button))
                .show(getFragmentManager(), "");

    }
    private Orientation getOrientation() {
        if (m_id_rb_vertical != null && m_id_rb_horizontal != null) {
            if(m_id_rb_vertical.isChecked() && !m_id_rb_horizontal.isChecked()) {
                return Orientation.LANDSCAPE;
            }
            if(!m_id_rb_vertical.isChecked() && m_id_rb_horizontal.isChecked()) {
                return Orientation.PORTRAIT;
            }
        }

        return Orientation.UNDEFINED;
    }

    public static Intent makeBaseValuePropActivity(Context context, Class cls, int iType) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(ControlDataPropActivity.CONTROL_TYPE, iType);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByRoomID(Context context, Class cls, int iType, long lRoomID) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(ControlDataPropActivity.CONTROL_TYPE, iType);
        intent.putExtra(ControlDataPropActivity.ROOM_ID, lRoomID);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByValueID(Context context, Class cls, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(ControlDataPropActivity.CONTROL_ID, lID);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByValueData(Context context, Class cls, ControlData bvd) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(ControlDataPropActivity.CONTROL_DATA, bvd);
        return intent;
    }
}
