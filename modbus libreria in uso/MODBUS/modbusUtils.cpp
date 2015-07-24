#include <Arduino.h>
#include <modbusUtils.h>

modbusUtils::modbusUtils()
{

}

// Short 2 bytes
void modbusUtils::setShortToBytes(short shortVal, byte* bytearrayVal) {
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;
  // Overite bytes of union with float variable
  u.temp_short = shortVal;
  // Assign bytes to input array
  bytearrayVal[0] = u.temp_bytearray[1];
  bytearrayVal[1] = u.temp_bytearray[0];
}

short modbusUtils::getShortFromBytes(byte* bytearrayVal) {
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;

  u.temp_bytearray[1] = bytearrayVal[0];
  u.temp_bytearray[0] = bytearrayVal[1];

  return u.temp_short;
}

// Long 4 byte
void modbusUtils::setLongToBytes(long longVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    long temp_long;
    byte temp_bytearray[4];
  } u;
  // Overite bytes of union with int variable
  u.temp_long = longVal;

  bytearrayVal[0] = u.temp_bytearray[3];
  bytearrayVal[1] = u.temp_bytearray[2];
  bytearrayVal[2] = u.temp_bytearray[1];
  bytearrayVal[3] = u.temp_bytearray[0];
}

long modbusUtils::getLongFromBytes(byte* bytearrayVal){
  // Create union of shared memory space
  union {
    long temp_long;
    byte temp_bytearray[4];
  } u;

  u.temp_bytearray[3] = bytearrayVal[0];
  u.temp_bytearray[2] = bytearrayVal[1];
  u.temp_bytearray[1] = bytearrayVal[2];
  u.temp_bytearray[0] = bytearrayVal[3];

  return u.temp_long;
}

// Float 4 byte
void modbusUtils::setFloatToBytes(float floatVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    float temp_float;
    byte temp_bytearray[4];
  } u;
  // Overite bytes of union with float variable
  u.temp_float = floatVal;

  bytearrayVal[0] = u.temp_bytearray[3];
  bytearrayVal[1] = u.temp_bytearray[2];
  bytearrayVal[2] = u.temp_bytearray[1];
  bytearrayVal[3] = u.temp_bytearray[0];
}

float modbusUtils::getFloatFromBytes(byte* bytearrayVal){
  // Create union of shared memory space
  union {
    float temp_float;
    byte temp_bytearray[4];
  } u;

  u.temp_bytearray[3] = bytearrayVal[0];
  u.temp_bytearray[2] = bytearrayVal[1];
  u.temp_bytearray[1] = bytearrayVal[2];
  u.temp_bytearray[0] = bytearrayVal[3];

  return u.temp_float;
}
