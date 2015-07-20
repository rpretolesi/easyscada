package com.pretolesi.easyscada.CommClientData;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easyscada.CustomControls.NumericDataType;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.CommClientData.TranspProtocolData.CommProtocolType;
import com.pretolesi.easyscada.Modbus.Modbus;
import com.pretolesi.easyscada.Modbus.ModbusAddressOutOfRangeException;
import com.pretolesi.easyscada.Modbus.ModbusQuantityOfRegistersOutOfRange;
import com.pretolesi.easyscada.Modbus.ModbusTransIdOutOfRangeException;
import com.pretolesi.easyscada.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easyscada.Modbus.ModbusValueOutOfRangeException;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.IO.ClientReadStatus;
import com.pretolesi.easyscada.IO.ClientStatus;
import com.pretolesi.easyscada.IO.ClientWriteStatus;
import com.pretolesi.easyscada.IO.ClientMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by RPRETOLESI on 16/06/2015.
 */
public class BaseCommClient extends AsyncTask<Object, Object, Void> {

    // Listener e Callback
    // Client
    protected List<TcpIpClientStatusListener> m_vTcpIpClientStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientStatus(TcpIpClientStatusListener listener) {
        if(m_vTcpIpClientStatusListener != null && !m_vTcpIpClientStatusListener.contains(listener)){
            m_vTcpIpClientStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientStatus(TcpIpClientStatusListener listener) {
        if(m_vTcpIpClientStatusListener != null && m_vTcpIpClientStatusListener.contains(listener)){
            m_vTcpIpClientStatusListener.remove(listener);
        }
    }

    // Write
    protected List<TcpIpClientWriteStatusListener> m_vTcpIpClientWriteStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientWriteSwitchStatus(TcpIpClientWriteStatusListener listener) {
        if(m_vTcpIpClientWriteStatusListener != null && !m_vTcpIpClientWriteStatusListener.contains(listener)){
            m_vTcpIpClientWriteStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientWriteSwitchStatus(TcpIpClientWriteStatusListener listener) {
        if(m_vTcpIpClientWriteStatusListener != null && m_vTcpIpClientWriteStatusListener.contains(listener)){
            m_vTcpIpClientWriteStatusListener.remove(listener);
        }
    }

    // Read
    protected List<TcpIpClientReadValueStatusListener> m_vTcpIpClientReadValueStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientReadValueStatus(TcpIpClientReadValueStatusListener listener) {
        if(m_vTcpIpClientReadValueStatusListener != null && !m_vTcpIpClientReadValueStatusListener.contains(listener)){
            m_vTcpIpClientReadValueStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientReadValueStatus(TcpIpClientReadValueStatusListener listener) {
        if(m_vTcpIpClientReadValueStatusListener != null && m_vTcpIpClientReadValueStatusListener.contains(listener)){
            m_vTcpIpClientReadValueStatusListener.remove(listener);
        }
    }

    protected Context m_context = null;
    protected TranspProtocolData m_ticd = null;
    private ConcurrentLinkedQueue<ClientMsg> m_clqim = null;

    // MBAP
    protected byte[] m_byteMBAP = null;
    // PDU
    protected byte[] m_bytePDU = null;

    protected DataOutputStream m_dataOutputStream = null;
    protected DataInputStream m_dataInputStream = null;

    private int m_iProgressCounter = 0;

    protected boolean m_bRestartConnection = false;
    private int m_iCommNrOfError;

    public BaseCommClient (Context context){
        m_vTcpIpClientStatusListener = new Vector<>();
        m_vTcpIpClientWriteStatusListener = new Vector<>();
        m_vTcpIpClientReadValueStatusListener = new Vector<>();
        m_context = context;
        m_byteMBAP = new byte[6];
        m_bytePDU = new byte[256];
        m_clqim = new ConcurrentLinkedQueue<>();
    }


    public synchronized long getID() {
        if (m_ticd != null) {
            return m_ticd.getID();
        }
        return 0;
    }

    public synchronized String getName() {
        if (m_ticd != null) {
            return m_ticd.getName();
        }
        return "";
    }

    public synchronized String getAddress() {
        if (m_ticd != null) {
            return m_ticd.getAddress();
        }
        return "";
    }

    protected void setMsgToSend(ClientMsg tim) {
        if (m_clqim != null && tim != null) {
            if (!m_clqim.contains(tim)) {
                m_clqim.add(tim);
            }
        }
    }

    protected ClientMsg getMsgSent(){
        ClientMsg tim = null;
        for (Iterator<ClientMsg> iterator = m_clqim.iterator(); iterator.hasNext();) {
            ClientMsg tim_temp = iterator.next();
            if (tim_temp != null) {
                if(tim_temp.getMsgSent()) {
                    tim = tim_temp;
                }
            }
        }

        return tim;
    }

    protected ClientMsg getMsgToSend(){
        boolean bNoMsgSent = true;

        if(m_clqim == null || m_clqim.isEmpty()) {
            return null;
        }

        for (ClientMsg tim : m_clqim) {
            if (tim != null && tim.getMsgSent()) {
                bNoMsgSent = false;
            }
        }

        int iPriority = -1;
        ClientMsg tim = null;

        if(bNoMsgSent) {
            for (ClientMsg tim_temp : m_clqim) {
                if (tim_temp != null && tim_temp.getPriority() > iPriority) {
                    iPriority = tim_temp.getPriority();
                    tim = tim_temp;
                    if(iPriority == 1){
                        iPriority = 1;
                    }
                }
            }
            if(tim != null){
                tim.setMsgTimeMSNow();
                tim.setMsgAsSent(true);
            }
        }

        return tim;
    }

    protected ClientMsg removeMsg(short shTID, short shUID) {
        ClientMsg timRemoved = null;
        for (Iterator<ClientMsg> iterator = m_clqim.iterator(); iterator.hasNext(); ) {
            ClientMsg tim = iterator.next();
            if (tim != null && tim.getTID() == shTID && tim.getUID() == shUID) {
                timRemoved = new ClientMsg(tim);
                iterator.remove();
            }
        }
        return timRemoved;
    }

    protected void setAllMsgAsUnsent(){
        if(m_clqim != null) {
            for (ClientMsg tim : m_clqim) {
                if (tim != null) {
                    tim.setMsgTimeMSNow();
                    tim.setMsgAsSent(false);
                }
            }
        }
    }

    protected void checkTimeoutAndSetAllMsgAsUnsent(){
        if(m_clqim != null && m_context != null) {
            for (ClientMsg tim : m_clqim) {
                if (tim != null) {
                    if (System.currentTimeMillis() - tim.getSentTimeMS() >= m_ticd.getTimeout()) {
                        tim.setMsgTimeMSNow();
                        tim.setMsgAsSent(false);
                        publishProgress(new ClientWriteStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), ClientWriteStatus.Status.ERROR, 0, m_context.getString(R.string.TimeoutException)));
                        publishProgress(new ClientReadStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), ClientReadStatus.Status.ERROR, 0, m_context.getString(R.string.TimeoutException), null));
                    }
                }
            }
        }
    }

    // Error
    protected boolean canErrorFireAndIncCount(){
        if(m_ticd == null){
            return false;
        }
        m_iCommNrOfError = m_iCommNrOfError + 1;
        if(m_iCommNrOfError <= m_ticd.getNrMaxOfErr()){
            return false;
        }
        return true;
    }

    protected boolean canErrorFire(){
        if(m_ticd == null){
            return false;
        }
        if(m_iCommNrOfError <= m_ticd.getNrMaxOfErr()){
            return false;
        }
        return true;
    }

    protected void resetErrorCount(){
        m_iCommNrOfError = 0;
    }

    // Progress
    protected void setOnLineProgressStatusBar(){
        m_iProgressCounter = m_iProgressCounter + 1;
        if(m_iProgressCounter > 16) {
            m_iProgressCounter = 1;
        }
        String strProgress = "";
        for(int index = 0; index < m_iProgressCounter; index++){
            strProgress = strProgress + "-";
        }
        // Callbacks on UI
        publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ONLINE, strProgress ));
    }

