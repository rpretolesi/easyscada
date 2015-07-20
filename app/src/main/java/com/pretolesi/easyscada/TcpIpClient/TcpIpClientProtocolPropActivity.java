package com.pretolesi.easyscada.TcpIpClient;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.pretolesi.easyscada.CommClientData.TranspProtocolData;
import com.pretolesi.easyscada.CommClientData.TranspProtocolDataPropActivity;
import com.pretolesi.easyscada.R;

/**
 *
 */
public class TcpIpClientProtocolPropActivity extends TranspProtocolDataPropActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_id_tv_server_name.setText(R.string.text_tv_server_ip_name);
        m_id_tv_server_address.setText(R.string.text_tv_server_ip_address);
        m_id_et_server_address.setText(R.string.text_et_server_ip_address);
        m_id_et_server_address.setHint(R.string.hint_et_server_ip_address);
        m_id_stica_et_timeout.setText(Short.toString((short)9000));
        m_id_stica_tv_comm_receive_wait_data.setVisibility(View.GONE);
        m_id_stica_et_comm_receive_wait_data.setVisibility(View.GONE);
        m_id_stica_tv_comm_nr_max_err.setVisibility(View.GONE);
        m_id_stica_et_comm_nr_max_err.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_2.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_2.setVisibility(View.GONE);

        m_id_stica_spn_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TranspProtocolData.CommProtocolType.getValues(TranspProtocolData.TranspProtocolType.TCP_IP)));

        setActionBarTitle(getString(R.string.settings_title_section_edit_tcp_ip_client));

    }

    @Override
    protected boolean setBaseData(int iDialogOriginID){

        if (m_ticd == null) {
            m_ticd = new TranspProtocolData(TranspProtocolData.TranspProtocolType.TCP_IP.getID());
        }

        return super.setBaseData(iDialogOriginID);
    }
}
