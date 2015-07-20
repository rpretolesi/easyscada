package com.pretolesi.easyscada.IO;

import com.pretolesi.easyscada.R;

/**
 *
 */

public class ClientWriteStatus {

    private long m_lServerID;
    private int m_iTID;
    private int m_iUID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;

    public ClientWriteStatus(){
        m_lServerID = -1;
        m_iTID = -1;
        m_iUID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
    }

    public ClientWriteStatus(long lServerID, int iTID, int iUID, Status sStatus, int iErrorCode, String strErrorMessage){
        m_lServerID = lServerID;
        m_iTID = iTID;
        m_iUID = iUID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
    }

    public void setData(long lServerID, int iTID, int iUID, Status sStatus, int iErrorCode, String strErrorMessage){
        m_lServerID = lServerID;
        m_iTID = iTID;
        m_iUID = iUID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
    }

    public void setData(ClientWriteStatus ticws){
        m_lServerID = ticws.getServerID();
        m_iTID = ticws.getTID();
        m_iUID = ticws.getUID();
        m_sStatus = ticws.getStatus();
        m_iErrorCode = ticws.getErrorCode();
        m_strErrorMessage = ticws.getErrorMessage();
    }

    public void resetData(){
        m_lServerID = -1;
        m_iTID = -1;
        m_iUID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
    }

    public void setServerID(long lServerID){
        m_lServerID = lServerID;
    }

    public void setTID(int iTID){ m_iTID = iTID; }

    public void setUID(int iUID){ m_iUID = iUID; }

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
