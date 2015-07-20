package com.pretolesi.easyscada.Modbus;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusCRCException extends Exception {

    public ModbusCRCException(String message) {
        super(message);
    }

    public ModbusCRCException(Throwable throwable) {
        super(throwable);
    }

    public ModbusCRCException(String message, Throwable throwable) {
        super(message, throwable);
    }
}