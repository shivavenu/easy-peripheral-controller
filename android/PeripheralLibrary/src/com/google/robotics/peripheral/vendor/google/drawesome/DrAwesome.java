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

package com.google.robotics.peripheral.vendor.google.drawesome;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.util.Log;

import com.google.robotics.peripheral.channel.TwiChannel;
import com.google.robotics.peripheral.connector.ConnectionListener;
import com.google.robotics.peripheral.device.AnalogInput;
import com.google.robotics.peripheral.device.DigitalInput;
import com.google.robotics.peripheral.device.DigitalOutput;
import com.google.robotics.peripheral.device.PwmOutput;
import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.util.AbstractResource;
import com.google.robotics.peripheral.util.Bus;
import com.google.robotics.peripheral.util.Pin;
import com.google.robotics.peripheral.util.Pin.Capability;
import com.google.robotics.peripheral.vendor.google.adk.AdkController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author arshan@google.com (Your Name Here)
 * 
 *      TODO   - StreamMinder , look for magic pattern in stream, trigger diagnostic
 */
public class DrAwesome extends AdkController implements Runnable {

  public static final String ACCESSORY_STRING = "com.google.android.DrAwesome";
  public static final String TAG = "DrAwesome";

  /*
   * Pin Definitions 
   */
  // Unroll these? ie. DIGITAL_18, DIGITAL_19 ...
  public static final AwesomePin[] DIGITAL;
  public static final AwesomePin[] ANALOG;
  
  // These actually overlap with the digital pins, but seemed useful for clarity.
  public static final AwesomePin RX;
  public static final AwesomePin TX;
  public static final AwesomePin RX0;
  public static final AwesomePin TX0;
  public static final AwesomePin RX1;
  public static final AwesomePin TX1;
  public static final AwesomePin RX2;
  public static final AwesomePin TX2;
  public static final AwesomePin RX3;
  public static final AwesomePin TX3;

  public static final AwesomePin SDA;
  public static final AwesomePin SCL;
  
  private static final Bus TWI_BUS = new Bus("TWI Bus",Pin.Capability.TWI);

  /*
   * Setup the available pins.
   * TODO(arshan): consider breaking these out into implementations for Arduino vs. Mega boards
   *               when they are available.
   */
   static {    
    
    DIGITAL = new AwesomePin[54];
    ANALOG = new AwesomePin[16];
                            
    DIGITAL[0]= new AwesomePin(0, "Digital/Rx", 
      Pin.Capability.DIGITAL_INPUT, Pin.Capability.DIGITAL_OUTPUT);
    DIGITAL[1] = new AwesomePin(1, "Digital/Tx", 
      Pin.Capability.DIGITAL_INPUT, Pin.Capability.DIGITAL_OUTPUT);

    // PWM pins
    for (int x = 2 ; x < 47; x++) {
      DIGITAL[x]= new AwesomePin(x, "Digital/Pwm " + x, Pin.Capability.DIGITAL_INPUT, 
        Pin.Capability.DIGITAL_OUTPUT, Pin.Capability.PWM_OUTPUT, Pin.Capability.SERVO_DRIVER);
    }
    
    // Not sure why but these higher pins dont work with servo code
    for (int x = 47 ; x < DIGITAL.length; x++) {
      DIGITAL[x]= new AwesomePin(x, "Digital/Pwm " + x, Pin.Capability.DIGITAL_INPUT, 
        Pin.Capability.DIGITAL_OUTPUT, Pin.Capability.PWM_OUTPUT);
    }
    
    // Analog pins
    for (int x = 0; x < ANALOG.length; x++) {
      ANALOG[x] = new AwesomePin(x, "Analog " + x, Pin.Capability.ANALOG_INPUT);
    }
    
    // The rest of them.
    // TODO(arshan): call out the ones that support rx/tx and twi.
    RX = RX0 = DIGITAL[0];
    TX = TX0 = DIGITAL[1];
    RX1 = DIGITAL[19];
    TX1 = DIGITAL[18];
    RX2 = DIGITAL[17];
    TX2 = DIGITAL[16];
    RX3 = DIGITAL[15];
    TX3 = DIGITAL[14];
    SDA = DIGITAL[20];
    SCL = DIGITAL[21];
  }
  
