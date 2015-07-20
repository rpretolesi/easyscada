package com.pretolesi.easyscada.Modbus;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusByteCountOutOfRangeException extends Exception {

    public ModbusByteCountOutOfRangeException(String message) {
        super(message);
    }

    public ModbusByteCountOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusByteCountOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}