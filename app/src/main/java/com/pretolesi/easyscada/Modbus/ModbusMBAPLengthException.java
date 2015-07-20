package com.pretolesi.easyscada.Modbus;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusMBAPLengthException extends Exception {

    public ModbusMBAPLengthException(String message) {
        super(message);
    }

    public ModbusMBAPLengthException(Throwable throwable) {
        super(throwable);
    }

    public ModbusMBAPLengthException(String message, Throwable throwable) {
        super(message, throwable);
    }
}