package com.pretolesi.easyscada.Modbus;

/**
 * Created by ricca_000 on 20/04/2015.
 */
public class ModbusTransIdOutOfRangeException extends Exception {

    public ModbusTransIdOutOfRangeException(String message) {
        super(message);
    }

    public ModbusTransIdOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusTransIdOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
