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

#ifndef ADK_h
#define ADK_h

#include <stdio.h>
#include <string.h>
#include <pins_arduino.h>
#include <WProgram.h>
#include <Wire.h>

// NOTE: these includes have to appear in the sketch too, otherwise the 
// arduino doesnt seem to setup the INCLUDE path right? arduino-fu needed.
#include <Max3421e.h>
#include <Max3421e_constants.h>
#include <Max_LCD.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <Servo.h>

#include <HardwareSerial.h>

#define ADK_OP_RESET            0x00
#define ADK_OP_DIGITAL_READ     0x01
#define ADK_OP_DIGITAL_WRITE    0x02
#define ADK_OP_DIGITAL_WATCH    0x03
#define ADK_OP_DIGITAL_UNWATCH  0x04
#define ADK_OP_ANALOG_READ      0x05
#define ADK_OP_ANALOG_WRITE     0x06
#define ADK_OP_ANALOG_WATCH     0x07
#define ADK_OP_ANALOG_UNWATCH   0x08
#define ADK_OP_ANALOG_REFERENCE 0x09
#define ADK_OP_PIN_MODE         0x0A
#define ADK_OP_SERIAL_ACTION    0x0B
#define ADK_OP_SERIAL_BEGIN     0x0C
#define ADK_OP_SERIAL_READ      0x0D
#define ADK_OP_SERIAL_WRITE     0x0E

#define ADK_OP_SERVO_OP         0x0F

#define ADK_OP_TWI_ACTION       0x1B
#define ADK_OP_TWI_BEGIN        0x1C
#define ADK_OP_TWI_READ         0x1D
#define ADK_OP_TWI_WRITE        0x1E
#define ADK_OP_REGISTER_OP      0x1F

#define ADK_MSG_SIZE_0 (0x00 << 5)
#define ADK_MSG_SIZE_1 (0x01 << 5)
#define ADK_MSG_SIZE_2 (0x02 << 5)
#define ADK_MSG_SIZE_4 (0x03 << 5)
#define ADK_MSG_SIZE_N (0x04 << 5)


class ADK {
private:
  AndroidAccessory *_droid;
  void (ADK::*_opCodeHandlers[32])(char, int);

  bool _loggingEnabled;
  HardwareSerial *_loggingOut;
  
  // Input Buffer for reading from the USB.
  char _input_buffer[32]; // TODO(arshan) : tune this number.
  int _current;
  int _max;

  // Handling for servo library
  Servo _servo_array[53]; 
  // I dont think the arduino can actually support this many but makes resource mgmt easier.


  //------
  // Consolidate communications calls we we can swap them out.
  //------
  int availableChar();
  char readChar(); // non-blocking, returns NULL if none available
  int fillInputBuffer();
  char blockingReadChar(); // will block if none available 
  void writeBytes(byte *buf, int size);
  // End communications calls

  void adkOpDigitalWrite(char pin, int value);
  void adkOpAnalogWrite(char pin, int value);
  void adkOpSerialWrite(char options, char *str, int size);
  void adkOpTwiWrite(char addr, char *str, int size);

  // OP_CODE handlers.
  void doAnalogOp(char opCode, int msgSize);
  void doDigitalOp(char opCode, int msgSize);
  void doRegisterOp(char opCode, int msgSize);
  void doSerialWrite(char opCode, int msgSize);
  void doTwiRead(char opCode, int msgSize);
  void doTwiWrite(char opCode, int msgSize);
  void do0ArgOp(char opCode, int msgSize);
  void doOpNotImplemented(char opCode, int msgSize);

  void doServoOp(char opCode, int msgSize);
  void doServoInit(int servo, int pin);
  void setServoPulse(int servo, unsigned int usec);
  void tearDownServo(int servo);

  bool loggingEnabled();
  void log(char *str);


public:
  ADK(AndroidAccessory &droid);

  void enableLogging(bool enable);
  void setLoggingOutput(HardwareSerial &sout);

  /**
   * Initialize the communications library.
   */
  void init();

  /**
   * Check the communications lines. If there is data available,
   * this method will block until the first command is processed.
   *
   * TODO(clayb): is this the final name?
   */
  void checkForInput();

  /**
   * Send a string to the host. Useful for debugging.
   */
  void serialWrite(char *str);


};

#endif