  /*
   * Op Codes for the protocol
   */
  private static final char MSG_SIZE_0 = 0x00 << 5;
  private static final char MSG_SIZE_1 = 0x01 << 5;
  private static final char MSG_SIZE_2 = 0x02 << 5;
  private static final char MSG_SIZE_4 = 0x03 << 5;
  private static final char MSG_SIZE_N = 0x04 << 5;

  private static final char OP_RESET = 0x00;
  private static final char OP_DIGITAL_READ = 0x01;
  private static final char OP_DIGITAL_WRITE = 0x02; // consider moving this
                                                     // into extended op space.
  private static final char OP_DIGITAL_WATCH = 0x03;
  private static final char OP_ANALOG_READ = 0x05;
  private static final char OP_ANALOG_WRITE = 0x06;
  private static final char OP_ANALOG_WATCH = 0x07;

  // incoming ops
  private static final char OP_ANALOG_VALUE = 0x05;
  private static final char OP_SERVO_READ = 0x0F;

  private static final char OP_PIN_MODE = 0x0A;
  private static final char OP_SERIAL_ACTION = 0x0B;
  private static final char OP_SERIAL_BEGIN = 0x0C;
  private static final char OP_SERIAL_READ = 0x0D;
  private static final char OP_SERIAL_WRITE = 0x0E;

  private static final char OP_SERVO_OP = 0x0F;

  private static final char OP_TWI_ACTION = 0x1B;
  private static final char OP_TWI_BEGIN = 0x1C;
  private static final char OP_TWI_READ = 0x1D;
  private static final char OP_TWI_WRITE = 0x1E;
  private static final char OP_REGISTER_OP = 0x1F;

  public static final int INPUT = 0x00;
  public static final int OUTPUT = 0x01;
  public static final boolean LOW = false;
  public static final boolean HIGH = true;

  public static final int REG_OP_READ = 0x00;
  public static final int REG_OP_SET = 0x01;
  public static final int REG_OP_AND = 0x02;
  public static final int REG_OP_OR = 0x03;
  public static final int REG_OP_XOR = 0x04;
  public static final int REG_DDR = 0x00;
  public static final int REG_PORT = 0x01;
  public static final int REG_PIN = 0x03;

  private byte[] mWriteBuffer = new byte[16];  
  
  /**
   * @param ctxt
   */
  public DrAwesome(InputStream in, OutputStream out) {
    super(in, out);
  }

  public DrAwesome(Context context, ConnectionListener listener) {
    super(context, listener);
  }

  @Override
  public void connected(UsbAccessory accessory) {
    super.connected(accessory);
    init();
  }

  protected void init() {    
    // Start the input consumer thread. 
    new Thread(this).start();
  }

  public AnalogInput getAnalogInput(Pin pin) {
    verifyPin(pin, Pin.Capability.ANALOG_INPUT);    
    return new AwesomeAnalogInput(this, pin);
  }
  
  public DigitalInput getDigitalInput(Pin pin) {
    verifyPin(pin, Pin.Capability.DIGITAL_INPUT);
    return new AwesomeDigitalInput(this, pin);
  }

  public DigitalOutput getDigitalOutput(Pin pin) {
    verifyPin(pin, Pin.Capability.DIGITAL_OUTPUT);
    return new AwesomeDigitalOutput(this, pin);
  }
  
  public PwmOutput getPwmOutput(Pin pin) {
    verifyPin(pin, Pin.Capability.PWM_OUTPUT);
    return new AwesomeAnalogOutput(this, pin);
  }

  public Servo getServo(Pin pin) {
    verifyPin(pin, Pin.Capability.SERVO_DRIVER);
    return new AwesomeServo(this, pin);
  }
  
  /*
   *
   * needs support for enumerated channels downstream.
   */
  public TwiChannel getTwiChannel() throws IOException {
        beginTwi();
        return new AwesomeTwiChannel(this, TWI_BUS);
  }

  // TODO(arshan): consider finding reporting all the problems, not just the first.
  private void verifyPin(Pin pin, Capability c) {
    if (! (pin instanceof AwesomePin)) {
      throw new IllegalArgumentException("You can only use pins that are defined in DrAwesome");
    }
    
    if (pin.isReserved()) {
      throw new IllegalArgumentException("Pin" + pin + " is already in use");
    }
    
    if (! pin.supports(c)) {
      throw new IllegalArgumentException("Pin " + pin + " does not support " + c);
    }
     

  }
  
