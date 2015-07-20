/*
 Modbus RTU Server

 Circuit:
 * HC6 Bluetooth module shield attached

 created 30 june 2015
 by Riccardo Pretolesi

 */

// Bluetooth Communication
#include <SoftwareSerial.h>

#define  BT_RX 5            // PIN TO receive from bluetooth
#define  BT_TX 3            // PIN TO transmit to bluetooth

SoftwareSerial m_btSerial(BT_RX, BT_TX);
byte m_bytebtReadData[32] = {0};
unsigned int m_uibtReadDataLength = 0;
byte m_bytebtWriteData[32] = {0};
unsigned int m_uibtWriteDataLength = 0;
byte m_bytebtPDUWriteData[32] = {0};
unsigned int m_uibtPDUWriteDataLength = 0;

unsigned long m_ulbtRecDataTime = 0;
boolean m_bbtDataAvailable = false;
boolean m_bbtDataNotAvailable = false;
boolean m_bbtDataCompleted = false;
// Create union of shared memory space
union {
  short temp_short;
  byte temp_bytearray[2];
} m_u_CRC;

// Create union of shared memory space
union {
  short temp_short[20];
  long temp_long[10];
  float temp_float[10];
  byte temp_bytearray[40];
} m_union_share_mem;

void setup() {

  // Set as Output mode
  pinMode(3, OUTPUT);
  pinMode(5, INPUT);

  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  Serial.println("Begin Setup");
  // Initialize Bluetooth SoftwareSerial port for selected data speed
  m_btSerial.begin(9600);

  Serial.println("End Setup");

}

