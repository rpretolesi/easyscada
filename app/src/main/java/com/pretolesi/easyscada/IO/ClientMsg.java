package com.pretolesi.easyscada.IO;

import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;

/**
 *
 */
public class ClientMsg {
    private long m_lTID;
    private long m_lUID;
    private DataType m_dtDataType;
    private byte[] m_byteMsgData;
    private boolean m_bMsgSent;
    private long m_lSentTimeMS;
    private int m_iPriority;

    public ClientMsg(long lTID, long lUID, byte[] byteMsgData, DataType dt, int iPriority){
        m_lTID = lTID;
        m_lUID = lUID;
        m_dtDataType = dt;
        m_byteMsgData = byteMsgData;
        m_bMsgSent = false;
        m_lSentTimeMS = System.currentTimeMillis();
        m_iPriority = iPriority;
    }

    public ClientMsg(ClientMsg tim){
        if(tim != null) {
            m_lTID = tim.getTID();
            m_lUID = tim.getUID();
            m_dtDataType = tim.getDataType();
            m_byteMsgData = tim.getMsgData();
            m_bMsgSent = tim.getMsgSent();
            m_lSentTimeMS = tim.getSentTimeMS();
            m_iPriority = tim.getPriority();
        }
    }

    public long getTID(){
        return m_lTID;
    }

    public long getUID(){
        return m_lUID;
    }

    public DataType getDataType(){
        return m_dtDataType;
    }

    public byte[] getMsgData(){
        return m_byteMsgData;
    }

    public boolean getMsgSent(){ return m_bMsgSent; }

    public long getSentTimeMS(){
        return m_lSentTimeMS;
    }

    public int getPriority(){
        return m_iPriority;
    }

    public void setDataType(DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    public void setMsgAsSent(boolean bMsgSent ){ m_bMsgSent = bMsgSent; }

    public void setMsgTimeMSNow(){ m_lSentTimeMS = System.currentTimeMillis(); }

    public void setPriority(int iPriority){ m_iPriority = iPriority; }

    @Override
    public boolean equals(Object obj) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == obj) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(obj instanceof ClientMsg)) {
            return false;
        }

        final ClientMsg tim = (ClientMsg) obj;
        if (this.m_lTID != tim.m_lTID) {
            return false;
        }
        if (this.m_lUID != tim.m_lUID) {
            return false;
        }
        return true;
    }
}
