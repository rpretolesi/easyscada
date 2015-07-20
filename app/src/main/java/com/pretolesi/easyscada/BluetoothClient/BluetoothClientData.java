package com.pretolesi.easyscada.BluetoothClient;

/**
 * Created by RPRETOLESI on 09/06/2015.
 */
public class BluetoothClientData {

    private  String m_strName;
    private  String m_strAddress;
    private  boolean m_bPaired;

    public BluetoothClientData() {
    }

    public BluetoothClientData(String strName, String strAddress, boolean bPaired)
    {
        super();

        this.m_strName = strName;
        this.m_strAddress = strAddress;
        this.m_bPaired = bPaired;
    }

    public String getName() {
        return m_strName;
    }

    public void setName(String strName) {
        this.m_strName = strName;
    }

    public String getAddress() {
        return m_strAddress;
    }

    public void setAddress(String strAddress) {
        this.m_strAddress = strAddress;
    }

    public boolean getPaired() {
        return m_bPaired;
    }

    public void setPaired(boolean bPaired) {
        this.m_bPaired = bPaired;
    }

}
