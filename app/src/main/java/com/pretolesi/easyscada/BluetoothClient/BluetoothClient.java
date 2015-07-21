package com.pretolesi.easyscada.BluetoothClient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.pretolesi.easyscada.CommClientData.BaseCommClient;
import com.pretolesi.easyscada.CommClientData.TranspProtocolData;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.CustomDataStream.ReadDataInputStream;
import com.pretolesi.easyscada.Modbus.Modbus;
import com.pretolesi.easyscada.Modbus.ModbusByteCountOutOfRangeException;
import com.pretolesi.easyscada.Modbus.ModbusCRCException;
import com.pretolesi.easyscada.Modbus.ModbusPDULengthOutOfRangeException;
import com.pretolesi.easyscada.Modbus.ModbusPDU;
import com.pretolesi.easyscada.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.IO.ClientReadStatus;
import com.pretolesi.easyscada.IO.ClientStatus;
import com.pretolesi.easyscada.IO.ClientWriteStatus;
import com.pretolesi.easyscada.IO.ClientMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 *
 */

public class BluetoothClient extends BaseCommClient implements ReadDataInputStream.ReadDataInputStreamListener {
    private static final String TAG = "BluetoothClient";

    public static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ReadDataInputStream m_rdis;
    protected boolean m_bDataInputStreamReady;
    protected long m_lDataInputStreamReadyTime;

    private BluetoothAdapter m_BluetoothAdapter;
    private BluetoothDevice m_btDevice;
    private BluetoothSocket m_btSocket;

    public BluetoothClient(Context context){
        super(context);
    }

    // Cancellare
    private ClientMsg timRemoved_temp;// Debug
    private byte[] bytePDU_temp;// Debug

