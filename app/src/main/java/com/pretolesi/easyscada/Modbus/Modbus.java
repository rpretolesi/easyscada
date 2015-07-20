package com.pretolesi.easyscada.Modbus;

import android.content.Context;

import com.pretolesi.easyscada.CommClientData.TranspProtocolData.CommProtocolType;
import com.pretolesi.easyscada.CustomControls.NumericDataType.DataType;
import com.pretolesi.easyscada.R;
import com.pretolesi.easyscada.IO.ClientMsg;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 *
 */
public class Modbus {

    public static synchronized ClientMsg writeShort(Context context, int iTID, int iUID, int iAddress, int iValue, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ClientMsg tim = null;
        int[] iaValue = new int[1];
        iaValue[0] = iValue;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 1, DataType.SHORT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 1, DataType.SHORT);
        }

        return tim;
    }

    public static synchronized ClientMsg writeInteger(Context context, int iTID, int iUID, int iAddress, long lValue, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {

        ClientMsg tim = null;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(0, (int)lValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 2, DataType.INT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 2, DataType.INT);
        }
        return tim;
    }

    public static synchronized ClientMsg writeLong(Context context, int iTID, int iUID, int iAddress, long lValue, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {

        ClientMsg tim = null;
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(0, lValue);
        int[] iaValue = new int[4];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        iaValue[2] = bb.getShort(4);
        iaValue[3] = bb.getShort(6);
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 4, DataType.LONG);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 4, DataType.LONG);
        }
        return tim;
    }

    public static synchronized ClientMsg writeFloat(Context context, int iTID, int iUID, int iAddress, float fValue, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {

        ClientMsg tim = null;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putFloat(0, fValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 2, DataType.FLOAT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 2, DataType.FLOAT);
        }
        return tim;
    }

    public static synchronized ClientMsg writeDouble(Context context, int iTID, int iUID, int iAddress, double dblValue, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {

        ClientMsg tim = null;
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putDouble(0, dblValue);
        int[] iaValue = new int[4];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        iaValue[2] = bb.getShort(4);
        iaValue[3] = bb.getShort(6);
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 4, DataType.DOUBLE);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 4, DataType.DOUBLE);
        }
        return tim;
    }

    private static synchronized ClientMsg writeMultipleRegistersOnTcp(Context context, int iTID, int iUID, int iAddress, int[] iaValue, int iNrOfRegisters, DataType dt) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusValueOutOfRangeException {
        short shTID;
        byte byteUID;
        short shAddress;
        short shNrOfRegisters;
        byte byteByteCount;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }

        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusUnitIdOutOfRangeException));
        }
        if(iAddress >= 0 && iAddress <= 65535){
            shAddress = (short) iAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }
        if(iNrOfRegisters > 0 && iNrOfRegisters < 124) {
            shNrOfRegisters = (short) iNrOfRegisters;
            byteByteCount = (byte)(shNrOfRegisters * 2);
        } else {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(13 + byteByteCount);
        bb.putShort(shTID);
        bb.putShort((short) 0);
        bb.putShort((short) (7 + byteByteCount));
        bb.put(byteUID);
        bb.put((byte) 0x10);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);
        bb.put(byteByteCount);

        if(iaValue != null && iaValue.length > 0){
            for (int iValue : iaValue) {
                if (iValue >= -32768  && iValue <= 32767 ) {
                    bb.putShort((short) iValue);
                } else {
                    throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
                }
            }
        } else {
            throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
        }

        return new ClientMsg(iTID, byteUID, bb.array(), dt, 1);
    }

    private static synchronized ClientMsg writeMultipleRegistersOnSerial(Context context, int iTID, int iUID, int iAddress, int[] iaValue, int iNrOfRegisters, DataType dt) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusValueOutOfRangeException {
        short shTID;
        byte byteUID;
        short shAddress;
        short shNrOfRegisters;
        byte byteByteCount;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }

        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusUnitIdOutOfRangeException));
        }
        if(iAddress >= 0 && iAddress <= 65535){
            shAddress = (short) iAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }
        if(iNrOfRegisters > 0 && iNrOfRegisters < 124) {
            shNrOfRegisters = (short) iNrOfRegisters;
            byteByteCount = (byte)(shNrOfRegisters * 2);
        } else {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(7 + byteByteCount + 2);
        bb.put(byteUID);
        bb.put((byte) 0x10);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);
        bb.put(byteByteCount);

        if(iaValue != null && iaValue.length > 0){
            for (int iValue : iaValue) {
                if (iValue >= -32768  && iValue <= 32767 ) {
                    bb.putShort((short) iValue);
                } else {
                    throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
                }
            }
        } else {
            throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
        }

