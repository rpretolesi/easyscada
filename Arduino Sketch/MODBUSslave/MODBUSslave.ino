#include <modbus.h>
#include <modbusDevice.h>
#include <modbusRegBank.h>
#include <modbusSlave.h>
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

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

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);  

void setup()
{   
  pinMode(10, INPUT);
  Serial.begin(9600);

  //Assign the modbus device ID.  
  regBank.setId(1);

  lcd.begin(16, 2);

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
  regBank.add(40006);  
  regBank.add(40007);  
  regBank.add(40008);  
/*
Assign the modbus device object to the protocol handler
This is where the protocol handler will look to read and write
register data.  Currently, a modbus slave protocol handler may
only have one device assigned to it.
*/
  slave._device = &regBank;  

// Initialize the serial port for coms at 9600 baud  
  slave.setBaudAndInterCharTiming(9600, 500);    
}
long l1 = 0;
long l1_old = -1;
long l2 = 0;
long l2_old = -1;
long l3 = 0;
long l3_old = -1;

void loop()
{  
  if(regBank.getLong(40001, &l1) == 0){
    if(l1_old != l1){
      l1_old = l1;
//      lcd.clear();
      lcd.setCursor(0, 0);  
      lcd.print("       "); 
      lcd.setCursor(0, 0);  
      lcd.print(l1); 
    }
  }
  if(regBank.getLong(40003, &l2) == 0){
    if(l2_old != l2){
      l2_old = l2;
//      lcd.clear();
      lcd.setCursor(8, 0);  
      lcd.print("       "); 
      lcd.setCursor(8, 0);  
      lcd.print(l2); 
    }
  }
  if(regBank.getLong(40005, &l3) == 0){
    if(l3_old != l3){
      l3_old = l3;
//      lcd.clear();
      lcd.setCursor(0, 1);  
      lcd.print("       "); 
      lcd.setCursor(0, 1);  
      lcd.print(l3); 
    }
  }
  int bVal = digitalRead(10);
  lcd.setCursor(8, 1);  
  if(bVal == 0){
    if(regBank.setShort(40007, 1) == 0){
      lcd.print("1"); 
    }
  } else {
    if(regBank.setShort(40007, 4) == 0){
      lcd.print("4"); 
    }
  } 

  slave.run();  
}
