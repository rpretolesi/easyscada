package com.pretolesi.easyscada.IO;

import com.pretolesi.easyscada.R;

/**
 *
 */
public class ClientStatus {

    private long m_lServerID;
    private String m_strServerName;
    private Status m_sStatus;
    private String m_strError;

    public ClientStatus(){
        m_lServerID = -1;
        m_strServerName = "";
        m_sStatus = Status.IDLE;
        m_strError = "";
    }

    public ClientStatus(long lServerID, String strServerName, Status sStatus, String strError){
        m_lServerID = lServerID;
        m_strServerName = strServerName;
        m_sStatus = sStatus;
        m_strError = strError;
    }

    public void setData(long lServerID, String strServerName, Status sStatus, String strError){
        m_lServerID = lServerID;
        m_strServerName = strServerName;
        m_sStatus = sStatus;
        m_strError = strError;
    }

    public void setData(ClientStatus pud){
        m_lServerID = pud.getServerID();
        m_strServerName = pud.getServerName();
        m_sStatus = pud.getStatus();
        m_strError = pud.getError();
    }

    public void resetData(){
        m_lServerID = -1;
        m_strServerName = "";
        m_sStatus = Status.IDLE;
        m_strError = "";
    }

    public long getServerID(){
        return m_lServerID;
    }
    public String getServerName(){
        return m_strServerName;
    }

    public Status getStatus(){
        return m_sStatus;
    }

    public String getError(){
        return m_strError;
    }

    public static enum Status
    {
        IDLE(0, R.string.tics_idle),
        OFFLINE(1, R.string.tics_offline),
        CONNECTING(2, R.string.tics_connecting),
        ONLINE(3, R.string.tics_online),
        ERROR(4, R.string.tics_error),
        TIMEOUT(5, R.string.tics_timeout),
        DISCONNECTING(6, R.string.tics_disconnecting);

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
