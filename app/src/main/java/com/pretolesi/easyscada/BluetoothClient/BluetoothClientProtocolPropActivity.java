package com.pretolesi.easyscada.BluetoothClient;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.pretolesi.easyscada.CommClientData.TranspProtocolData;
import com.pretolesi.easyscada.CommClientData.TranspProtocolDataPropActivity;
import com.pretolesi.easyscada.R;


/**
 * Created by ricca_000 on 03/06/2015.
 */
public class BluetoothClientProtocolPropActivity extends TranspProtocolDataPropActivity {

    private static final int ACTIVITY_RESULT_BT_ADDRESS = 1;

    public static final String BT_NAME = "bt_name";
    public static final String BT_ADDRESS = "bt_address";

    private String m_server_name;
    private String m_server_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_id_tv_server_name.setText(R.string.text_tv_server_bt_name);
        m_id_tv_server_address.setText(R.string.text_tv_server_bt_address);
        m_id_et_server_address.setText(R.string.text_et_server_bt_address);
        m_id_et_server_address.setHint(R.string.hint_et_server_bt_address);
        m_id_stica_tv_server_port.setVisibility(View.GONE);
        m_id_stica_et_server_port.setVisibility(View.GONE);
        m_id_stica_et_timeout.setText(Short.toString((short) 3000));
        m_id_stica_tv_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_2.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_2.setVisibility(View.GONE);
        // Set Listener
        m_id_et_server_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    Intent intent = new Intent();
                    intent.setClass(getBaseContext(), BluetoothClientConfiguration.class);
                    startActivityForResult(intent, ACTIVITY_RESULT_BT_ADDRESS);
                }

            }
        });

        m_id_stica_spn_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TranspProtocolData.CommProtocolType.getValues(TranspProtocolData.TranspProtocolType.BLUETOOTH)));

        setActionBarTitle(getString(R.string.settings_title_section_edit_bluetooth_client));

        m_server_name = "";
        m_server_address = "";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_BT_ADDRESS) {
            if(resultCode == RESULT_OK){
                // Ok
                m_server_name = data.getStringExtra(BT_NAME);
                m_server_address = data.getStringExtra(BT_ADDRESS);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);
        if(!m_server_name.equals("") && !m_server_address.equals("") ){
            m_id_et_server_name.setText(m_server_name);
            m_id_et_server_address.setText(m_server_address);
        }
    }

    @Override
    protected boolean setBaseData(int iDialogOriginID){

        if (m_ticd == null) {
            m_ticd = new TranspProtocolData(TranspProtocolData.TranspProtocolType.BLUETOOTH.getID());
        }

        return super.setBaseData(iDialogOriginID);
    }
}
