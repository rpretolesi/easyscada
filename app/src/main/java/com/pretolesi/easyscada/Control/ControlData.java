package com.pretolesi.easyscada.Control;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *
 */
public class ControlData implements Parcelable {

    public static short TAGMinChar = 1;
    public static short TAGMaxChar = 128;
    public static String TAGDefaultValue = "My Name";

    public static String PosXDefaultValue = "50";

    public static String PosYDefaultValue = "50";

    public static String PosZDefaultValue = "0";

    public static int ProtTcpIpClientValueIDMinValue = 0;
    public static int ProtTcpIpClientValueIDMaxValue = 32768;
    public static String ProtTcpIpClientValueIDDefaulValue = "0";

    public static int ProtTcpIpClientValueAddressMin = 0;
    public static int ProtTcpIpClientValueAddressMax = 65535;
    public static String ProtTcpIpClientValueAddressDefaul = "0";

    public static int ProtTcpIpClientValueDataTypeDefaul = -1;

    public static String ValueDefaulValue = "###";

    public static int ValueMinNrCharToShowMinValue = 1;
    public static int ValueMinNrCharToShowMaxValue = 9;
    public static String ValueMinNrCharToShowDefaulValue = "9";

    public static int ValueNrOfDecimalMinValue = 0;
    public static int ValueNrOfDecimalMaxValue = 5;
    public static String ValueNrOfDecimalDefaulValue = "0";

    public static short ValueUMMinValue = 0;
    public static short ValueUMMaxValue = 9;
    public static String ValueUMDefaulValue = "";

    public static int ValueUpdateMillisMinValue = 10;
    public static int ValueUpdateMillisMaxValue = 65535;
    public static String ValueUpdateMillisDefaulValue = "1000";

    public static String WriteValueOFFDefault = "1";
    public static String WriteValueOFFONDefault = "3";
    public static String WriteValueONOFFDefault = "7";
    public static String WriteValueONDefault = "4";

    // Sensor
    public static float SensorAmplKMinValue = 0.001f;
    public static float SensorAmplKMaxValue = 1000.0f;
    public static String SensorAmplKDefaulValue = "10.0";

    public static float SensorLowPassFilterKMinValue = 0.001f;
    public static float SensorLowPassFilterKMaxValue = 0.999f;
    public static String SensorLowPassFilterKDefaulValue = "0.1";

    public static int SensorSampleTimeMinValue = 1;
    public static int SensorSampleTimeMaxValue = 60000;
    public static String SensorSampleTimeDefaulValue = "300";

    public static int SensorWriteUpdateTimeMinValue = 100;
    public static int SensorWriteUpdateTimeMaxValue = 60000;
    public static String SensorWriteUpdateTimeDefaulValue = "1000";

    // Graphic
    private long m_ID;
    private int m_iTypeID;
    private boolean m_bSaved;
    private long m_lRoomID;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;
    private boolean m_bVertical;

    // Protocol_
    private boolean m_bTranspProtocolEnable;
    private long m_lTranspProtocolID;
    private int m_iTranspProtocolUI;
    private int m_iTranspProtocolDataAddress;
    private int m_iTranspProtocolDataType;

    // Format Value
    private int m_iValueMinNrCharToShow;
    private int m_iValueNrOfDecimal;
    private String m_strValueUM;
    private int m_iValueUpdateMillis;
    private boolean m_bValueReadOnly;
    private boolean m_bValueWriteOnly;

    // Switch
    private int m_iWriteValueOFF;
    private int m_iWriteValueOFFON;
    private int m_iWriteValueONOFF;
    private int m_iWriteValueON;

    // Sensor
    private long m_lSensorTypeID;
    private long m_lSensorValueID;
    private boolean m_bSensorEnableSimulation;
    private float m_fSensorAmplK;
    private float m_fSensorLowPassFilterK;
    private int m_iSensorSampleTimeMillis;
    private int m_iSensorWriteUpdateTimeMillis;

