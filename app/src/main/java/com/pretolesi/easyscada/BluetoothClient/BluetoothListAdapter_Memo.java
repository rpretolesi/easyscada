package com.pretolesi.easyscada.BluetoothClient;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pretolesi.easyscada.CustomControls.ViewHolder;
import com.pretolesi.easyscada.R;

import java.util.List;

/**
 * Created by RPRETOLESI on 09/06/2015.
 */
public class BluetoothListAdapter_Memo extends BaseAdapter {
    private Context m_Context;
    private List<BluetoothClientData> m_bcd;
    private LayoutInflater m_LayoutInflater;

    public BluetoothListAdapter_Memo(Context context, List<BluetoothClientData> bcd) {
        m_Context = context;
        m_bcd = bcd;
        if(m_Context != null) {
            m_LayoutInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public int getCount() {
        if(m_bcd != null){
            return m_bcd.size();
        }
        return 0;
    }

    @Override
    public BluetoothClientData getItem(int position) {
        if(m_bcd != null){
            return m_bcd.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(m_bcd != null){
            return position;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null && m_LayoutInflater != null) {
            convertView = m_LayoutInflater.inflate(R.layout.bluetooth_client_configuration_list_activity_items, parent, false);
        }

        TextView id_tv_name = ViewHolder.get(convertView, R.id.id_tv_name);
        TextView id_tv_address = ViewHolder.get(convertView, R.id.id_tv_address);
        TextView id_tv_paired = ViewHolder.get(convertView, R.id.id_tv_paired);

        if (m_bcd != null)
        {
            id_tv_name.setText(m_bcd.get(position).getName());
            id_tv_address.setText(m_bcd.get(position).getAddress());
            if(m_bcd.get(position).getPaired()){
                id_tv_paired.setText(R.string.text_bt_device_paired);
                id_tv_paired.setTextColor(Color.GREEN);
            } else {
                id_tv_paired.setText(R.string.text_bt_device_unpaired);
                id_tv_paired.setTextColor(Color.YELLOW);
            }
        }

        return convertView;
    }

    public void add(BluetoothClientData bcd) {
        if(m_bcd != null){
            m_bcd.add(bcd);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if(m_bcd != null){
            m_bcd.clear();
            notifyDataSetChanged();
        }
    }
}
