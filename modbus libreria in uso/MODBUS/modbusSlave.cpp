#include <modbusSlave.h>
#include <modbus.h>
#include <modbusDevice.h>
#include <Arduino.h>

modbusSlave::modbusSlave()
{
	_softwareSerial = 0;
}
/* Pretolesi */
modbusSlave::modbusSlave(uint8_t receivePin, uint8_t transmitPin, bool inverse_logic /* = false */)
{
	_softwareSerial = new SoftwareSerial(receivePin, transmitPin, inverse_logic);
}
/*
Set the Serial Baud rate.
Reconfigure the UART for 8 data bits, no parity, and 1 stop bit.
and flush the serial port.
*/
void modbusSlave::setBaud(word baud)
{
	_baud = baud;
	//calculate the time perdiod for 3 characters for the given bps in ms.
	_frameDelay = 24000000/_baud;

	if(_softwareSerial == 0){
		Serial.begin(baud);
		Serial.flush();
	} else {
		_softwareSerial->begin(baud);
	}
}

void modbusSlave::setBaudAndInterCharTiming(word baud, word frameDelay)
{
	_baud = baud;
	//calculate the time perdiod for 3 characters for the given bps in ms.
	_frameDelay = frameDelay;

	if(_softwareSerial == 0){
		Serial.begin(baud);
		Serial.flush();
	} else {
		_softwareSerial->begin(baud);
	}
}

/*
Retrieve the serial baud rate
*/
word modbusSlave::getBaud(void)
{
	return(_baud);
}

/*
Generates the crc for the current message in the buffer.
*/

void modbusSlave::calcCrc(void)
{
	byte	CRCHi = 0xFF,
			CRCLo = 0x0FF,
			Index,
			msgLen,
			*msgPtr;

	msgLen = _len-2;
	msgPtr = _msg;

	while(msgLen--)
	{
		Index = CRCHi ^ *msgPtr++;
		CRCHi = CRCLo ^ _auchCRCHi[Index];
		CRCLo = _auchCRCLo[Index];
	}
	_crc = (CRCHi << 8) | CRCLo;
}

/*
  Checks the UART for query data
*/
void modbusSlave::checkSerial(void)
{
	//while there is more data in the UART than when last checked
	if(_softwareSerial == 0){
		while(Serial.available() > _len)
		{
			//update the incoming query message length
			_len = Serial.available();

			delayMicroseconds(_frameDelay);
		}
	} else {
		while(_softwareSerial->available() > _len)
		{
			//update the incoming query message length
			_len = _softwareSerial->available();

			delayMicroseconds(_frameDelay);
		}
	}
}

/*
Copies the contents of the UART to a buffer
*/
void modbusSlave::serialRx(void)
{
	byte i;

	//allocate memory for the incoming query message
	_msg = (byte*) malloc(_len);

		//copy the query byte for byte to the new buffer
		for (i=0 ; i < _len ; i++){
			if(_softwareSerial == 0){
				_msg[i] = Serial.read();
			} else {
				_msg[i] = _softwareSerial->read();
			}
		}
}

/*
Generates a query reply message for Digital In/Out status update queries.
*/
void modbusSlave::getDigitalStatus(byte funcType, word startreg, word numregs)
{
	//initialize the bit counter to 0
	byte bitn =0;
	
	//if the function is to read digital inputs then add 10001 to the start register
	//else add 1 to the start register
	if(funcType == READ_DI)
		startreg += 10001;
	else
		startreg += 1;

	//determine the message length
	//for each group of 8 registers the message length increases by 1
	_len = numregs/8;
	//if there is at least one incomplete byte's worth of data
	//then add 1 to the message length for the partial byte.
	if(numregs%8)
		_len++;
	//allow room for the Device ID byte, Function type byte, data byte count byte, and crc word
	_len +=5;

	//allocate memory of the appropriate size for the message
	_msg = (byte *) malloc(_len);

	//write the slave device ID
	_msg[0] = _device->getId();
	//write the function type
	_msg[1] = funcType;
	//set the data byte count
	_msg[2] = _len-5;

	//For the quantity of registers queried
	while(numregs--)
	{
		//if a value is found for the current register, set bit number bitn of msg[3]
		//else clear it
		if(_device->get(startreg))
			bitSet(_msg[3], bitn);
		else
			bitClear(_msg[3], bitn);
		//increment the bit index
		bitn++;
		//increment the register
		startreg++;
	}
	
	//generate the crc for the query reply and append it
	this->calcCrc();
	_msg[_len - 2] = _crc >> 8;
	_msg[_len - 1] = _crc & 0xFF;
}