    @Override
    protected boolean startConnection() {
        super.startConnection();
        m_BluetoothAdapter = null;
        m_btDevice = null;
        m_btSocket = null;

        // Quering for paired device
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter == null) {
            // Device does not support Bluetooth
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, "Device does not support Bluetooth"));
            return false;
        }
        if (!m_BluetoothAdapter.isEnabled()) {
            // Bluetooth is Disable
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, "Device must turn on Bluetooth"));
            return false;
        }

        // Stop discovering
        if (m_BluetoothAdapter.isDiscovering()) {
            m_BluetoothAdapter.cancelDiscovery();
        }

        Set<BluetoothDevice> pairedDevices = m_BluetoothAdapter.getBondedDevices();
        if(pairedDevices != null){
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if(device != null){
                        if(device.getAddress().equals(getAddress())){
                            m_btDevice = device;
                        }
                    }
                }
            }
        }

        if (m_btDevice == null) {
            // Device does not support Bluetooth
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, "Device selected is not paired. Please try to pair before use it."));
            return false;
        }

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            m_btSocket = m_btDevice.createRfcommSocketToServiceRecord(BluetoothClient.SSP_UUID);
        } catch (IOException ex) {
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, "Cannot create Bluetooth Stream." + " " + ex.getMessage()));
            return false;
        }

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            m_btSocket.connect();

            m_dataOutputStream = new DataOutputStream(m_btSocket.getOutputStream());
            m_dataInputStream = new DataInputStream(m_btSocket.getInputStream());
            m_bDataInputStreamReady = false;
            resetErrorCount();

            // Start Read
            m_rdis = new ReadDataInputStream(m_dataInputStream, (short)256);
            m_rdis.registerReadDataInputStream(this);
            m_rdis.start();

        } catch (IOException ex_1) {
            // Unable to connect; close the socket and get out
            try {
                m_btSocket.close();
            } catch (IOException ex_2) {
            }
            m_btSocket = null;
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.OFFLINE, "Cannot establish a connection with:" + getName() + ", " + getAddress() + ", " + ex_1.getMessage() + "."));
            return false;
        }

        // Restore the operations not completed
        setAllMsgAsUnsent();

        // Callbacks on UI
        publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ONLINE, "" ));

        return true;
    }

    @Override
    protected boolean isConnected() {

        checkTimeoutAndSetAllMsgAsUnsent();

        if(m_btSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_btSocket.isConnected()){
            return true;
        }

        // Callbacks on UI
        publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.OFFLINE, "" ));
        return false;
    }

    @Override
    public void onReadDataInputStreamCallback() {
        m_bDataInputStreamReady = true;
        m_lDataInputStreamReadyTime = System.currentTimeMillis();
    }

    @Override
    public void onCloseReadDataInputStreamCallback() {

        m_bRestartConnection = true;
    }

    @Override
    protected boolean send() {
        super.send();
        ClientMsg tim = getMsgToSend();

        // if there is message to sent, i will empty the receive buffer
        if(tim != null){
            if(m_rdis != null){
                // Svuoto il buffer di ricezione
                resetErrorCount();
                m_rdis.getData(true);
            }
        }

        return sendMsg(tim);
    }

    @Override
    protected boolean receive() {
        super.receive();

        if (m_dataInputStream == null){
            return false;
        }
        if (m_ticd == null){
            return false;
        }
        if(m_rdis == null ) {
            return false;
        }

        // Check for sent message
        ClientMsg tim = getMsgSent();

        // No message sent, nothing to receive
        if(tim == null) {
            return true;
        }

        // No Data to read
        // Ora attendo un certo tempo dalla ricezione dei primi dati per assicurarmi che tutti il messaggio sia completo
        // Imposto un tempo di 4 ms
        if(!m_bDataInputStreamReady || (m_lDataInputStreamReadyTime + m_ticd.getReceiveWaitData() >= System.currentTimeMillis())) {
            return true;
        }

        // Modbus
        if((m_ticd.getCommProtocolType() == TranspProtocolData.CommProtocolType.MODBUS_ON_SERIAL)) {
            try {
                byte[] bytePDU = m_rdis.getData(false);
                bytePDU_temp = bytePDU;// Debug
                m_bDataInputStreamReady = false;

                if(bytePDU == null){
                    return true;
                }

                ModbusPDU mpdu = Modbus.getPDU(m_context, bytePDU, (short)bytePDU.length, true);
                if (mpdu == null) {
                    return true;
                }

                // Controllo l'indirizzo
                if(tim.getUID() != mpdu.getUID()){
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, m_context.getString(R.string.ModbusUnitIdNotMatchingException)));
                    return false;
                }

                // Svuoto il buffer di ricezione
                setOnLineProgressStatusBar();
                resetErrorCount();
                m_rdis.getData(true);

                // Tutto Ok, rimuovo l'elemento
                ClientMsg timRemoved = removeMsg((short)tim.getTID(), mpdu.getUID());
                timRemoved_temp = timRemoved;
                DataType dtDataType = null;
                if(timRemoved != null){
                    dtDataType = timRemoved.getDataType();
                }

                if (mpdu.getFEC() == 0x10 || mpdu.getFEC() == 0x90) {
                    // Check Return code
                    if (mpdu.getExCID() == 0) {
                        publishProgress(new ClientWriteStatus(getID(), (short)tim.getTID(), mpdu.getUID(), ClientWriteStatus.Status.OK, 0, ""));
                    } else {
                        publishProgress(new ClientWriteStatus(getID(), (short)tim.getTID(), mpdu.getUID(), ClientWriteStatus.Status.ERROR, mpdu.getExCID(), mpdu.getExCDescr()));
                    }
                }

                if (mpdu.getFEC() == 0x03 || mpdu.getFEC() == 0x83) {
                    // Check Return code
                    if (mpdu.getExCID() == 0 && dtDataType != null && mpdu.getPDUValue() != null) {
                        publishProgress(new ClientReadStatus(getID(), (short)tim.getTID(), mpdu.getUID(), ClientReadStatus.Status.OK, 0, "", getValue(dtDataType, mpdu.getPDUValue())));
                    } else {
                        publishProgress(new ClientReadStatus(getID(), (short)tim.getTID(), mpdu.getUID(), ClientReadStatus.Status.ERROR, mpdu.getExCID(), mpdu.getExCDescr(), null));
                    }
                }

                return true;

            } catch (ModbusPDULengthOutOfRangeException ex) {
                // Callbacks on UI
                if(canErrorFireAndIncCount()) {
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                }
            } catch (ModbusCRCException ex) {
                // Callbacks on UI
                 if(canErrorFireAndIncCount()) {
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                }
            } catch (ModbusUnitIdOutOfRangeException ex) {
                // Callbacks on UI
                if(canErrorFireAndIncCount()) {
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                }
            } catch (ModbusByteCountOutOfRangeException ex) {
                // Callbacks on UI
                if(canErrorFireAndIncCount()) {
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                }
            } catch (Exception ex) {
                // Callbacks on UI
                if(canErrorFireAndIncCount()) {
                    publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                }
            }
        }

        // Se arrivo qua' c'e' stato un errore. Ritento per xx volte...
        if(!canErrorFire()){
            return true;
        }

        // Svuoto il buffer di ricezione
        resetErrorCount();
        m_rdis.getData(true);
        return false;
    }

    @Override
    protected void stopConnection() {
        super.stopConnection();

        // Chiudo il socket
        if (m_btSocket != null) {
            try {
                m_btSocket.close();
            } catch (IOException ioex_1) {
            }
            m_btSocket = null;
        }

        // Attendo che il Thread di lettura si arresti
        if (m_rdis != null) {
            m_rdis.interrupt();

            try {
                m_rdis.join();
            } catch (InterruptedException ex) {
            }
            m_rdis = null;
        }

        // Callbacks on UI
        publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.OFFLINE, "" ));
    }

    @Override
    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        return super.writeValue(context, iTID, iUID, iAddress, objValue);
    }

    @Override
    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType, String strValue){
        return super.writeValue(context, iTID, iUID, iAddress, dtDataType, strValue);
    }

}