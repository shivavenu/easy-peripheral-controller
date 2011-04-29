// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.google.robotics.peripheral.vendor.google.adk.AdkController;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class DrAwesome extends AdkController {

  public static final String ACCESSORY_STRING = "com.google.android.DrAwesome";
  public static final String TAG = "ADK";
  
  private static final char MSG_SIZE_0 = 0x00 << 5;
  private static final char MSG_SIZE_1 = 0x01 << 5;
  private static final char MSG_SIZE_2 = 0x02 << 5;
  private static final char MSG_SIZE_4 = 0x03 << 5;
  private static final char MSG_SIZE_N = 0x04 << 5;

  private static final char OP_RESET         = 0x00;
  private static final char OP_DIGITAL_READ  = 0x01;
  private static final char OP_DIGITAL_WRITE = 0x02;
  private static final char OP_DIGITAL_WATCH = 0x03;
  private static final char OP_ANALOG_READ   = 0x05;
  private static final char OP_ANALOG_WRITE  = 0x06;
  private static final char OP_ANALOG_WATCH  = 0x07;

  private static final char OP_PIN_MODE      = 0x0A;
  private static final char OP_SERIAL_ACTION = 0x0B;
  private static final char OP_SERIAL_BEGIN  = 0x0C;
  private static final char OP_SERIAL_READ   = 0x0D;
  private static final char OP_SERIAL_WRITE  = 0x0E;

  private static final char OP_TWI_ACTION    = 0x1B;
  private static final char OP_TWI_BEGIN     = 0x1C;
  private static final char OP_TWI_READ      = 0x1D;
  private static final char OP_TWI_WRITE     = 0x1E;
  private static final char OP_REGISTER_OP   = 0x1F;

  public static final int INPUT  = 0x00;
  public static final int OUTPUT = 0x01;
  public static final boolean LOW    = false;
  public static final boolean HIGH   = true;

  public static final int REG_OP_READ = 0x00;
  public static final int REG_OP_SET  = 0x01;
  public static final int REG_OP_AND  = 0x02;
  public static final int REG_OP_OR   = 0x03;
  public static final int REG_OP_XOR  = 0x04;
  public static final int REG_DDR     = 0x00;
  public static final int REG_PORT    = 0x01;
  public static final int REG_PIN     = 0x03;
  
//  public static final Pin PWM0 = new Pin();
  
  /**
   * @param ctxt
   */
  public DrAwesome(InputStream in, OutputStream out) {
    super(in, out);
  }
  
  
  boolean value = false;
  
  public  void init() {
    Log.d(TAG, "toggling 59 to " + value);
    try {
      digitalWrite(59, value);
      value = !value;
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  public void digitalWrite(int pin, boolean value) throws IOException {
    char header = (char) (MSG_SIZE_1 | OP_DIGITAL_WRITE);
    char data = (char) (((value) ? 0x80 : 0x00) | ((char) pin & 0x7F));
    getOutputStream().write(new byte[]{(byte)header, (byte)data});
  }

  public void run() {
    // TODO Auto-generated method stub
    
  }

  
}
