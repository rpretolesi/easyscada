package com.pretolesi.easyscada.IO;

import com.pretolesi.easyscada.R;

/**
 *
 */

public class ClientReadStatus {

    private long m_lServerID;
    private int m_iTID;
    private int m_iUID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;
    private Object m_objValue;

    public ClientReadStatus(){
        m_lServerID = -1;
        m_iTID = -1;
        m_iUID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
        m_objValue = null;
    }

    public ClientReadStatus(long lServerID, int iTID, int iUID, Status sStatus, int iErrorCode, String strErrorMessage, Object objValue){
        m_lServerID = lServerID;
        m_iTID = iTID;
        m_iUID = iUID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
        m_objValue = objValue;
    }

    public void setServerID(long lServerID){
        m_lServerID = lServerID;
    }

    public void setTID(int iTID){ m_iTID = iTID; }

    public void setUnitID(int iUnitID){ m_iUID = iUnitID; }

    public long getServerID(){
        return m_lServerID;
    }

    public int getTID(){
        return m_iTID;
    }

    public int getUID(){
        return m_iUID;
    }

    public Status getStatus(){
        return m_sStatus;
    }

    public int getErrorCode(){
        return m_iErrorCode;
    }

    public String getErrorMessage(){
        return m_strErrorMessage;
    }

    public Object getValue(){
        return m_objValue;
    }
/*
    public byte[] getValue(){
        return m_abyteValue;
    }
*/
    public static enum Status
    {
        IDLE(0, R.string.ticwos_idle),
        OK(1, R.string.ticwos_ok),
        ERROR(2, R.string.ticwos_error),
        TIMEOUT(3, R.string.ticwos_timeout);

        private int ID;
        private int StringResID;

        private Status(int ID, int StringResID) {
            this.ID = ID;
            this.StringResID = StringResID;
        }

        public int getID() {
            return ID;
        }

        public int getStringResID() {
            return StringResID;
        }
    }
}