    public ControlData(int iTypeID) {
        // Graphic
        m_iTypeID = iTypeID;
        m_ID = -1;
        m_bSaved = false;
        m_lRoomID = -1;
        m_strTAG = "";
        m_fPosX = 20;
        m_fPosY = 20;
        m_fPosZ = 0;
        m_bVertical = false;

        // Protocol_
        m_bTranspProtocolEnable = false;
        m_lTranspProtocolID = -1;
        m_iTranspProtocolUI = 0;
        m_iTranspProtocolDataAddress = 0;
        m_iTranspProtocolDataType = -1;

        // Format Value
        m_iValueMinNrCharToShow = 0;
        m_iValueNrOfDecimal = 0;
        m_strValueUM = "";
        m_iValueUpdateMillis = 1000;

        // Switch
        m_iWriteValueOFF = 1;
        m_iWriteValueOFFON = 3;
        m_iWriteValueONOFF = 7;
        m_iWriteValueON = 4;
        m_bValueReadOnly = false;
        m_bValueWriteOnly = false;

        // Sensor
        m_lSensorTypeID = -1;
        m_lSensorValueID = -1;
        m_bSensorEnableSimulation = false;
        m_fSensorAmplK = 1.0f;
        m_fSensorLowPassFilterK = 0.1f;
        m_iSensorSampleTimeMillis = 300;
    }

    public void setPositionValue(int iTypeID, long id, long lRoomID, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        // Graphic
        m_iTypeID = iTypeID;
        m_ID = id;
        m_lRoomID = lRoomID;
        m_strTAG = strTAG;
        m_fPosX = fPosX;
        m_fPosY = fPosY;
        m_fPosZ = fPosZ;
        m_bVertical = bLandscape;
    }

    public void setTranspProtocol(boolean bTranspProtocolEnable, long lTranspProtocolID, int iTranspProtocolUI, int iTranspProtocolDataAddress, int iTranspProtocolDataType) {
        this.m_bTranspProtocolEnable = bTranspProtocolEnable;
        this.m_lTranspProtocolID = lTranspProtocolID;
        this.m_iTranspProtocolUI = iTranspProtocolUI;
        this.m_iTranspProtocolDataAddress = iTranspProtocolDataAddress;
        this.m_iTranspProtocolDataType = iTranspProtocolDataType;
    }

    public void setFormatValue(int iValueMinNrCharToShow, int iValueNrOfDecimal, String strValueUM, int iValueUpdateMillis, boolean bValueReadOnly, boolean bValueWriteOnly) {
        // Format Value
        m_iValueMinNrCharToShow = iValueMinNrCharToShow;
        m_iValueNrOfDecimal = iValueNrOfDecimal;
        m_strValueUM = strValueUM;
        m_iValueUpdateMillis = iValueUpdateMillis;
        m_bValueReadOnly = bValueReadOnly;
        m_bValueWriteOnly = bValueWriteOnly;
    }

    public void setSwitchValue(int iWriteValueOFF, int iWriteValueOFFON, int iWriteValueONOFF, int iWriteValueON) {
        // Switch Value
        m_iWriteValueOFF = iWriteValueOFF;
        m_iWriteValueOFFON = iWriteValueOFFON;
        m_iWriteValueONOFF = iWriteValueONOFF;
        m_iWriteValueON = iWriteValueON;
    }

    public void setSensorType(long lSensorTypeID, long lSensorValueID, boolean bSensorEnableSimulation, float fSensorK, float fSensorLowPassFilterK, int iSensorSampleTime, int iSensorWriteUpdateTimeMillis) {
        m_lSensorTypeID = lSensorTypeID;
        m_lSensorValueID = lSensorValueID;
        m_bSensorEnableSimulation = bSensorEnableSimulation;
        m_fSensorAmplK = fSensorK;
        m_fSensorLowPassFilterK = fSensorLowPassFilterK;
        m_iSensorSampleTimeMillis = iSensorSampleTime;
        m_iSensorWriteUpdateTimeMillis = iSensorWriteUpdateTimeMillis;
    }

    // Set Method
    public void setTypeID(int iType) { this.m_iTypeID = iType; }

    public void setID(long id) { this.m_ID = id; }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setRoomID(long lRoomID) { this.m_lRoomID = lRoomID; }

    public void setTAG(String strTAG) { this.m_strTAG = strTAG; }

    public void setPosX(float fPosX) {  this.m_fPosX = fPosX; }

    public void setPosY(float fPosY) { this.m_fPosY = fPosY; }

    public void setPosZ(float fPosZ) { this.m_fPosZ = fPosZ; }

    public void setVertical(boolean bVertical) { this.m_bVertical = bVertical; }

