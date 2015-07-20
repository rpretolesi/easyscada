package com.pretolesi.easyscada.Modbus;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusPDULengthOutOfRangeException extends Exception {

    public ModbusPDULengthOutOfRangeException(String message) {
        super(message);
    }

    public ModbusPDULengthOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusPDULengthOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}