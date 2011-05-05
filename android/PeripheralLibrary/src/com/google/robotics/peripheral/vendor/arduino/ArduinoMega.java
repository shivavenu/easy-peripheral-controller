// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.arduino;

import com.google.robotics.peripheral.device.Controller;

/**
 * It might make more sense to organize this by the atmega controller? 
 * 
 * @author arshan@google.com (Your Name Here)
 *
 */
public abstract class ArduinoMega extends Controller {
  
  // Mapping of the arduino pins to the controller pins
  // ahem, so the naming is different for the Uno ... might want to just 
  // define that directly ... 
  public static final int PWM0 = 2;
  public static final int PWM1 = 3;
  public static final int PWM2 = 6;
  public static final int PWM3 = 7;
  public static final int PWM4 = 1;
  public static final int PWM5 = 5;
  public static final int PWM6 = 15;
  public static final int PWM7 = 16;
  public static final int PWM8 = 17;
  public static final int PWM9 = 18;
  public static final int PWM10 = 23;
  public static final int PWM11 = 24;
  public static final int PWM12 = 25;
  public static final int PWM13 = 26;  
  
  public static final int RX0 = 2;
  public static final int TX0 = 3;

  public static final int ANALOG0 = 97;
  public static final int ANALOG1 = 96;
  public static final int ANALOG2 = 95;
  public static final int ANALOG3 = 94;
  public static final int ANALOG4 = 93;
  public static final int ANALOG5 = 92;
  
  
  // Mega only? above is probably on the not mega ... 
  public static final int RX1 = 45;
  public static final int TX1 = 46;
  public static final int RX2 = 12;
  public static final int TX2 = 13;
  public static final int RX3 = 63;
  public static final int TX3 = 64;
  
  public static final int SDA = 44;
  public static final int SCl = 43;
  
  public static final int ANALOG6 = 91;
  public static final int ANALOG7 = 90;
  public static final int ANALOG8 =  82;
  public static final int ANALOG9 =  83;
  public static final int ANALOG10 = 84;
  public static final int ANALOG11 = 85;
  public static final int ANALOG12 = 86;
  public static final int ANALOG13 = 87;
  public static final int ANALOG14 = 88;
  public static final int ANALOG15 = 89;
  
  public static final int DIGITAL22 = 78;
  public static final int DIGITAL23 = 77;
  public static final int DIGITAL24 = 76;
  public static final int DIGITAL25 = 75;
  public static final int DIGITAL26 = 74;
  public static final int DIGITAL27 = 73;
  public static final int DIGITAL28 = 72;
  public static final int DIGITAL29 = 71;
  public static final int DIGITAL30 = 60;
  public static final int DIGITAL31 = 59;
  public static final int DIGITAL32 = 58;
  public static final int DIGITAL33 = 57;
  public static final int DIGITAL34 = 56;
  public static final int DIGITAL35 = 55;
  public static final int DIGITAL36 = 54;
  public static final int DIGITAL37 = 53;
  public static final int DIGITAL38 = 50;
  public static final int DIGITAL39 = 70;
  public static final int DIGITAL40 = 52;
  public static final int DIGITAL41 = 51;
  public static final int DIGITAL42 = 42;
  public static final int DIGITAL43 = 41;
  public static final int DIGITAL44 = 40;
  public static final int DIGITAL45 = 39;
  public static final int DIGITAL46 = 38;
  public static final int DIGITAL47 = 37;
  public static final int DIGITAL48 = 36;
  public static final int DIGITAL49 = 35;
  public static final int DIGITAL50 = 22;
  public static final int DIGITAL51 = 21;
  public static final int DIGITAL52 = 20;
  public static final int DIGITAL53 = 19;
  
  public static final int MISO = 22;
  public static final int MOSI = 21;
  public static final int SCLK = 20;
  public static final int SS   = 19;
  
}