  public synchronized void pinMode(int pin, int mode) throws IOException {
    // Consider the util function for sendMessage();
    mWriteBuffer[0] = (MSG_SIZE_1 | OP_PIN_MODE);
    mWriteBuffer[1] = (byte) (((mode << 7) | (pin & 0x7F)) & 0xFF);
    getOutputStream().write(mWriteBuffer, 0, 2);
  }

  public synchronized void digitalWrite(int pin, boolean value) throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_1 | OP_DIGITAL_WRITE);
    mWriteBuffer[1] = (byte) (((value) ? 0x80 : 0x00) | ((char) pin & 0x7F));
    getOutputStream().write(mWriteBuffer, 0, 2);
  }

  /**
   * The more regularly used mechanism for reading from digital pins, is that
   * the board returns the value on pin change, or a periodic read that is timed
   * by the board.
   */
  public synchronized void digitalRead(int pin) throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_1 | OP_DIGITAL_READ);
    mWriteBuffer[1] = (byte) (pin & 0xFF);
    getOutputStream().write(mWriteBuffer, 0, 2);
  }


  // this is a non blocking call that will result in the result
  // coming back over the proto. So your listener can deal with it.
  public synchronized void analogRead(int pin) throws IOException {
   mWriteBuffer[0] = (MSG_SIZE_1 | OP_ANALOG_READ);
   mWriteBuffer[1] = (byte)(pin & 0xFF);
   getOutputStream().write(mWriteBuffer, 0, 2);    
  }

  public synchronized void analogWrite(int pin, int value) throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_2 | OP_ANALOG_WRITE);
    mWriteBuffer[1] = (byte)((value << 4 | pin) & 0xFF);
    mWriteBuffer[2] = (byte)((value >> 4) & 0xFF);
    getOutputStream().write(mWriteBuffer,0,3);
  }
  
  /**
   * Servo Control
   */
  private static final int SERVO_INIT       = 0x00;
  private static final int SERVO_SET        = 0x40;
  private static final int SERVO_TEARDOWN   = 0x80;

  public synchronized void setupServo(int servoN, int pin, int initUSec) throws IOException {
    log("setup servo " + servoN + " on pin " + pin);
    mWriteBuffer[0] = (MSG_SIZE_4 | OP_SERVO_OP);
    mWriteBuffer[1] = (byte) (SERVO_INIT | servoN);
    mWriteBuffer[2] = (byte) (pin & 0xFF);
    mWriteBuffer[3] = (byte) ((initUSec >> 8)&0xFF);
    mWriteBuffer[4] = (byte) (initUSec & 0xFF);
    getOutputStream().write(mWriteBuffer, 0, 5);
  }

  public synchronized void setServoPulse(int servoN, int uSec) throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_4 | OP_SERVO_OP);
    mWriteBuffer[1] = (byte) (SERVO_SET | servoN);
    mWriteBuffer[2] = 0;
    mWriteBuffer[3] = (byte) (uSec >> 8);
    mWriteBuffer[4] = (byte) (uSec & 0xFF);
    Log.d(TAG, "meant to do : " + uSec + " on " + servoN);
    printem(mWriteBuffer, 0, 5, false);
    getOutputStream().write(mWriteBuffer, 0, 5);
  }

  public synchronized void tearDownServo(int servoN) throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_4 | OP_SERVO_OP);
    mWriteBuffer[1] = (byte) (SERVO_TEARDOWN | servoN);
    mWriteBuffer[2] = 0;
    mWriteBuffer[3] = 0; // consider allowing a final set on the tear down.
    mWriteBuffer[4] = 0;
    getOutputStream().write(mWriteBuffer, 0, 5);
  }
  

  /*
   * TWI methods.
   */
  public synchronized void beginTwi() throws IOException {
    mWriteBuffer[0] = (MSG_SIZE_0 | OP_TWI_BEGIN);
    getOutputStream().write(mWriteBuffer, 0, 1);
  }
  
  public synchronized void writeTwi(int addr, byte[] arr, int offset, int length) throws IOException {
    mWriteBuffer[0] = (byte)(MSG_SIZE_N | OP_TWI_WRITE);
    mWriteBuffer[1] = (byte)(length & 0xFF);
    mWriteBuffer[2] = (byte)(addr & 0xFF);
    System.arraycopy(arr, offset, mWriteBuffer, 3, length);
    getOutputStream().write(mWriteBuffer, 0, length+3);
  }
  
  public synchronized void readTwi(int addr, int length) throws IOException {
    mWriteBuffer[0] = (byte)(MSG_SIZE_2 | OP_TWI_READ);
    mWriteBuffer[1] = (byte) (addr & 0xFF);
    mWriteBuffer[2] = (byte) (length & 0xFF);
    getOutputStream().write(mWriteBuffer, 0, 3);
  }
  
  /**
   * Run method handles all the incoming messages from the board.
   */
  public void run() {

    int total = 0;
    byte[] buffer = new byte[16384];
    int current = 0;
    
    // This doesnt have legs, should use Message, and use the pooling from android.
    AwesomeMessage message = new AwesomeMessage();
    boolean running = true;
    
      try {
        while (running) {
        total = getInputStream().read(buffer);
      
        if (total > 5) { // HACK! take this out.
          // cheap hack to let syslog messages through
          printem(buffer, 0, total, true);
        }
        else {
          current = 0;
          while (current < total) {
            current += message.populate(buffer, current, total - current);
            handleMessage(message);
          }
        }
        }
      } catch (IOException e) {
        e.printStackTrace();
        // anything we can do to recover here? 
      }

    disconnected();
    releaseAllPins();
  }

  public void printem(byte[] arr, int offset, int len, boolean string){
    String str = "";
    for (int x = offset; x < offset+len; x++) {
      if (string) {
      str += (char)arr[x];
      }
      else {
        str += "0x" + Integer.toHexString(arr[x]) + ".";
      }
    }
    Log.d(TAG, str);    
  }  
  
  public class AwesomeMessage {
    public int mOpCode = 0;
    public int mSize = 0;
    public boolean mVariableSize = false;
    public byte[] payload = new byte[32]; // hard coded for now, consider making
                                          // it variable

    public int populate(byte[] buffer, int offset, int size) {
      mVariableSize = (buffer[offset] & 0x80) > 0;

      if (mVariableSize) {
        mSize = buffer[offset + 1];
        mOpCode = buffer[offset] & 0x7F;
        offset += 2;
      } else {
        mSize = (buffer[offset] & 0x60) >> 5;
        mOpCode = buffer[offset] & 0x1F;
        if (mSize == 3) {
          mSize = 4;
        }
        offset += 1;
      }

      if (mSize > size) {
        throw new RuntimeException("not enough bytes to fill payload, need " 
          + mSize + " but got " + size);
      }
      
      System.arraycopy(buffer, offset, payload, 0, mSize);
      
      return mSize+1; // +1 to add the opCode byte back to the number consumed.
    }
  };

  public void handleMessage(final AwesomeMessage message) {
    switch (message.mOpCode) {
     
      case OP_ANALOG_WRITE:        
        int pin = message.payload[0] & 0x3F;
        int value = (message.payload[1] & 0xFF) | ((message.payload[0] & 0xC0) << 2);
    
        for (AbstractResource res : ANALOG[pin].getResourcesAttached() ) {
            if (res instanceof AwesomeAnalogInput) {
              ((AwesomeAnalogInput)res).setValue(value);
            }
        }
        break;
      
      case OP_DIGITAL_WRITE:
        int dpin = message.payload[0] & 0x7f;
        boolean val = (message.payload[0] & 0x80) > 0; 
        
        for (AbstractResource res : DIGITAL[dpin].getResourcesAttached() ) {
          if (res instanceof AwesomeDigitalInput) {
            ((AwesomeDigitalInput)res).setValue(val);
          }
        } 
        break;
        
      case OP_TWI_WRITE:
        TWI_BUS.incoming(message.payload[0], message.payload, 1, message.mSize-1);
        break;
      case OP_SERVO_READ:
        break;
    }
  }
  
  private void releaseAllPins() {
    for (int x = 0; x < DIGITAL.length; x++) {
      DIGITAL[x].releaseAll();
    }
    for (int x = 0; x < ANALOG.length; x++) {
      ANALOG[x].releaseAll();
    }
    
    TWI_BUS.releaseAll();
  }
  
  protected void log(String msg) {
    Log.d(TAG,msg);
  }
 
}