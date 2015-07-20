package com.pretolesi.easyscada.Modbus;

/**
 * Created by ricca_000 on 20/04/2015.
 */
public class ModbusUnitIdOutOfRangeException  extends Exception {

    public ModbusUnitIdOutOfRangeException(String message) {
        super(message);
    }

    public ModbusUnitIdOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusUnitIdOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