void modbusSlave::getAnalogStatus(byte funcType, word startreg, word numregs)
{
	word val;
	word i = 0;

	//if the function is to read analog inputs then add 30001 to the start register
	//else add 40001 to the start register
	if(funcType == READ_AI)
		startreg += 30001;
	else
		startreg += 40001;

	//calculate the query reply message length
	//for each register queried add 2 bytes
	_len = numregs * 2;
	//allow room for the Device ID byte, Function type byte, data byte count byte, and crc word
	_len += 5;

	//allocate memory for the query response
	_msg = (byte *) malloc(_len);

	//write the device ID
	_msg[0] = _device->getId();
	//write the function type
	_msg[1] = funcType;
	//set the data byte count
	_msg[2] = _len - 5;

	//for each register queried
	while(numregs--)
	{
		//retrieve the value from the register bank for the current register
		val = _device->get(startreg+i);
        Serial.print("val: ");
        Serial.print(val);
        Serial.print(" startreg+i: ");
        Serial.println(startreg+i);
		//write the high byte of the register value
		_msg[3 + i * 2]  = val >> 8;
		//write the low byte of the register value
		_msg[4 + i * 2] = val & 0xFF;
		//increment the register
		i++;
	}

	//generate the crc for the query reply and append it
	this->calcCrc();
	_msg[_len - 2] = _crc >> 8;
	_msg[_len - 1] = _crc & 0xFF;
}

void modbusSlave::setStatus(byte funcType, word reg, word val)
{
	//Set the query response message length
	//Device ID byte, Function byte, Register byte, Value byte, CRC word
	_len = 8;
	//allocate memory for the message buffer.
	_msg = (byte *) malloc(_len);


	//write the device ID
	_msg[0] = _device->getId();
	//if the function type is a digital write
	if(funcType == WRITE_DO)
	{
		//Add 1 to the register value and set it's value to val
		_device->set(reg + 1, val);
		//write the function type to the response message
		_msg[1] = WRITE_DO;
	}
	else
	{
		//else add 40001 to the register and set it's value to val
		_device->set(reg + 40001, val);

		//write the function type of the response message
		_msg[1] = WRITE_AO;
	}
	
	//write the register number high byte value
	_msg[2] = reg >> 8;
	//write the register number low byte value
	_msg[3] = reg & 0xFF;
	//write the control value's high byte
	_msg[4] = val >> 8;
	//write the control value's low byte
	_msg[5] = val & 0xFF;

	//calculate the crc for the query reply and append it.
	this->calcCrc();
	_msg[_len - 2]= _crc >> 8;
	_msg[_len - 1]= _crc & 0xFF;
}