    // Protocol_
    public void setTranspProtocolEnable(boolean bTranspProtocolEnable) { this.m_bTranspProtocolEnable = bTranspProtocolEnable; }

    public void setTranspProtocolID(long lProtTcpIpClientID) { this.m_lTranspProtocolID = lProtTcpIpClientID; }

    public void setTranspProtocolUI(int iTranspProtocolUI) { this.m_iTranspProtocolUI = iTranspProtocolUI; }

    public void setTranspProtocolDataAddress(int iTranspProtocolDataAddress) { this.m_iTranspProtocolDataAddress = iTranspProtocolDataAddress;  }

    public void setTranspProtocolDataType(int iTranspProtocolDataType) { this.m_iTranspProtocolDataType = iTranspProtocolDataType; }

    // Value Format
    public void setValueMinNrCharToShow(int iValueMinNrCharToShow) { this.m_iValueMinNrCharToShow = iValueMinNrCharToShow; }

    public void setValueNrOfDecimal(int iValueNrOfDecimal) { this.m_iValueNrOfDecimal = iValueNrOfDecimal; }

    public void setValueUM(String strValueUM) { this.m_strValueUM = strValueUM; }

    public void setValueUpdateMillis(int iValueUpdateMillis) { this.m_iValueUpdateMillis = iValueUpdateMillis; }

    public void setValueReadOnly(boolean bValueReadOnly) { this.m_bValueReadOnly = bValueReadOnly; }

    public void setValueWriteOnly(boolean bValueWriteOnly) { this.m_bValueWriteOnly = bValueWriteOnly; }

    // Switch
    public void setWriteValueOFF(int iWriteValueOFF) { this.m_iWriteValueOFF = iWriteValueOFF; }

    public void setWriteValueOFFON(int iWriteValueOFFON) { this.m_iWriteValueOFFON = iWriteValueOFFON; }

    public void setWriteValueONOFF(int iWriteValueONOFF) { this.m_iWriteValueONOFF = iWriteValueONOFF; }

    public void setWriteValueON(int iWriteValueON) { this.m_iWriteValueON = iWriteValueON; }

    // Sensor
    public void setSensorTypeID(long lSensorTypeID) {
        this.m_lSensorTypeID = lSensorTypeID;
    }

    public void setSensorValueID(long lSensorValueID) {
        this.m_lSensorValueID = lSensorValueID;
    }

    public void setSensorEnableSimulation(boolean bSensorEnableSimulation) {
        this.m_bSensorEnableSimulation = bSensorEnableSimulation;
    }

    public void setSensorAmplK(float fSensorAmplK) {
        this.m_fSensorAmplK = fSensorAmplK;
    }

    public void setSensorLowPassFilterK(float fSensorLowPassFilterK) {
        this.m_fSensorLowPassFilterK = fSensorLowPassFilterK;
    }

    public void setSensorSampleTimeMillis(int iSensorSampleTimeMillis) {
        this.m_iSensorSampleTimeMillis = iSensorSampleTimeMillis;
    }

    public void setSensorWriteUpdateTimeMillis(int iSensorWriteUpdateTimeMillis) {
        this.m_iSensorWriteUpdateTimeMillis = iSensorWriteUpdateTimeMillis;
    }

    // Get Method
    public int getTypeID() { return m_iTypeID; }

    public long getID() { return m_ID; }

    public boolean getSaved() { return m_bSaved; }

    public long getRoomID() { return m_lRoomID; }

    public String getTag() {
        return m_strTAG;
    }

    public float getPosX() {
        return m_fPosX;
    }

    public float getPosY() { return m_fPosY; }

    public float getPosZ() {
        return m_fPosZ;
    }

    public boolean getVertical() { return m_bVertical; }

    // Protocol_
    public boolean getTranspProtocolEnable() { return m_bTranspProtocolEnable; }

    public long getTranspProtocolID() {
        return m_lTranspProtocolID;
    }

    public int getTranspProtocolUI() { return m_iTranspProtocolUI; }

    public int getTranspProtocolDataAddress() { return m_iTranspProtocolDataAddress; }

    public int getTranspProtocolDataType() { return m_iTranspProtocolDataType; }

    // Format Number
    public int getValueMinNrCharToShow() {
        return m_iValueMinNrCharToShow;
    }