void loop() {

  // Bluetooth
  if (m_btSerial.available() > 0){
    // After 4 ms i suppose that all data are received
    m_bbtDataNotAvailable = false;
    if(m_bbtDataAvailable == false){
      m_bbtDataAvailable = true;
      m_uibtReadDataLength = 0;
      m_ulbtRecDataTime = micros();    
      Serial.println("Data available.");
    }
    // After 10 millis all data should be received
    if(m_ulbtRecDataTime + 100000 <= micros()){
      Serial.println("Bletooth data...");
      while(m_btSerial.available()){
        m_bytebtReadData[m_uibtReadDataLength] = m_btSerial.read();
        Serial.print(m_bytebtReadData[m_uibtReadDataLength]);  
        Serial.print(" ");  
        m_uibtReadDataLength = m_uibtReadDataLength + 1;
      }
      Serial.println("");
      m_bbtDataCompleted = true;
      m_u_CRC.temp_short = getCRC(m_bytebtReadData, m_uibtReadDataLength - 2);
      if((m_bytebtReadData[m_uibtReadDataLength - 2] == m_u_CRC.temp_bytearray[0]) && (m_bytebtReadData[m_uibtReadDataLength - 1] == m_u_CRC.temp_bytearray[1])){

        // Data completed successfully
        Serial.println("CRC Ok!");
        processModbusPDU(&m_bytebtReadData[1], m_uibtReadDataLength - 3, &m_bytebtPDUWriteData[0], &m_uibtPDUWriteDataLength);

        // Prepare answer
        m_bytebtWriteData[0] = m_bytebtReadData[0]; // Address
        m_uibtWriteDataLength = 1;
        
        memcpy(&m_bytebtWriteData[1], &m_bytebtPDUWriteData[0], m_uibtPDUWriteDataLength); // PDU
        m_uibtWriteDataLength = m_uibtWriteDataLength + m_uibtPDUWriteDataLength;
        
        m_u_CRC.temp_short = getCRC(m_bytebtWriteData, m_uibtWriteDataLength); // CRC
        m_bytebtWriteData[m_uibtWriteDataLength] = m_u_CRC.temp_bytearray[0]; // CRC
        m_uibtWriteDataLength = m_uibtWriteDataLength + 1;
        m_bytebtWriteData[m_uibtWriteDataLength] = m_u_CRC.temp_bytearray[1]; // CRC
        m_uibtWriteDataLength = m_uibtWriteDataLength + 1;
        
        Serial.println("Answer Start: ");
        for (int index_0 = 0; index_0 < m_uibtWriteDataLength; index_0++) {
          m_btSerial.write(m_bytebtWriteData[index_0]);
          Serial.print(m_bytebtWriteData[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");
        Serial.println("Answer End. ");        
      }
    }
  } else {
    m_bbtDataAvailable = false;
    if(m_bbtDataNotAvailable == false){
      m_bbtDataNotAvailable = true;
      m_uibtReadDataLength = 0;
      Serial.println("Data NOT available.");
      Serial.println("");
    }
  }
}

// Process MODBUS PDU
void processModbusPDU(byte byteInModbusPDU[], int iInlenght, byte byteOutModbusPDU[], unsigned int *iOutlenght){

  boolean bFunctionCodeOk = false;
  boolean bRegisterAndByteCountOk = false;
  boolean bAddressOk = false;
  byte byteModbusFunctionCode = byteInModbusPDU[0];
  Serial.print("Function Code: ");
  Serial.println(byteModbusFunctionCode);

  // Function
  if (byteModbusFunctionCode == 0x10 || byteModbusFunctionCode == 0x03) {
    bFunctionCodeOk = true;
  }

  if(bFunctionCodeOk == true) {

    // Address
    // Write Multiple Register
    if (byteModbusFunctionCode == 0x10) {
      // Check Data
      short shortQuantityOfRegisters = getShortFromBytes(&byteInModbusPDU[3]);
      byte byteByteCount = byteInModbusPDU[5];
      Serial.print("Quantity of Register: ");
      Serial.println(shortQuantityOfRegisters);
      Serial.print("Byte Count: ");
      Serial.println(byteByteCount);
  
      if (byteByteCount == (2 * shortQuantityOfRegisters)) {
        bRegisterAndByteCountOk = true;
      }
  
      if (bRegisterAndByteCountOk == true) {
        unsigned short ushortModbusAddress = getShortFromBytes(&byteInModbusPDU[1]);
        Serial.print("Address: ");
        Serial.println(ushortModbusAddress);
  
        if (ushortModbusAddress + shortQuantityOfRegisters < 20) {
          bAddressOk = true;
  
          // Copy data to union
          memcpy(&m_union_share_mem.temp_bytearray[ushortModbusAddress], &byteInModbusPDU[6], byteByteCount);
  
          // Print data read
          Serial.print("Short: ");
          union {
            short sh;
            byte shByteTemp[2];
          } ush;
          memcpy(ush.shByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 2);
          reverseByteArray(ush.shByteTemp, 0, 1);
          Serial.print(ush.sh);
  
          Serial.print(", Long: ");
          union {
            long l;
            byte lByteTemp[4];
          } ul;
          memcpy(ul.lByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
          reverseByteArray(ul.lByteTemp, 0, 3);
          Serial.print(ul.l);
   
          Serial.print(", Float: ");
          union {
            float f;
            byte fByteTemp[4];
          } uf;
          memcpy(uf.fByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
          reverseByteArray(uf.fByteTemp, 0, 3);
          Serial.print(uf.f);
  
          Serial.println(" ");
  
          // You can use here the values!!!!
 
         // Prepare answer
         byteOutModbusPDU[0] = byteInModbusPDU[0];
         byteOutModbusPDU[1] = byteInModbusPDU[1];
         byteOutModbusPDU[2] = byteInModbusPDU[2];
         byteOutModbusPDU[3] = byteInModbusPDU[3];
         byteOutModbusPDU[4] = byteInModbusPDU[4];
         *iOutlenght = 5;
         
        } else {
          // Address out of range
          // Prepare answer
          byteOutModbusPDU[0] = byteInModbusPDU[0] + 0x80;
          byteOutModbusPDU[1] = 0x02;
          *iOutlenght = 2;
          
        }
      }
    }
    // Read Multiple Register
    if (byteModbusFunctionCode == 0x03) {
      unsigned short ushortModbusAddress = getShortFromBytes(&byteInModbusPDU[1]);
      Serial.print("Address: ");
      Serial.println(ushortModbusAddress);
      short shortQuantityOfRegisters = getShortFromBytes(&byteInModbusPDU[3]);
      Serial.print("Quantity of Register: ");
      Serial.println(shortQuantityOfRegisters);
      byte byteByteCount = shortQuantityOfRegisters * 2;
      Serial.print("Byte Count: ");
      Serial.println(byteByteCount);

      if (ushortModbusAddress + shortQuantityOfRegisters < 20) {
        bAddressOk = true;

        // Prepare answer
        byteOutModbusPDU[0] = byteInModbusPDU[0]; // Function Code
        *iOutlenght = 1;
        byteOutModbusPDU[1] = byteByteCount; // Byte Count
        *iOutlenght = *iOutlenght + 1;
        
        // Writing a sh value... at address 10
        union {
          short sh;
          byte shByteTemp[2];
        } ush;
        ush.sh = -25; // this value is read at address 10....
        reverseByteArray(ush.shByteTemp, 0, 1);
        memcpy(&m_union_share_mem.temp_bytearray[10], ush.shByteTemp, sizeof(ush.shByteTemp));
        
  
        // Copy data to union
        memcpy(&byteOutModbusPDU[2], &m_union_share_mem.temp_bytearray[ushortModbusAddress], byteByteCount);
        *iOutlenght = *iOutlenght + byteByteCount;
      } else {
        // Address out of range
        // Prepare answer
        byteOutModbusPDU[0] = byteInModbusPDU[0] + 0x80;
        byteOutModbusPDU[1] = 0x02;
        *iOutlenght = 2;        
      }
    }    
  }  
}

// Short
void setShortToBytes(short shortVal, byte* bytearrayVal) {
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

short getShortFromBytes(byte* bytearrayVal) {
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;

  u.temp_bytearray[1] = bytearrayVal[0];
  u.temp_bytearray[0] = bytearrayVal[1];

  return u.temp_short;
}
/* Function to reverse arr[] from start to end*/
void reverseByteArray(byte Array[], int iStart, int iEnd)
{
  byte temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseShortArray(short Array[], int iStart, int iEnd)
{
  short temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseLongArray(long Array[], int iStart, int iEnd)
{
  long temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseFloatArray(float Array[], int iStart, int iEnd)
{
  float temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

// Modbus CRC
// Compute the MODBUS RTU CRC
unsigned short getCRC(byte buf[], int len)
{
  unsigned short crc = 0xFFFF;
 
  for (int pos = 0; pos < len; pos++) {
    crc ^= (unsigned short)buf[pos];          // XOR byte into least sig. byte of crc
 
    for (int i = 8; i != 0; i--) {    // Loop over each bit
      if ((crc & 0x0001) != 0) {      // If the LSB is set
        crc >>= 1;                    // Shift right and XOR 0xA001
        crc ^= 0xA001;
      }
      else                            // Else LSB is not set
        crc >>= 1;                    // Just shift right
    }
  }
  // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
  return crc;  
}

