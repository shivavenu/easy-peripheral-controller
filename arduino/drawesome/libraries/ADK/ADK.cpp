/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

#include "ADK.h"


ADK::ADK(AndroidAccessory &droid) {
  this->_droid = &droid;
  this->_loggingOut = 0x0;
  
  // setup for the input buffer
  _current = 0;
  _max = 0;
  memset(_input_buffer, 0x0, sizeof(_input_buffer));

  // Setup OP_CODE handling.
  memset(_opCodeHandlers, 0x0, 32);
  _opCodeHandlers[ADK_OP_RESET]            = &ADK::do0ArgOp;
  _opCodeHandlers[ADK_OP_DIGITAL_READ]     = &ADK::doDigitalOp;
  _opCodeHandlers[ADK_OP_DIGITAL_WRITE]    = &ADK::doDigitalOp;
  _opCodeHandlers[ADK_OP_DIGITAL_WATCH]    = &ADK::doDigitalOp;
  _opCodeHandlers[ADK_OP_DIGITAL_UNWATCH]  = &ADK::doDigitalOp;
  _opCodeHandlers[ADK_OP_ANALOG_READ]      = &ADK::doAnalogOp;
  _opCodeHandlers[ADK_OP_ANALOG_WRITE]     = &ADK::doAnalogOp;
  _opCodeHandlers[ADK_OP_ANALOG_WATCH]     = &ADK::doAnalogOp;
  _opCodeHandlers[ADK_OP_ANALOG_UNWATCH]   = &ADK::doAnalogOp;
  _opCodeHandlers[ADK_OP_ANALOG_REFERENCE] = &ADK::doAnalogOp;
  _opCodeHandlers[ADK_OP_PIN_MODE]         = &ADK::doDigitalOp;
  _opCodeHandlers[ADK_OP_SERIAL_ACTION]    = &ADK::doOpNotImplemented;
  _opCodeHandlers[ADK_OP_SERIAL_BEGIN]     = &ADK::doOpNotImplemented;
  _opCodeHandlers[ADK_OP_SERIAL_READ]      = &ADK::doOpNotImplemented;
  _opCodeHandlers[ADK_OP_SERIAL_WRITE]     = &ADK::doSerialWrite;
  _opCodeHandlers[ADK_OP_TWI_ACTION]       = &ADK::doOpNotImplemented;
  _opCodeHandlers[ADK_OP_TWI_BEGIN]        = &ADK::do0ArgOp;
  _opCodeHandlers[ADK_OP_TWI_READ]         = &ADK::doTwiRead;
  _opCodeHandlers[ADK_OP_TWI_WRITE]        = &ADK::doTwiWrite;
  _opCodeHandlers[ADK_OP_REGISTER_OP]      = &ADK::doRegisterOp;
  _opCodeHandlers[ADK_OP_SERVO_OP]         = &ADK::doServoOp;

}


/**
 * Initialize the communications library.
 */
void ADK::init() {
  // Send a reset sequence to note that we're alive.
  writeBytes((byte[]) {ADK_MSG_SIZE_0 | ADK_OP_RESET}, 1);
}

void ADK::enableLogging(bool enable) {
#ifdef DEBUG
  this->_loggingEnabled = enable;
#else
  this->_loggingEnabled = false;
#endif
}


void ADK::setLoggingOutput(HardwareSerial &sout) {
#ifdef DEBUG
  this->_loggingOut = &sout;
#endif
}

/**
 * Check the communications lines. If there is data available,
 * this method will block until the first command is processed.
 *
 * TODO(clayb): is this the final name?
 */
void ADK::checkForInput() {

  if (availableChar() == 0) {
   fillInputBuffer();
  }

  char header = readChar(); 

  // If no char available will return null.
  if (header == NULL) {   
    return;
  }
  
  bool varMsgSize = header & 0x80;
  int msgSize = (header & 0x60) >> 5;
  char opCode = header & 0x1f;
  if (varMsgSize) {
    // TODO: recover from a bad stream here. 
    msgSize = readChar();
  }


  // Choose a continue function based on OP_CODE.
  void (ADK::*opCodeFunc)(char, int) = _opCodeHandlers[opCode];
  if (opCodeFunc != 0x0) {
    (this->*opCodeFunc)(opCode, msgSize);
  } else {
    this->doOpNotImplemented(opCode, msgSize);
  }
}

