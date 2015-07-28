#include <modbusRegBank.h>
#include <stdlib.h>

modbusRegBank::modbusRegBank(void)
{
	_digRegs		= 0;
	_lastDigReg		= 0;
	_anaRegs		= 0;
	_lastAnaReg		= 0;
}


void modbusRegBank::add(word addr)
{
	if(addr<20000)
	{
		modbusDigReg *temp;

		temp = (modbusDigReg *) malloc(sizeof(modbusDigReg));
		temp->address = addr;
		temp->value		= 0;
		temp->next		= 0;

		if(_digRegs == 0)
		{
			_digRegs = temp;
			_lastDigReg = _digRegs;
		}
		else
		{
			//Assign the last register's next pointer to temp;
			_lastDigReg->next = temp;
			//then make temp the last register in the list.
			_lastDigReg = temp;
		}
	}	
	else
	{
		modbusAnaReg *temp;

		temp = (modbusAnaReg *) malloc(sizeof(modbusAnaReg));
		temp->address = addr;
		temp->value = 0;
		temp->next = 0;

		if(_anaRegs == 0)
		{
			_anaRegs = temp;
			_lastAnaReg = _anaRegs;
		}
		else
		{
			_lastAnaReg->next = temp;
			_lastAnaReg = temp;
		}
	}
}

word modbusRegBank::get(word addr)
{
	if(addr < 20000)
	{
		modbusDigReg * regPtr;
		regPtr = (modbusDigReg *) this->search(addr);
		if(regPtr)
			return(regPtr->value);
		else
			return(NULL);	
	}
	else
	{
		modbusAnaReg * regPtr;
		regPtr = (modbusAnaReg *) this->search(addr);
		if(regPtr)
			return(regPtr->value);
		else
			return(NULL);	
	}
}

short modbusRegBank::getShort(word addr)
{
	union {
		word temp_word;
		byte temp_byte[2];
	} u;

	if(addr < 20000)
	{
		return(NULL);
	}
	else
	{
		modbusAnaReg * regPtr;
		regPtr = (modbusAnaReg *) this->search(addr);
		if(regPtr){
			u.temp_word = regPtr->value;
			return (short)(u.temp_byte[1] << 8) | u.temp_byte[0];
		} else {
			return(NULL);
		}
	}
}

bool modbusRegBank::getLong(word addr, long *lValue)
{

	if(addr < 20000)
	{
		return false;
	}
	else
	{
		modbusAnaReg * regPtr;
		word temp_word_0 = 0;
		word temp_word_1 = 0;
		regPtr = (modbusAnaReg *) this->search(addr);
		if(!regPtr){
			return false;
		}
		temp_word_0 = regPtr->value;
        Serial.print("addr: ");
		Serial.print(addr);
        Serial.print(" - temp_word_0: ");
		Serial.println(temp_word_0);

		regPtr = (modbusAnaReg *) this->search(addr + 1);
		if(!regPtr){
			return false;
		}
		temp_word_1 = regPtr->value;
        Serial.print("addr + 1: ");
		Serial.print(addr + 1);
        Serial.print(" - temp_word_1: ");
		Serial.println(temp_word_1);

		if(!lValue){
			return false;
		}

		*lValue = temp_word_0;
        Serial.print("lValue_0: ");
		Serial.println(*lValue);

		*lValue = *lValue << 16 | temp_word_1;
        Serial.print("lValue_1: ");
		Serial.println(*lValue);

		return true;
	}
}

word modbusRegBank::set(word addr, word value)
{
	word wError = 0;
	//for digital data
	if(addr < 20000)
	{
		modbusDigReg * regPtr;
		//search for the register address
		regPtr = (modbusDigReg *) this->search(addr);
		//if a pointer was returned the set the register value to true if value is non zero
		if(regPtr){
			if(value){
				regPtr->value = 0xFF;
			} else {
				regPtr->value = 0x00;
			}
		} else {
			wError = ILLEGAL_DATA_ADDRESS;
		}
	}
	else
	{
		modbusAnaReg * regPtr;
		//search for the register address
		regPtr = (modbusAnaReg *) this->search(addr);
		//if found then assign the register value to the new value.
		if(regPtr){
			regPtr->value = value;
		} else {
			wError = ILLEGAL_DATA_ADDRESS;
		}
	}

	return wError;
}

void modbusRegBank::setShort(word addr, short shValue)
{
	union {
		short temp_short;
		byte temp_byte[2];
	} u;

	u.temp_short = shValue;

	//for digital data
	if(addr < 20000){
	}
	else {
		modbusAnaReg * regPtr;
		//search for the register address
		regPtr = (modbusAnaReg *) this->search(addr);
		//if found then assign the register value to the new value.
		if(regPtr){
			regPtr->value = (u.temp_byte[1] << 8) | u.temp_byte[0];
		}
	}
}

void modbusRegBank::setLong(word addr, long lValue)
{
	union {
		long temp_long;
		byte temp_byte[4];
	} u;

	u.temp_long = lValue;

	//for digital data
	if(addr < 20000){
	}
	else {
		modbusAnaReg * regPtr;
		modbusAnaReg * regNextPtr;
		//search for the register address
		regPtr = (modbusAnaReg *) this->search(addr);
		//if found then assign the register value to the new value.
		if(regPtr){
			regPtr->value = (u.temp_byte[3] << 8) | u.temp_byte[2];
			regNextPtr = regPtr->next;
			if(regNextPtr){
				regNextPtr->value = (u.temp_byte[1] << 8) | u.temp_byte[0];
			}
		}
	}
}

void * modbusRegBank::search(word addr)
{
	//if the requested address is 0-19999 
	//use a digital register pointer assigned to the first digital register
	//else use a analog register pointer assigned the first analog register

	if(addr < 20000)
	{
		modbusDigReg *regPtr = _digRegs;

		//if there is no register configured, bail
		if(regPtr == 0)
			return(0);

		//scan through the linked list until the end of the list or the register is found.
		//return the pointer.
		do
		{
			if(regPtr->address == addr)
				return(regPtr);
			regPtr = regPtr->next;
		}
		while(regPtr);
	}
	else
	{
		modbusAnaReg *regPtr = _anaRegs;

		//if there is no register configured, bail
		if(regPtr == 0)
			return(0);

		//scan through the linked list until the end of the list or the register is found.
		//return the pointer.
		do
		{
			if(regPtr->address == addr)
				return(regPtr);
			regPtr = regPtr->next;
		}
		while(regPtr);
	}
	return(0);
}


