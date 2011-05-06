// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.Led;

/**
 * Led controller, we assume that we have a pwm that takes a byte value. 
 * If its the on/off variety of Led your after please use the Relay class.
 * 
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AdkLed extends AdkMessage implements Led {
  
  boolean mIsOn = false;
 
  DemoKit mController;
  
  public AdkLed(DemoKit controller) {
    super(controller);
    mController = controller;
    mController.register(this);
  }
  
  public boolean isOn() {
    return toBytes()[3] > 0;
  }

  public void setValue(float value) {
    super.setValue((int)value*255);
  }
  
}
