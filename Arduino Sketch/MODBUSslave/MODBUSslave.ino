#include <modbus.h>
#include <modbusDevice.h>
#include <modbusRegBank.h>
#include <modbusSlave.h>
#include <SoftwareSerial.h>

/*
This example code shows a quick and dirty way to get an
arduino to talk to a modbus master device with a
device ID of 1 at 9600 baud.
*/

//Setup the brewtrollers register bank
//All of the data accumulated will be stored here
modbusDevice regBank;
//Create the modbus slave protocol handler
modbusSlave slave(7,6);

void setup()
{   
  Serial.begin(9600);

  //Assign the modbus device ID.  
  regBank.setId(1);

/*
modbus registers follow the following format
00001-09999  Digital Outputs, A master device can read and write to these registers
10001-19999  Digital Inputs, A master device can only read the values from these registers
30001-39999  Analog Inputs, A master device can only read the values from these registers
40001-49999  Analog Outputs, A master device can read and write to these registers 

Analog values are 16 bit unsigned words stored with a range of 0-32767
Digital values are stored as bytes, a zero value is OFF and any nonzer value is ON

It is best to configure registers of like type into contiguous blocks.  this
allows for more efficient register lookup and and reduces the number of messages
required by the master to retrieve the data
*/

//Add Digital Output registers 00001-00016 to the register bank
/*
  regBank.add(1);
  regBank.add(2);
  regBank.add(3);
  regBank.add(4);

//Add Digital Input registers 10001-10008 to the register bank
  regBank.add(10001);  
  regBank.add(10002);  
  regBank.add(10003);  
  regBank.add(10004);  

//Add Analog Input registers 30001-10010 to the register bank
  regBank.add(30001);  
  regBank.add(30002);  
  regBank.add(30003);  
  regBank.add(30004);  
*/
//Add Analog Output registers 40001-40020 to the register bank
  regBank.add(40001);  
  regBank.add(40002);  
  regBank.add(40003);  
  regBank.add(40004);  
  regBank.add(40005);  
/*
Assign the modbus device object to the protocol handler
This is where the protocol handler will look to read and write
register data.  Currently, a modbus slave protocol handler may
only have one device assigned to it.
*/
  slave._device = &regBank;  

// Initialize the serial port for coms at 9600 baud  
  slave.setBaudAndInterCharTiming(9600, 1500);    
}

void loop()
{
    //put a random number into registers 1, 10001, 30001 and 40001
//    regBank.set(1, (byte) random(0, 2));
//    regBank.set(10001, (byte) random(0, 2));
//    regBank.set(30001, (word) random(0, 32767));
//    regBank.set(40001, (word) random(0, 32767));
//      short sh1 = regBank.get(40001);
//      short sh2 = regBank.get(40002);
//      long l = regBank.getLong(40001);
//      Serial.print("sh1: ");
//      Serial.print(sh1);
//      Serial.print("-sh2: ");
//      Serial.print(sh2);
//      Serial.print("-long: ");
 //     Serial.println(l);

//     long l = -1234567890;
//     short sh = -3276;

//     regBank.setLong(40001, l);
     slave.run();  
}