    public int getValueNrOfDecimal() {
        return m_iValueNrOfDecimal;
    }

    public String getValueUM() {
        return m_strValueUM;
    }

    public int getValueUpdateMillis() { return m_iValueUpdateMillis; }

    public boolean getValueReadOnly() { return m_bValueReadOnly; }

    public boolean getValueWriteOnly() { return m_bValueWriteOnly; }

    // Switch
    public int getWriteValueOFF() { return m_iWriteValueOFF; }

    public int getWriteValueOFFON() { return m_iWriteValueOFFON; }

    public int getWriteValueONOFF() { return m_iWriteValueONOFF; }

    public int getWriteValueON() { return m_iWriteValueON; }

    // Sensor
    public long getSensorTypeID() {
        return m_lSensorTypeID;
    }

    public long getSensorValueID() {
        return m_lSensorValueID;
    }

    public boolean getSensorEnableSimulation() {
        return m_bSensorEnableSimulation;
    }

    public float getSensorAmplK() {
        return m_fSensorAmplK;
    }

    public float getSensorLowPassFilterK() {
        return m_fSensorLowPassFilterK;
    }

    public int getSensorSampleTimeMillis() {
        return m_iSensorSampleTimeMillis;
    }

    public int getSensorWriteUpdateTimeMillis() {
        return m_iSensorWriteUpdateTimeMillis;
    }

    protected ControlData(Parcel in) {
        m_iTypeID = in.readInt();
        m_ID = in.readLong();
        m_bSaved = in.readByte() != 0;
        m_lRoomID = in.readLong();
        m_strTAG = in.readString();
        m_fPosX = in.readFloat();
        m_fPosY = in.readFloat();
        m_fPosZ = in.readFloat();
        m_bVertical = in.readByte() != 0;

        m_bTranspProtocolEnable = in.readByte() != 0;
        m_lTranspProtocolID = in.readLong();
        m_iTranspProtocolUI = in.readInt();
        m_iTranspProtocolDataAddress = in.readInt();
        m_iTranspProtocolDataType = in.readInt();

        m_iValueMinNrCharToShow = in.readInt();
        m_iValueNrOfDecimal = in.readInt();
        m_strValueUM = in.readString();
        m_iValueUpdateMillis = in.readInt();
        m_bValueReadOnly = in.readByte() != 0;
        m_bValueWriteOnly = in.readByte() != 0;

        m_iWriteValueOFF = in.readInt();
        m_iWriteValueOFFON = in.readInt();
        m_iWriteValueONOFF = in.readInt();
        m_iWriteValueON = in.readInt();

        m_lSensorTypeID = in.readLong();
        m_lSensorValueID = in.readLong();
        m_bSensorEnableSimulation = in.readByte() != 0;
        m_fSensorAmplK = in.readFloat();
        m_fSensorLowPassFilterK = in.readFloat();
        m_iSensorSampleTimeMillis = in.readInt();
        m_iSensorWriteUpdateTimeMillis = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_iTypeID);
        dest.writeLong(m_ID);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeLong(m_lRoomID);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
        dest.writeByte((byte) (m_bVertical ? 1 : 0));

        dest.writeByte((byte) (m_bTranspProtocolEnable ? 1 : 0));
        dest.writeLong(m_lTranspProtocolID);
        dest.writeInt(m_iTranspProtocolUI);
        dest.writeInt(m_iTranspProtocolDataAddress);
        dest.writeInt(m_iTranspProtocolDataType);

        dest.writeInt(m_iValueMinNrCharToShow);
        dest.writeInt(m_iValueNrOfDecimal);
        dest.writeString(m_strValueUM);
        dest.writeInt(m_iValueUpdateMillis);
        dest.writeByte((byte) (m_bValueReadOnly ? 1 : 0));
        dest.writeByte((byte) (m_bValueWriteOnly ? 1 : 0));

        dest.writeInt(m_iWriteValueOFF);
        dest.writeInt(m_iWriteValueOFFON);
        dest.writeInt(m_iWriteValueONOFF);
        dest.writeInt(m_iWriteValueON);

