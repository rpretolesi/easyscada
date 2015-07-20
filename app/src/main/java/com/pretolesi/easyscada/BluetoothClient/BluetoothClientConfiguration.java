package com.pretolesi.easyscada.BluetoothClient;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.pretolesi.easyscada.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BluetoothClientConfiguration extends ListActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    //private android.widget.SimpleCursorAdapter m_Adapter;
    private BluetoothListAdapter m_blAdapter;
    private BluetoothAdapter m_BluetoothAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver m_Receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(context != null && intent != null) {
                String action = intent.getAction();
                if(action != null && m_blAdapter != null) {
                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device != null) {
                            m_blAdapter.add(device);
/*
                            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                // Add the name and address to an array adapter to show in a ListView
                                m_blAdapter.add(new BluetoothClientData(device.getName(),device.getAddress(), true));
                            } else {
                                m_blAdapter.add(new BluetoothClientData(device.getName(),device.getAddress(), false));
                            }
*/
                        }
                    }

                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        setProgressBarIndeterminateVisibility(true);
                        setTitle(R.string.text_bt_devices_discovering);
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        setProgressBarIndeterminateVisibility(false);
                        setTitle(R.string.text_bt_devices_discovering_completed);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.bluetooth_client_configuration_list_activity);

        // Get the Bluetooth reference
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this,"Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }

        // Register the BroadcastReceiver
        if(m_Receiver != null) {
            IntentFilter filter;
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(m_Receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            registerReceiver(m_Receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(m_Receiver, filter);
        }
        // Initialize the adapter
        List<BluetoothDevice> albtd = new ArrayList<>();
        m_blAdapter = new BluetoothListAdapter(this, albtd);
        setListAdapter(m_blAdapter);

/*
//        setContentView(R.layout.bluetooth_client_configuration_list_activity_);
        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        mCursor = this.getContentResolver().query(People.CONTENT_URI, null, null, null, null);
        startManagingCursor(mCursor);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        m_Adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.two_line_list_item,  // Specify the row template to use (here, two columns bound to the two retrieved cursor
                rows).
                mCursor,                                              // Pass in the cursor to bind to.
        new String[] {People.NAME, People.COMPANY},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_BluetoothAdapter != null) {
            if (!m_BluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else{
                getBluetoothDevice();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK){
                // Ok
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this,"Device must turn on Bluetooth", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(m_blAdapter != null) {
            m_blAdapter.clear();
        }
        if (m_BluetoothAdapter.isDiscovering()) {
            m_BluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(m_Receiver != null) {
            unregisterReceiver(m_Receiver);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(m_blAdapter.getItem(position).getBondState() == BluetoothDevice.BOND_BONDED){
            Intent intent = getIntent();
            if(intent != null) {
                if(m_blAdapter != null) {
                    intent.putExtra(BluetoothClientProtocolPropActivity.BT_NAME, m_blAdapter.getItem(position).getName());
                    intent.putExtra(BluetoothClientProtocolPropActivity.BT_ADDRESS, m_blAdapter.getItem(position).getAddress());
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            }
        } else {
            // If not paired, i try to pair
            PairingThread pt = new PairingThread(m_blAdapter.getItem(position));
            pt.run();
        }
    }

    private void getBluetoothDevice(){
        if(m_BluetoothAdapter != null && m_blAdapter != null){
/*
            Set<BluetoothDevice> pairedDevices = m_BluetoothAdapter.getBondedDevices();
            if(pairedDevices != null){
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                            if(device != null){
                            // Add the name and address to an array adapter to show in a ListView
                                m_blAdapter.add(new BluetoothClientData(device.getName(),device.getAddress(), true));
                        }
                    }
                }
            }
*/
            // Enable Discovering
            if (m_BluetoothAdapter.isDiscovering()) {
                m_BluetoothAdapter.cancelDiscovery();
            }
            // Request discover from BluetoothAdapter
            m_BluetoothAdapter.startDiscovery();

        }
    }

    private class PairingThread extends Thread {
        private final BluetoothSocket m_btSocket;
        private final BluetoothDevice m_btDevice;

        public PairingThread(BluetoothDevice btd) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket bts = null;
            m_btDevice = btd;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                bts = m_btDevice.createRfcommSocketToServiceRecord(BluetoothClient.SSP_UUID);
            } catch (IOException e) { }
            m_btSocket = bts;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            if (m_BluetoothAdapter.isDiscovering()) {
                m_BluetoothAdapter.cancelDiscovery();
            }

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                m_btSocket.connect();
                try {
                    m_btSocket.close();
                } catch (IOException closeException) { }

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    m_btSocket.close();
                } catch (IOException closeException) { }
            }
        }
    }
}
