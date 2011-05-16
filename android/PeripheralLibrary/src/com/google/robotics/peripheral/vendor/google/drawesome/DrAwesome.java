// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.util.Log;

import com.google.robotics.peripheral.connector.ConnectionListener;
import com.google.robotics.peripheral.vendor.google.adk.AdkController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author arshan@google.com (Your Name Here)
 * 
 *         - StreamMinder , look for magic pattern in stream, trigger diagnostic
 */
public class DrAwesome extends AdkController implements Runnable {

  public static final String ACCESSORY_STRING = "com.google.android.DrAwesome";
  public static final String TAG = "DrAwesome";

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

  // public static final Pin PWM0 = new Pin();

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
    // must be something?
    new Thread(this).start();
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
  public synchronized boolean digitalRead(int pin) throws IOException {
    clearInputBuffers();
    mWriteBuffer[0] = (MSG_SIZE_1 | OP_DIGITAL_READ);
    mWriteBuffer[1] = (byte) (pin & 0xFF);
    getOutputStream().write(mWriteBuffer, 0, 2);

    // wait on return message ... with timeout?
    // nope, the return comes via the listener framework
    return true;
  }


  // this is a non blocking call that will result in the result
  // coming back over the proto. So your listener can deal with it.
  public synchronized void analogRead(int pin) {
  }

  /**
   * Servo Control
   */
  private static final int SERVO_INIT = 0x0;
  private static final int SERVO_SET = 0x40;
  private static final int SERVO_TEARDOWN = 0x80;

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
   * make sure there is no pending bytes in the input buffer
   */
  private void clearInputBuffers() {

  }

  public void digitalWatch(int pin, int period) throws IOException {
    // getOutputStream().write(new byte[]{(MSG_SIZE_N | OP_DIGITAL_WATCH), });
  }

  public void run() {

    int total = 0;
    byte[] buffer = new byte[16384];
    int current = 0;
    AwesomeMessage message = new AwesomeMessage();
    boolean running = true;
    
      try {
        while (running) {
        total = getInputStream().read(buffer);
        Log.d(TAG, "read bytes from the board : " + total);
        printem(buffer, 0, total);
        
        current = 0;
        //while (total < current) {
        //  current += message.populate(buffer, current, total - current);
        //  handleMessage(message);
       // }
        }
      } catch (IOException e) {
        e.printStackTrace();
        // anything we can do to recover here? 
      }
   

    disconnected();
  }


  public void printem(byte[] arr, int offset, int len){
    String str = "";
    for (int x = offset; x < offset+len; x++) {
      str += (char)arr[x];
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
        offset += 1;
      } else {
        mSize = (buffer[offset] & 0x60) >> 5;
        if (mSize == 3) {
          mSize = 4;
        }
      }

      if (mSize > size) {
        throw new RuntimeException("not enough bytes to fill payload, need " + mSize + " but got "
            + size);
      }

      System.arraycopy(buffer, offset, payload, 0, mSize);
      return mSize;
    }
  };

  public void handleMessage(AwesomeMessage message) {
    switch (message.mOpCode) {
      case OP_ANALOG_READ:
      case OP_DIGITAL_READ:
      case OP_SERVO_READ:
    }
  }
  
  protected void log(String msg) {
    Log.d(TAG,msg);
  }

}