        dest.writeLong(m_lSensorTypeID);
        dest.writeLong(m_lSensorValueID);
        dest.writeByte((byte) (m_bSensorEnableSimulation ? 1 : 0));
        dest.writeFloat(m_fSensorAmplK);
        dest.writeFloat(m_fSensorLowPassFilterK);
        dest.writeInt(m_iSensorSampleTimeMillis);
        dest.writeInt(m_iSensorWriteUpdateTimeMillis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ControlData> CREATOR = new Parcelable.Creator<ControlData>() {
        @Override
        public ControlData createFromParcel(Parcel in) {
            return new ControlData(in);
        }

        @Override
        public ControlData[] newArray(int size) {
            return new ControlData[size];
        }
    };

    public enum ControlType {
        SWITCH(0, "Switch"),
        VALUE(1, "Value"),
        RAW_SENSOR(2, "Raw Sensor"),
        CAL_SENSOR(3, "Cal. Sensor");

        private int m_iID;
        private String m_strName;

        ControlType(int iID, String strName) {

            m_iID = iID;
            m_strName = strName;
        }

        public int getID() {
            return m_iID;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iID) + "-" + m_strName;
        }
    }

    public enum SensorTypeCalibr {
        COMPASS(0, "Compass");
/*
        Y_1(1, "Y1 axis value"),
        Z_1(2, "Z1 axis value"),
        X_2(3, "X2 axis or aux value 1"),
        Y_2(4, "Y2 axis or aux value 2"),
        Z_2(5, "Z2 axis or aux value 3");
*/
        private long m_lSensorTypeCalibrID;
        private String m_strSensorTypeCalibrName;

        SensorTypeCalibr(long lSensorTypeCalibrID, String strSensorTypeCalibrName) {
            m_lSensorTypeCalibrID = lSensorTypeCalibrID;
            m_strSensorTypeCalibrName = strSensorTypeCalibrName;
        }

        public static SensorTypeCalibr getSensorTypeCalibr(long lSensorTypeCalibrID) {
            if(lSensorTypeCalibrID == SensorTypeCalibr.COMPASS.m_lSensorTypeCalibrID) {
                return SensorTypeCalibr.COMPASS;
            }

            return null;
        }

        public static ArrayList<SensorTypeCalibr> getListSensorTypeCalibr() {
            ArrayList<SensorTypeCalibr> alstc;
            alstc = new ArrayList<>();
            alstc.add(COMPASS);
            return alstc;
        }

        @Override
        public String toString() {
            return Long.toString(m_lSensorTypeCalibrID) + "-" + m_strSensorTypeCalibrName;
        }

    }

    public enum SensorValue {
        X_1(0, "X1 axis value or single value"),
        Y_1(1, "Y1 axis value"),
        Z_1(2, "Z1 axis value"),
        X_2(3, "X2 axis or aux value 1"),
        Y_2(4, "Y2 axis or aux value 2"),
        Z_2(5, "Z2 axis or aux value 3");

        private long m_lSensorValueID;
        private String m_strSensorValueName;

        SensorValue(long lSensorValueID, String strSensorValueName) {
            m_lSensorValueID = lSensorValueID;
            m_strSensorValueName = strSensorValueName;
        }

        public static SensorValue getSensorValue(long lSensorValueID) {
            if(lSensorValueID == SensorValue.X_1.m_lSensorValueID) {
                return SensorValue.X_1;
            }
            if(lSensorValueID == SensorValue.Y_1.m_lSensorValueID) {
                return SensorValue.Y_1;
            }
            if(lSensorValueID == SensorValue.Z_1.m_lSensorValueID) {
                return SensorValue.Z_1;
            }
            if(lSensorValueID == SensorValue.X_2.m_lSensorValueID) {
                return SensorValue.X_2;
            }
            if(lSensorValueID == SensorValue.Y_2.m_lSensorValueID) {
                return SensorValue.Y_2;
            }
            if(lSensorValueID == SensorValue.Z_2.m_lSensorValueID) {
                return SensorValue.Z_2;
            }
            return null;
        }
        /*
                public static List<SensorValue> getListSensorValue() {
                    List<SensorValue> list;
                    list = new ArrayList<SensorValue>();
                            list.add(X_1);
                            list.add(Y_1);
                            list.add(Z_1);
                            list.add(X_2);
                            list.add(Y_2);
                            list.add(Z_2);
                    return list;
                }
        */
        @Override
        public String toString() {
            return Long.toString(m_lSensorValueID) + "-" + m_strSensorValueName;
        }

    }
}