void modbusSlave::setAnalogStatus(byte funcType, word reg, word *fieldDataRegisters, short fieldQuantityOfRegisters)
{
	//Set the query response message length
	//Device ID byte, Function byte, Register byte, Value byte, CRC word
	_len = 8;
	//allocate memory for the message buffer.
	_msg = (byte *) malloc(_len);


	//write the device ID
	_msg[0] = _device->getId();

	Serial.print("fieldQuantityOfRegisters: ");
	Serial.println(fieldQuantityOfRegisters);

	// add 40001 to the register and set it's value to val
	for(short shIndex = 0; shIndex < fieldQuantityOfRegisters; shIndex++){
		_device->set(reg + 40001 + shIndex, fieldDataRegisters[shIndex]);
		 Serial.print("reg: ");
		 Serial.print(reg + 40001 + shIndex);
		 Serial.print("fieldDataRegisters: ");
		 Serial.println(fieldDataRegisters[shIndex]);
	}

	//write the function type of the response message
	_msg[1] = funcType;

	//write the register start number high byte value
	_msg[2] = reg >> 8;
	//write the register start number low byte value
	_msg[3] = reg & 0xFF;
	//write the Quantity Of Registers value's high byte
	_msg[4] = fieldQuantityOfRegisters >> 8;
	//write the Quantity Of Registers value's low byte
	_msg[5] = fieldQuantityOfRegisters & 0xFF;

	//calculate the crc for the query reply and append it.
	this->calcCrc();
	_msg[_len - 2]= _crc >> 8;
	_msg[_len - 1]= _crc & 0xFF;
}

void modbusSlave::run(void)
{
	byte deviceId;
	byte funcType;
	word field1;
	word field2;

	word *fieldDataRegisters;
	word fieldQuantityOfRegisters;

	int i;
	
	//initialize mesasge length
	_len = 0;

	//check for data in the recieve buffer
	this->checkSerial();

	//if there is nothing in the recieve buffer, bail.
	if(_len == 0)
	{
		return;
	}

	//retrieve the query message from the serial uart
	this->serialRx();
	
	//if the message id is not 255, and
	// and device id does not match bail
	if( (_msg[0] != 0xFF) && 
		(_msg[0] != _device->getId()) )
	{
		return;
	}
	//calculate the checksum of the query message minus the checksum it came with.
	this->calcCrc();

	//if the checksum does not match, ignore the message
	if ( _crc != ((_msg[_len - 2] << 8) + _msg[_len - 1]))
		return;

	//copy the function type from the incoming query
	funcType = _msg[1];

	//copy field 1 from the incoming query
	field1	= (_msg[2] << 8) | _msg[3];

	//generate query response based on function type
	switch(funcType)
	{
	case READ_DI:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->getDigitalStatus(funcType, field1, field2);
		break;
	case READ_DO:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->getDigitalStatus(funcType, field1, field2);
		break;
	case READ_AI:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->getAnalogStatus(funcType, field1, field2);
		break;
	case READ_AO:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->getAnalogStatus(funcType, field1, field2);
		break;
	case WRITE_DO:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->setStatus(funcType, field1, field2);
		break;
	case WRITE_AO:
		//copy field 2 from the incoming query
		field2  = (_msg[4] << 8) | _msg[5];

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->setStatus(funcType, field1, field2);
		break;
	case WRITE_MULTI_AO:
		// allocate memory for data to write
		fieldQuantityOfRegisters = (_msg[4] << 8) | _msg[5];
		fieldDataRegisters = (word*) malloc(fieldQuantityOfRegisters);
		for(short shIndex = 0; shIndex < fieldQuantityOfRegisters; shIndex++){
			fieldDataRegisters[shIndex] = (_msg[7 + (2 * shIndex)] << 8) | _msg[8 + (2 * shIndex)];
		}

		//free the allocated memory for the query message
		free(_msg);
		//reset the message length;
		_len = 0;

		this->setAnalogStatus(funcType, field1, fieldDataRegisters, fieldQuantityOfRegisters);

		// free memory for data to write
		free(fieldDataRegisters);
		break;
	default:
		return;
		break;
	}

	//if a reply was generated
	if(_len)
	{
		int i;
		//send the reply to the serial UART
		//Senguino doesn't support a bulk serial write command....
		for(i = 0 ; i < _len ; i++){
			if(_softwareSerial == 0){
				Serial.write(_msg[i]);
			} else {
				_softwareSerial->write(_msg[i]);
			}
		}
		//free the allocated memory for the reply message
		free(_msg);
		//reset the message length
		_len = 0;
	}
}
