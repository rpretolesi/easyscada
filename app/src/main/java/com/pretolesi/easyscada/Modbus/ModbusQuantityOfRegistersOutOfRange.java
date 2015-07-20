package com.pretolesi.easyscada.Modbus;

/**
 * Created by ricca_000 on 29/04/2015.
 */
public class ModbusQuantityOfRegistersOutOfRange  extends Exception {

    public ModbusQuantityOfRegistersOutOfRange(String message) {
        super(message);
    }

    public ModbusQuantityOfRegistersOutOfRange(Throwable throwable) {
        super(throwable);
    }

    public ModbusQuantityOfRegistersOutOfRange(String message, Throwable throwable) {
        super(message, throwable);
    }
}
