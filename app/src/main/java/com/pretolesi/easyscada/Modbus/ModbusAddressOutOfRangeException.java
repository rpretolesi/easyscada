package com.pretolesi.easyscada.Modbus;

/**
 *
 */
public class ModbusAddressOutOfRangeException extends Exception {

    public ModbusAddressOutOfRangeException(String message) {
        super(message);
    }

    public ModbusAddressOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusAddressOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