//        bb.putShort(getCRC(bb.array(), bb.array().length - 2));

        ByteBuffer bbCRC = ByteBuffer.allocate(2);
        bbCRC.putShort(getCRC(bb.array(), bb.array().length - 2));

        bb.put(bbCRC.get(1));
        bb.put(bbCRC.get(0));

        return new ClientMsg(iTID, byteUID, bb.array(), dt, 1);
    }

    public static synchronized ClientMsg readShort(Context context, int iTID, int iUID, int iAddress, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        ClientMsg tim = null;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = readHoldingRegistersOnTcp(context, iTID, iUID, iAddress, (short) 1, DataType.SHORT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = readHoldingRegistersOnSerial(context, iTID, iUID, iAddress, (short) 1, DataType.SHORT);
        }
        return tim;
    }

    public static synchronized ClientMsg readInt(Context context, int iTID, int iUID, int iAddress, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        ClientMsg tim = null;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = readHoldingRegistersOnTcp(context, iTID, iUID, iAddress, (short) 2, DataType.INT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = readHoldingRegistersOnSerial(context, iTID, iUID, iAddress, (short) 2, DataType.INT);
        }
        return tim;
    }

    public static synchronized ClientMsg readLong(Context context, int iTID, int iUID, int iAddress, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        ClientMsg tim = null;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = readHoldingRegistersOnTcp(context, iTID, iUID, iAddress, (short) 4, DataType.LONG);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = readHoldingRegistersOnSerial(context, iTID, iUID, iAddress, (short) 4, DataType.LONG);
        }
        return tim;
    }

    public static synchronized ClientMsg readFloat(Context context, int iTID, int iUID, int iAddress, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        ClientMsg tim = null;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = readHoldingRegistersOnTcp(context, iTID, iUID, iAddress, (short) 2, DataType.FLOAT);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = readHoldingRegistersOnSerial(context, iTID, iUID, iAddress, (short) 2, DataType.FLOAT);
        }
        return tim;
    }

    public static synchronized ClientMsg readDouble(Context context, int iTID, int iUID, int iAddress, CommProtocolType p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        ClientMsg tim = null;
        if(p == CommProtocolType.MODBUS_ON_TCP_IP){
            tim = readHoldingRegistersOnTcp(context, iTID, iUID, iAddress, (short) 4, DataType.DOUBLE);
        }
        if(p == CommProtocolType.MODBUS_ON_SERIAL){
            tim = readHoldingRegistersOnSerial(context, iTID, iUID, iAddress, (short) 4, DataType.DOUBLE);
        }
        return tim;
    }

    private static synchronized ClientMsg readHoldingRegistersOnTcp(Context context, int iTID, int iUID, int iStartingAddress, short shNrOfRegisters, DataType dt) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException,  ModbusQuantityOfRegistersOutOfRange {
        short shTID;
        byte byteUID;
        short shAddress;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iStartingAddress >= 0 && iStartingAddress <= 65535){
            shAddress = (short) iStartingAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }

        if(shNrOfRegisters < 1 || shNrOfRegisters > 125) {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.putShort(shTID);
        bb.putShort((short) 0);
        bb.putShort((short) 6);
        bb.put(byteUID);
        bb.put((byte)0x03);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);

        return new ClientMsg(iTID, byteUID, bb.array(), dt, 0);
    }

    private static synchronized ClientMsg readHoldingRegistersOnSerial(Context context, int iTID, int iUID, int iStartingAddress, short shNrOfRegisters, DataType dt) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException,  ModbusQuantityOfRegistersOutOfRange {
        short shTID;
        byte byteUID;
        short shAddress;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iStartingAddress >= 0 && iStartingAddress <= 65535){
            shAddress = (short) iStartingAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }

        if(shNrOfRegisters < 1 || shNrOfRegisters > 125) {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.put(byteUID);
        bb.put((byte) 0x03);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);

