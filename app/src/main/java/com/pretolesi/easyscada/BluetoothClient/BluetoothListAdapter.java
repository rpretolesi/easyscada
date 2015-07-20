package com.pretolesi.easyscada.BluetoothClient;

import android.bluetooth.BluetoothDevice;
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
public class BluetoothListAdapter extends BaseAdapter {
    private Context m_Context;
    private List<BluetoothDevice> m_btd;
    private LayoutInflater m_LayoutInflater;

    public BluetoothListAdapter(Context context, List<BluetoothDevice> bcd) {
        m_Context = context;
        m_btd = bcd;
        if(m_Context != null) {
            m_LayoutInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public int getCount() {
        if(m_btd != null){
            return m_btd.size();
        }
        return 0;
    }

    @Override
    public BluetoothDevice getItem(int position) {
        if(m_btd != null){
            return m_btd.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(m_btd != null){
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

        if (m_btd != null)
        {
            id_tv_name.setText(m_btd.get(position).getName());
            id_tv_address.setText(m_btd.get(position).getAddress());
            if((m_btd.get(position).getBondState() == BluetoothDevice.BOND_BONDED)){
                id_tv_paired.setText(R.string.text_bt_device_paired);
                id_tv_paired.setTextColor(Color.GREEN);
            } else {
                id_tv_paired.setText(R.string.text_bt_device_unpaired);
                id_tv_paired.setTextColor(Color.YELLOW);
            }
        }

        return convertView;
    }

    public void add(BluetoothDevice btd) {
        if(m_btd != null){
            m_btd.add(btd);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if(m_btd != null){
            m_btd.clear();
            notifyDataSetChanged();
        }
    }
}
