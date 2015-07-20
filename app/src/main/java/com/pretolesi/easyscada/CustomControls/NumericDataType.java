package com.pretolesi.easyscada.CustomControls;

import java.nio.ByteBuffer;

/**
 *
 */
public class NumericDataType {

    public synchronized static Object getShort(byte[] abyteValue){
        Object obj = null;
        if(abyteValue != null) {
            try{
                obj = ByteBuffer.wrap(abyteValue).getShort();
            } catch(Exception ignored){
            }
        }
        return obj;
    }

    public synchronized static Object getInt(byte[] abyteValue){
        Object obj = null;
        if(abyteValue != null) {
            try{
                obj = ByteBuffer.wrap(abyteValue).getInt();
            } catch(Exception ignored){
            }
        }
        return obj;
    }

    public synchronized static Object getLong(byte[] abyteValue){
        Object obj = null;
        if(abyteValue != null) {
            try{
                obj = ByteBuffer.wrap(abyteValue).getLong();
            } catch(Exception ignored){
            }
        }
        return obj;
    }

    public synchronized static Object getFloat(byte[] abyteValue){
        Object obj = null;
        if(abyteValue != null) {
            try{
                obj = ByteBuffer.wrap(abyteValue).getFloat();
            } catch(Exception ignored){
            }
        }
        return obj;
    }

    public synchronized static Object getDouble(byte[] abyteValue){
        Object obj = null;
        if(abyteValue != null) {
            try{
                obj = ByteBuffer.wrap(abyteValue).getDouble();
            } catch(Exception ignored){
            }
        }
        return obj;
    }

    public enum DataType {
        SHORT(0, "Integer 16 bit"),
        INT(1, "Integer 32 bit"),
        LONG(2, "Integer 64 bit"),
        FLOAT(3, "Float with single precision IEEE 754 32 bit"),
        DOUBLE(4, "Float with double precision IEEE 754 64 bit");

        private int m_iDataTypeID;
        private String m_strDataTypeName;

        DataType(int iDataTypeID, String strDataTypeName) {

            m_iDataTypeID = iDataTypeID;
            m_strDataTypeName = strDataTypeName;
        }

        public int getTypeID() { return m_iDataTypeID; }

        public static DataType getDataType(int iDataTypeID) {
            if(iDataTypeID == DataType.SHORT.m_iDataTypeID) {
                return DataType.SHORT;
            }
            if(iDataTypeID == DataType.INT.m_iDataTypeID) {
                return DataType.INT;
            }
            if(iDataTypeID == DataType.LONG.m_iDataTypeID) {
                return DataType.LONG;
            }
            if(iDataTypeID == DataType.FLOAT.m_iDataTypeID) {
                return DataType.FLOAT;
            }
            if(iDataTypeID == DataType.DOUBLE.m_iDataTypeID) {
                return DataType.DOUBLE;
            }
            return null;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iDataTypeID) + "-" + m_strDataTypeName;
        }
    }
}
