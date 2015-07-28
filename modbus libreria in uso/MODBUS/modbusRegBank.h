
#include <stdint.h>
#include <Arduino.h>

#ifndef _MODBUSREGBANK
#define _MODBUSREGBANK

#include <modbus.h>

struct modbusDigReg
{
	word address;
	byte value;

	modbusDigReg *next;
};

struct modbusAnaReg
{
	word address;
	word value;

	modbusAnaReg *next;
};

class modbusRegBank
{
	public:

		modbusRegBank(void);
		
		void add(word);
		word get(word addr, word *wValue);
		word getShort(word addr, short *shValue);
		word getLong(word addr, long *);
		word set(word, word);
		word setShort(word, short);
		word setLong(word, long);

	private:
		void * search(word);
		
		modbusDigReg	*_digRegs,
						*_lastDigReg;
							
		modbusAnaReg	*_anaRegs,
						*_lastAnaReg;
};
#endif