void ADK::serialWrite(char *str) {
  this->adkOpSerialWrite(0x0, str, strlen(str));
}

bool ADK::loggingEnabled() {
  return this->_loggingEnabled;
}

void ADK::log(char *str) {
  if (_loggingOut) {
    _loggingOut->write(str);
  } else {
    this->adkOpSerialWrite(0x0, str, strlen(str));
  }
}


//-------------------------------------------------------------------
// Consolidate communications calls we we can swap them out.
//-------------------------------------------------------------------


char ADK::readChar() {
  if (_current >= _max) {
    return NULL;
  }
  else {
    return _input_buffer[_current++];
  }
}


int ADK::fillInputBuffer() {
  _max = _droid->read(_input_buffer, 
		      sizeof(_input_buffer), 
		      1);
  _current = 0;

#ifdef DEBUG
  if (_max > 0) {
    char buf[80];
    sprintf(buf, "filled the input buffer: %d\n", _max);
    Serial.print(buf);
  }
#endif 

  if (_max < 0) {
    // this means there was an error in reading ... reset something?
    _max = 0;
  }
  return _max;
}

char ADK::blockingReadChar() {
  // This read is blocking.  
  while (availableChar() == 0) 
    fillInputBuffer();
  
  // return Serial.read();
  return readChar();
}

void ADK::writeBytes(byte *buf, int size) {
 // for (int i = 0; i < size; i++) {
 //   Serial.write(buf[i]);
 // }

  _droid->write(buf, size);
}

int ADK::availableChar() {
  // return Serial.available();
  return _max - _current;
}


//-------------------------------------------------------------------
// Helper methods for sending out data.
//-------------------------------------------------------------------
void ADK::adkOpDigitalWrite(char pin, int value) {
  writeBytes((byte[]) {
      ADK_MSG_SIZE_1 | ADK_OP_DIGITAL_WRITE,
      (value ? 0x80 : 0x00) | (pin & 0x7F)}, 2);
}

void ADK::adkOpAnalogWrite(char pin, int value) {
  
    char buf[80];
    sprintf(buf, "sending back analog value: %d %u\n", pin, value);
    Serial.print(buf);

  writeBytes((byte[]) {
      ADK_MSG_SIZE_2 | ADK_OP_ANALOG_WRITE,
      ((value & 0x0300) >> 2) | (pin & 0x3F),
      value & 0xFF}, 3);
}

void ADK::adkOpSerialWrite(char options, char *str, int size) {
  writeBytes((byte[]) {
      ADK_MSG_SIZE_N | ADK_OP_SERIAL_WRITE,
      (size + 1),
      options}, 3);
  writeBytes((byte*) str, size);
}

void ADK::adkOpTwiWrite(char addr, char *str, int size) {
  writeBytes((byte[]) {
      ADK_MSG_SIZE_N | ADK_OP_TWI_WRITE,
      (size + 1),
      addr}, 3);
  writeBytes((byte*) str, size);
}


//-------------------------------------------------------------------
// Incoming operation handlers.
//-------------------------------------------------------------------
void ADK::doOpNotImplemented(char opCode, int msgSize) {
  char buf[80];
  sprintf(buf, "OP_CODE[0x%X] not implemented.\n", opCode);
  log(buf);
}

void ADK::do0ArgOp(char opCode, int msgSize) {
  if (opCode == ADK_OP_TWI_BEGIN) {
    Wire.begin();
  } else {
    this->doOpNotImplemented(opCode, msgSize);
  }
}

void ADK::doDigitalOp(char opCode, int msgSize) {
  char c = blockingReadChar();
  char pin = c & 0x7F;
  char bitVal = c >> 7;
 
#ifdef DEBUG
  char buf[80];
  sprintf(buf, "pin : %d val : %d\n", pin , bitVal);
  log(buf);
#endif

  if (opCode == ADK_OP_PIN_MODE) {
    // log("changing pin mode\n");
    pinMode(pin, bitVal);
  } else if (opCode == ADK_OP_DIGITAL_READ) {
    // log("reading ping value\n");
    adkOpDigitalWrite(pin, digitalRead(pin));
  } else if (opCode == ADK_OP_DIGITAL_WRITE) {
    // log("writing value to pin\n");
    digitalWrite(pin, bitVal);
  } else {
    this->doOpNotImplemented(opCode, msgSize);
  }
}

