#include <stdint.h>
#include <Arduino.h>

#ifndef MODBUSUTILS
#define MODBUSUTILS

class modbusUtils
{
	public:
		modbusUtils(void);

        void setShortToBytes(short, byte*);
	    short getShortFromBytes(byte*);
        void setLongToBytes(long , byte*);
        long getLongFromBytes(byte*);
        void setFloatToBytes(float, byte*);
        float getFloatFromBytes(byte*);
}