package com.pretolesi.easyscada.Modbus;

/**
 * Created by ricca_000 on 26/04/2015.
 */
public class ModbusMBAP {
    private short m_shTID;
    private short m_shPID;
    private short m_shLength;

    public ModbusMBAP(short shTID, short shPID, short shLength) {
        m_shTID = shTID;
        m_shPID = shPID;
        m_shLength = shLength;
    }

    public short getTID(){
        return m_shTID;
    }

    public short getPID(){
        return m_shPID;
    }

    public short getLength(){ return m_shLength; }

}