void ADK::doAnalogOp(char opCode, int msgSize) {
  int pin = blockingReadChar() & 0x7F;
  if (opCode == ADK_OP_ANALOG_WRITE) {
    analogWrite(pin, blockingReadChar());
  } else if (opCode == ADK_OP_ANALOG_READ) {
    adkOpAnalogWrite(pin, analogRead(pin));
  } else {
    this->doOpNotImplemented(opCode, msgSize);
  }
}

void ADK::doRegisterOp(char opCode, int msgSize) {
  // Unpack the command.
  char d0 = blockingReadChar();
  char operation = (d0 & 0xF0) >> 4;
  char portNum = d0 & 0x0F;
  char regNum = (operation & 0x8) >> 3;
  if (operation == 0xF) {
    regNum = 0x2;
    operation = 0x00;
  } else {
    operation &= 0x7;
  }

  // Select the requested register.
  volatile uint8_t * reg = (regNum == 0)
      ? portModeRegister(portNum)
      : (regNum == 1)
      ? portOutputRegister(portNum)
      : portInputRegister(portNum);

  // Perform the requested operation.
  char mask = blockingReadChar();
  if (operation == 0x00) { // R1-READ
    // TODO(arshan): implement.
  } else if (operation == 0x01) { // R1-SET
    *reg  = mask;
  } else if (operation == 0x02) { // R1-AND
    *reg &= mask;
  } else if (operation == 0x03) { // R1-OR
    *reg |= mask;
  } else if (operation == 0x04) { // R1-XOR
    *reg ^= mask;
  }
}

void ADK::doServoOp(char opCode, int msgSize) {
  char servoOp = readChar();
  char servoNum = servoOp & 0x3F;
  servoOp >>= 6;
  char pin = readChar();
  byte high = readChar();
  byte low = readChar();
  unsigned int usec = high << 8 | low;

  switch (servoOp) {
  case 0x00: // setup servo
    doServoInit(servoNum, pin);
  case 0x01: // set servo pulse
    setServoPulse(servoNum, usec);
    break;
  case 0x02: // tear down servo
    tearDownServo(servoNum);
    break;
  default:
    break;
    // no op, error message method is called for though
  }

}

void ADK::doSerialWrite(char opCode, int msgSize) {
  // TODO(clayb): actually look at the options.
  char options = blockingReadChar();
  for (int i = 0; i < msgSize - 1; i++) {
    Serial.write(blockingReadChar());
  }
}

void ADK::doTwiRead(char opCode, int msgSize) {
  char addr = blockingReadChar();
  char responseSize = blockingReadChar();

  // TODO(clayb): use global buffer!
  char buf[responseSize + 1];
  Wire.requestFrom(addr, responseSize);
  int i = 0;
  while(Wire.available() && i < responseSize) {
    buf[i++] = Wire.receive();
  }
  if (i < responseSize && loggingEnabled()) {
    log("ERROR: expected I2C data was unavailable.\n");
    // TODO(arshan) : write back error conditions in the proto
  }
  buf[i] = 0x0;
  adkOpTwiWrite(addr, buf, i);
}

void ADK::doTwiWrite(char opCode, int msgSize) {
  char addr = blockingReadChar();
  Wire.beginTransmission(addr);
  for (int i = 0; i < msgSize - 1; i++) {
    Wire.send(blockingReadChar());
  }
  Wire.endTransmission();
}

// initialize a servo in the array
void ADK::doServoInit(int servo, int pin) {
  _servo_array[servo].attach(pin);
}

// set a servo pulse width in the array
void ADK::setServoPulse(int servo, unsigned int usec) {
  _servo_array[servo].writeMicroseconds(usec);
}

void ADK::tearDownServo(int servo) {
  _servo_array[servo].detach();
}
