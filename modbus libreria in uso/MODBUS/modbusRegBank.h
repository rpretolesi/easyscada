
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
		word get(word);
		short getShort(word addr);
		bool getLong(word addr, long *);
		word set(word, word);
		void setShort(word, short);
		void setLong(word, long);

	private:
		void * search(word);
		
		modbusDigReg	*_digRegs,
						*_lastDigReg;
							
		modbusAnaReg	*_anaRegs,
						*_lastAnaReg;
};
#endif
