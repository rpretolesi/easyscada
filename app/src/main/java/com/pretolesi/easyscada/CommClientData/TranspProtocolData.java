package com.pretolesi.easyscada.CommClientData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TranspProtocolData implements Parcelable {

    public static short NameMinChar = 1;
    public static short NameMaxChar = 128;
    public static String NameDefaultValue = "My Name";

    public static short AddressMinChar = 1;
    public static short AddressMaxChar = 50;
    public static String AddressDefaultValue = "";

    public static int PortMinValue = 1;
    public static int PortMaxValue = 65535;
    public static String PortDefaultValue = "502";

    public static int TimeoutMinValue = 1;
    public static int TimeoutMaxValue = 60000;
    public static String TimeoutDefaultValue = "3000";

    public static int CommSendDelayDataMinValue = 1;
    public static int CommSendDelayDataMaxValue = 60000;
    public static String CommSendDelayDataDefaultValue = "3";

    public static int CommReceiveWaitDataMinValue = 1;
    public static int CommReceiveWaitDataMaxValue = 60000;
    public static String CommReceiveWaitDataDefaultValue = "10";

    public static int CommNrMaxOfErrMinValue = 0;
    public static int CommNrMaxOfErrMaxValue = 9;
    public static String CommNrMaxOfErrDefaultValue = "3";

    public static int ProtocolMinValue = 0;
    public static int ProtocolMaxValue = 3;
    public static int ProtocolDefaulValue = -1;

    public static int HeadMinValue = 0;
    public static int HeadMaxValue = 32768;
    public static String HeadDefaulValue = "0";

    public static int TailMinValue = 0;
    public static int TailMaxValue = 32768;
    public static String TailDefaulValue = "0";

    private long m_ID;
    private long m_lTypeID;
    private boolean m_bSaved;
    private String m_strName;
    private String m_strAddress;
    private int m_iPort;
    private int m_iTimeout;
    private int m_iSendDelayData;
    private int m_iReceiveWaitData;
    private int m_iNrMaxOfErr;
    private long m_lCommProtocolTypeID;
    private int m_iHead;
    private int m_iTail;

    public TranspProtocolData(int iType) {
        this.m_lTypeID = iType;
        this.m_ID = -1;
        this.m_bSaved = false;
        this.m_strName = null;
        this.m_strAddress = null;
        this.m_iPort = 0;
        this.m_iTimeout = 0;
        this.m_iSendDelayData = 0;
        this.m_iReceiveWaitData = 0;
        this.m_iNrMaxOfErr = 0;
        this.m_lCommProtocolTypeID = -1;
        this.m_iHead = 0;
        this.m_iTail = 0;
    }

    public TranspProtocolData(long lTypeID, long id, boolean bSaved, String strName, String strAddress, int iPort, int iTimeout, int iCommSendDelayData, int iCommReceiveWaitData, int iCommNrMaxOfErr, long lCommProtocolID, int iHead, int iTail) {
        this.m_lTypeID = lTypeID;
        this.m_ID = id;
        this.m_strName = strName;
        this.m_bSaved = bSaved;
        this.m_strAddress = strAddress;
        this.m_iPort = iPort;
        this.m_iTimeout = iTimeout;
        this.m_iSendDelayData = iCommSendDelayData;
        this.m_iReceiveWaitData = iCommReceiveWaitData;
        this.m_iNrMaxOfErr = iCommNrMaxOfErr;
        this.m_lCommProtocolTypeID = lCommProtocolID;
        this.m_iHead = iHead;
        this.m_iTail = iTail;
    }

    public void setTypeID(long lTypeID) { this.m_lTypeID = lTypeID; }

    public void setID(long id) {
        this.m_ID = id;
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setName(String strName) {
        this.m_strName = strName;
    }

    public void setAddress(String strAddress) {
        this.m_strAddress = strAddress;
    }

    public void setPort(int iPort) {
        this.m_iPort = iPort;
    }

    public void setTimeout(int iTimeout) {
        this.m_iTimeout = iTimeout;
    }

    public void setSendDelayData(int iSendDelayData) { this.m_iSendDelayData = iSendDelayData; }

    public void setReceiveWaitData(int iReceiveWaitData) { this.m_iReceiveWaitData = iReceiveWaitData; }

    public void setNrMaxOfErr(int iNrMaxOfErr) { this.m_iNrMaxOfErr = iNrMaxOfErr; }

    public void setCommProtocolType(CommProtocolType cpt) {
        if (cpt == CommProtocolType.MODBUS_ON_TCP_IP) {
            m_lCommProtocolTypeID = CommProtocolType.MODBUS_ON_TCP_IP.getID();
        }
        if (cpt == CommProtocolType.MODBUS_ON_SERIAL) {
            m_lCommProtocolTypeID = CommProtocolType.MODBUS_ON_SERIAL.getID();
        }
    }

    public void setCommProtocolTypeID(long lCommProtocolTypeID) {
        m_lCommProtocolTypeID = lCommProtocolTypeID;
    }

    public void setHead(int iHead) {
        this.m_iHead = iHead;
    }

    public void setTail(int iTail) {
        this.m_iTail = iTail;
    }

    public long getTypeID() { return m_lTypeID; }

    public long getID() { return m_ID; }

    public boolean getSaved() { return m_bSaved; }

    public String getName() {
        return m_strName;
    }

    public String getAddress() {
        return m_strAddress;
    }

    public int getPort() {
        return m_iPort;
    }

    public int getTimeout() {
        return m_iTimeout;
    }

    public int getSendDelayData() { return m_iSendDelayData; }

    public int getReceiveWaitData() { return m_iReceiveWaitData; }

    public int getNrMaxOfErr() { return m_iNrMaxOfErr; }

    public CommProtocolType getCommProtocolType() {
        if (m_lCommProtocolTypeID == CommProtocolType.MODBUS_ON_TCP_IP.getID()) {
            return CommProtocolType.MODBUS_ON_TCP_IP;
        }
        if (m_lCommProtocolTypeID == CommProtocolType.MODBUS_ON_SERIAL.getID()) {
            return CommProtocolType.MODBUS_ON_SERIAL;
        }
        return null;
    }

    public long getCommProtocolTypeID() { return m_lCommProtocolTypeID; }

    public int getHead() { return m_iHead; }

    public int getTail() {
        return m_iTail;
    }

    protected TranspProtocolData(Parcel in) {
        m_lTypeID = in.readLong();
        m_ID = in.readLong();
        m_bSaved = in.readByte() != 0;
        m_strName = in.readString();
        m_strAddress = in.readString();
        m_iPort = in.readInt();
        m_iTimeout = in.readInt();
        m_iSendDelayData = in.readInt();
        m_iReceiveWaitData = in.readInt();
        m_iNrMaxOfErr = in.readInt();
        m_lCommProtocolTypeID = in.readLong();
        m_iHead = in.readInt();
        m_iTail = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_lTypeID);
        dest.writeLong(m_ID);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeString(m_strName);
        dest.writeString(m_strAddress);
        dest.writeInt(m_iPort);
        dest.writeInt(m_iTimeout);
        dest.writeInt(m_iSendDelayData);
        dest.writeInt(m_iReceiveWaitData);
        dest.writeInt(m_iNrMaxOfErr);
        dest.writeLong(m_lCommProtocolTypeID);
        dest.writeInt(m_iHead);
        dest.writeInt(m_iTail);
    }

    @SuppressWarnings("unused")
    public static final Creator<TranspProtocolData> CREATOR = new Creator<TranspProtocolData>() {
        @Override
        public TranspProtocolData createFromParcel(Parcel in) {
            return new TranspProtocolData(in);
        }

        @Override
        public TranspProtocolData[] newArray(int size) {
            return new TranspProtocolData[size];
        }
    };

    public enum TranspProtocolType {
        TCP_IP(0, "TCP/IP"),
        BLUETOOTH(1, "Bluetooth");

        private int m_iProtocolID;
        private String m_strProtocolName;

        TranspProtocolType(int IProtocolID, String strProtocolName) {

            m_iProtocolID = IProtocolID;
            m_strProtocolName = strProtocolName;
        }

        public int getID() {
            return m_iProtocolID;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iProtocolID) + "-" + m_strProtocolName;
        }
    }

    public enum CommProtocolType {
        MODBUS_ON_TCP_IP(0, "Modbus RTU ON TCP/IP", TranspProtocolType.TCP_IP),
        MODBUS_ON_SERIAL(1, "Modbus RTU ON Serial or Bluetooth", TranspProtocolType.BLUETOOTH);

        private int m_iProtocolID;
        private String m_strProtocolName;
        private TranspProtocolType m_tp;

        CommProtocolType(int IProtocolID, String strProtocolName, TranspProtocolType tp) {

            m_iProtocolID = IProtocolID;
            m_strProtocolName = strProtocolName;
            m_tp = tp;
        }

        public static List<CommProtocolType> getValues(TranspProtocolType tp) {
            List<CommProtocolType> al = new ArrayList<>(2);
            for (CommProtocolType p : CommProtocolType.values()){
                if (p != null && tp != null && p.m_tp == tp){
                    al.add(p);
                }
            }
            return al;
        }

        public int getID() {
            return m_iProtocolID;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iProtocolID) + "-" + m_strProtocolName;
        }
    }

}
