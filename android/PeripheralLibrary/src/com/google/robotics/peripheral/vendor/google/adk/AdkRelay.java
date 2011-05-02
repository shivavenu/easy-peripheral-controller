// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.DigitalOutput;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkRelay extends AdkMessage implements DigitalOutput {

  boolean state;
  DemoKit mController;
  
  public AdkRelay(DemoKit controller) {
    mController = controller;
    mController.register(this);
  }
  
  
  public void setValue(boolean val) {
    state = val;
    setValue(val?0x1:0x0);
    invalidate();
  }
  
  public boolean getState() {
    return state;
  }

}