    protected boolean startConnection() { return false; }

    protected boolean send() { return false; }

    protected boolean sendMsg(ClientMsg tim) {

        if (m_dataOutputStream != null) {
            try {
                if(tim != null){
                    m_dataOutputStream.write(tim.getMsgData(), 0, tim.getMsgData().length);
                    setOnLineProgressStatusBar();
                }
                return true;

//            } catch (EmptyStackException ex) {
//                return true;
            } catch (Exception ex) {
                // Callbacks on UI
                publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.ERROR, ex.getMessage()));
                // Close
                stopConnection();
            }
        }

        return false;
    }

    protected boolean receive() {
        return false;
    }

    protected boolean isConnected() {
        return false;
    }

    protected void stopConnection() {
        if(m_ticd != null) {
            // Callbacks on UI
            publishProgress(new ClientStatus(getID(), getName(), ClientStatus.Status.DISCONNECTING, ""));
        }

        // close Output stream
        if (m_dataOutputStream != null) {
            try {
                m_dataOutputStream.close();
            } catch (IOException ioex_2) {
            }
        }
        m_dataOutputStream = null;

        // close Input stream
        if (m_dataInputStream != null) {
            try {
                m_dataInputStream.close();
            } catch (IOException ioex_3) {
            }
        }
        m_dataInputStream = null;
    }

    /*
     * Writing/Reading Function
     */

    // Writing
    protected void writeShort(Context context, int iTID, int iUID, int iAddress, int iValue){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.writeShort(context, iTID, iUID, iAddress, iValue, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                }
            }
        }
    }

    protected void writeInteger(Context context, int iTID, int iUID, int iAddress, long lValue){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.writeInteger(context, iTID, iUID, iAddress, lValue, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                }
            }
        }
    }

    protected void writeLong(Context context, int iTID, int iUID, int iAddress, long lValue){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.writeLong(context, iTID, iUID, iAddress, lValue, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                }
            }
        }
    }

    protected void writeFloat(Context context, int iTID, int iUID, int iAddress, float fValue){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.writeFloat(context, iTID, iUID, iAddress, fValue, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                }
            }
        }
    }

    protected void writeDouble(Context context, int iTID, int iUID, int iAddress, double dblValue){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.writeDouble(context, iTID, iUID, iAddress, dblValue, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientWriteStatus(getID(), iTID, iUID, ClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                }
            }
        }
    }

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        if(objValue == null) {
            return false;
        }
        if(objValue instanceof Short){
            writeShort(context, iTID, iUID, iAddress, (Short)objValue);
            return true;
        }
        if(objValue instanceof Integer){
            writeInteger(context, iTID, iUID, iAddress, (Integer) objValue);
            return true;
        }
        if(objValue instanceof Long){
            writeLong(context, iTID, iUID, iAddress, (Long) objValue);
            return true;
        }
        if(objValue instanceof Float){
            writeFloat(context, iTID, iUID, iAddress, (Float) objValue);
            return true;
        }
        if(objValue instanceof Double){
            writeDouble(context, iTID, iUID, iAddress, (Double) objValue);
            return true;
        }
        return false;
    }

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType, String strValue){
        if(dtDataType != null){
            switch (dtDataType) {
                case SHORT:
                    short shValue;
                    try {
                        shValue = Short.parseShort(strValue);
                        writeShort(context, iTID, iUID, iAddress, shValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case INT:
                    int iValue;
                    try {
                        iValue = Integer.parseInt(strValue);
                        writeInteger(context, iTID, iUID, iAddress, iValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case LONG:
                    long lValue;
                    try {
                        lValue = Long.parseLong(strValue);
                        writeLong(context, iTID, iUID, iAddress, lValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case FLOAT:
                    float fValue;
                    try {
                        fValue = Float.parseFloat(strValue);
                        writeFloat(context, iTID, iUID, iAddress, fValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case DOUBLE:
                    double dblValue;
                    try {
                        dblValue = Double.parseDouble(strValue);
                        writeDouble(context, iTID, iUID, iAddress, dblValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

            }
        }
        return false;
    }

    // Reading
    private void readShort(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.readShort(context, iTID, iUID, iAddress, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readInt(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.readInt(context, iTID, iUID, iAddress, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readLong(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.readLong(context, iTID, iUID, iAddress, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readFloat(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.readFloat(context, iTID, iUID, iAddress, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readDouble(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if((m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_TCP_IP) || (m_ticd.getCommProtocolType() == CommProtocolType.MODBUS_ON_SERIAL)) {
                try {
                    ClientMsg tim = Modbus.readDouble(context, iTID, iUID, iAddress, m_ticd.getCommProtocolType());
                    setMsgToSend(tim);
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new ClientReadStatus(getID(), iTID, iUID, ClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    public synchronized void readValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType){
        if(dtDataType != null){
            switch (dtDataType) {
                case SHORT:
                    readShort(context, iTID, iUID, iAddress);
                    break;

                case INT:
                    readInt(context, iTID, iUID, iAddress);
                    break;

                case LONG:
                    readLong(context, iTID, iUID, iAddress);
                    break;

                case FLOAT:
                    readFloat(context, iTID, iUID, iAddress);
                    break;

                case DOUBLE:
                    readDouble(context, iTID, iUID, iAddress);
                    break;

            }
        }
    }

    public synchronized Object getValue(DataType dtDataType, byte[] aByteValue){
        Object obj = null;

        if(dtDataType != null && aByteValue != null){
            switch (dtDataType) {
                case SHORT:
                    obj = NumericDataType.getShort(aByteValue);
                    break;

                case INT:
                    obj = NumericDataType.getInt(aByteValue);
                    break;

                case LONG:
                    obj = NumericDataType.getLong(aByteValue);
                    break;

                case FLOAT:
                    obj = NumericDataType.getFloat(aByteValue);
                    break;

                case DOUBLE:
                    obj = NumericDataType.getDouble(aByteValue);
                    break;

            }
        }
        return obj;
    }

    @Override
    protected Void doInBackground(Object... obj) {

        m_ticd = (TranspProtocolData) obj[0];

        try {
            while (!isCancelled() && m_ticd != null) {
                try {
                    if (!isConnected() || m_bRestartConnection) {
                        // Reset bit
                        m_bRestartConnection = false;

                        // Stop communication with Server
                        stopConnection();
/*
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {

                        }
*/
                        // Start communication with Server
                        if(!startConnection()) {
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    } else {
                        if (!send()){
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }

                        if (!receive()) {
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        // Delay (depending of the protocol specific
                        try {
                            Thread.sleep(m_ticd.getSendDelayData(), 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
        }

        // Closing...
        stopConnection();

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... obj) {
        super.onProgressUpdate(obj);
        // Aggiorno
        if(obj != null){
            if(obj[0] instanceof ClientStatus){
                if(m_vTcpIpClientStatusListener != null) {
                    for (TcpIpClientStatusListener ticl : m_vTcpIpClientStatusListener) {
                        if(ticl != null) {
                            ticl.onTcpIpClientStatusCallback((ClientStatus) obj[0]);
                        }
                    }
                }
            }
            if(obj[0] instanceof ClientWriteStatus){
                if(m_vTcpIpClientWriteStatusListener != null) {
                    for (TcpIpClientWriteStatusListener ticwsl : m_vTcpIpClientWriteStatusListener) {
                        if(ticwsl != null) {
                            ticwsl.onWriteValueStatusCallback((ClientWriteStatus) obj[0]);
                        }
                    }
                }
            }
            if(obj[0] instanceof ClientReadStatus){
                if(m_vTcpIpClientReadValueStatusListener != null) {
                    for (TcpIpClientReadValueStatusListener ticrvsl : m_vTcpIpClientReadValueStatusListener) {
                        if(ticrvsl != null) {
                            ticrvsl.onReadValueStatusCallback((ClientReadStatus) obj[0]);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    /**
     * Callbacks interface.
     */
    public interface TcpIpClientStatusListener {
        /**
         * Callbacks
         */
        void onTcpIpClientStatusCallback(ClientStatus tics);
    }
    public interface TcpIpClientWriteStatusListener {
        /**
         * Callbacks
         */
        void onWriteValueStatusCallback(ClientWriteStatus ticws);
    }
    public interface TcpIpClientReadValueStatusListener {
        /**
         * Callbacks
         */
        void onReadValueStatusCallback(ClientReadStatus ticrs);
    }
}