//        bb.putShort(getCRC(bb.array(), bb.array().length - 2));

        ByteBuffer bbCRC = ByteBuffer.allocate(2);
        bbCRC.putShort(getCRC(bb.array(), bb.array().length - 2));

        bb.put(bbCRC.get(1));
        bb.put(bbCRC.get(0));

        return new ClientMsg(iTID, byteUID, bb.array(), dt, 0);
    }

    public static synchronized ModbusMBAP getMBAP(Context context, byte[] byteMBA) throws ModbusProtocolOutOfRangeException, ModbusPDULengthOutOfRangeException, ModbusMBAPLengthException {
        // Max message length 260 byte
        if(byteMBA != null && byteMBA.length == 6){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            short shTID = bb.getShort(); // Transaction Identifier
            short shPID = bb.getShort(); // Protocol_
            if(shPID != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            short shLength = bb.getShort(); // Length
            if(shLength < 3 || shLength > 254){
                throw new ModbusPDULengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            return new ModbusMBAP(shTID, shPID, shLength);
        }

        throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
    }

    public static synchronized ModbusPDU getPDU(Context context, byte[] bytePDUValue, short shPDULenght, boolean bCheckCRC) throws ModbusPDULengthOutOfRangeException, ModbusCRCException, ModbusUnitIdOutOfRangeException, ModbusByteCountOutOfRangeException {
        // Max total message length 260 byte
        ModbusPDU mpdu = null;

        if(bytePDUValue == null) {
            return null;
        }

        if(shPDULenght < 3 || shPDULenght > 247){
            throw new ModbusPDULengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
        }

        if(bCheckCRC) {
            // Controllo CRC
            ByteBuffer bbCRC = ByteBuffer.allocate(2);
            bbCRC.putShort(getCRC(bytePDUValue, shPDULenght - 2));
            if ((bytePDUValue[shPDULenght - 1] != bbCRC.get(0)) || (bytePDUValue[shPDULenght - 2] != bbCRC.get(1))) {
                throw new ModbusCRCException(context.getString(R.string.ModbusCRCException));
            }
        }
        ByteBuffer bb = ByteBuffer.wrap(bytePDUValue);
         // Unit Identifier
        short shUI = bb.get();
        if(shUI < 0 || shUI > 247){
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusUnitIdOutOfRangeException));
        }

        // Function Code
        short shFEC = (short)(bb.get() & 0xFF);
        byte byteExceptionCode = 0;

        switch(shFEC) {
            case 0x10:
                short shAddress = bb.getShort();
                short shQuantityOfRegisters = bb.getShort();

                mpdu = new ModbusPDU(shUI, shFEC, (short)0, "", null, (short)0);

                break;

            case 0x90:
                byteExceptionCode = bb.get();

                mpdu = new ModbusPDU(shUI, shFEC, byteExceptionCode, getExceptionCodeDescription(context, byteExceptionCode), null, (short)0);

                break;

            case 0x03:
                short shByteCount = (short)(bb.get() & 0xFF);
                if(shByteCount < 1 || shPDULenght > 125){
                    throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                }
                byte[] byteBuffer = new byte[shByteCount];
                for(short shIndice = 0; shIndice < shByteCount; shIndice++){
                    try{
                        byteBuffer[shIndice] = bb.get();
                    } catch(BufferUnderflowException ex) {
                        throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                    }
                }

                mpdu = new ModbusPDU(shUI, shFEC, (short)0, "", byteBuffer, shByteCount);

                break;

            case 0x83:
                byteExceptionCode = bb.get();

                mpdu = new ModbusPDU(shUI, shFEC, byteExceptionCode, getExceptionCodeDescription(context, byteExceptionCode), null, (short)0);

                break;
        }

        return mpdu;
    }

    private static String getExceptionCodeDescription(Context context, byte byteExC ){
        String strDescription = "";
        switch(byteExC) {
            case 0x01:
                strDescription = context.getString(R.string.ModbusIllegalFunctionException);
                break;

            case 0x02:
                strDescription = context.getString(R.string.ModbusIllegalDataAddressException);
                break;

            case 0x03:
                strDescription = context.getString(R.string.ModbusIllegalDataValueException);
                break;

            case 0x04:
                strDescription = context.getString(R.string.ModbusServerDeviceFailureException);
                break;

            default:
                strDescription = context.getString(R.string.ModbusUnknowException);

        }

        return strDescription;
    }

    private static short getCRC(byte[] buf, int len)
    {
        int crc = 0xFFFF;

        for (int pos = 0; pos < len; pos++) {
//            crc ^= (int)buf[pos];          // XOR byte into least sig. byte of crc
            crc ^= (int)(0x00ff & buf[pos]);  // FIX HERE -- XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) {    // Loop over each bit
                if ((crc & 0x0001) != 0) {      // If the LSB is set
                    crc >>= 1;                    // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                }
                else                            // Else LSB is not set
                    crc >>= 1;                    // Just shift right
            }
        }
        // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return (short)crc;
    }
}
