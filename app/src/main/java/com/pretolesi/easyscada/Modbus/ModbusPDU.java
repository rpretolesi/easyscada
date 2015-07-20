package com.pretolesi.easyscada.Modbus;

/**
 *
 */
public class ModbusPDU {
    private short m_shUID;
    private short m_shFEC;
    private short m_shExCID;
    private String m_strExCDescr;
    private byte[] m_aPDUValue;
    private short m_shPDULenght;

    public ModbusPDU(short shUID, short shFEC, short shExCID, String strExCDescr, byte[] aPDUValue, short shPDULenght) {
        m_shUID = shUID;
        m_shFEC = shFEC;
        m_shExCID = shExCID;
        m_strExCDescr = strExCDescr;
        m_aPDUValue = aPDUValue;
        m_shPDULenght = shPDULenght;
    }

    public short getUID() { return m_shUID; }

    public short getFEC(){
        return m_shFEC;
    }

    public short getExCID(){
        return m_shExCID;
    }

    public String getExCDescr(){
        return m_strExCDescr;
    }

    public short getPDULenght(){ return m_shPDULenght; }

    public byte[] getPDUValue(){
        return m_aPDUValue;
    }

}
