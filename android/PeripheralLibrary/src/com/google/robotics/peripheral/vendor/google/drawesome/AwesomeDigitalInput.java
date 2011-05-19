// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.DigitalInput;
import com.google.robotics.peripheral.util.AbstractInputResource;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 * 
 */
public class AwesomeDigitalInput extends AbstractInputResource implements DigitalInput {

  private boolean mValue;
  private Pin mPin;

  public AwesomeDigitalInput(DrAwesome theDoctor, Pin pin) {
    mPin = pin;
    mPin.reserve(this);
  }

  public void setValue(boolean val) {
    mValue = val;
    this.notifyListeners();
  }

  @Override
  public boolean getValue() {
    return mValue;
  }

  @Override
  public void release() {
    super.release();
    mPin.release(this);
  }

  public Pin getPin() {
    return mPin;
  }  
  
}
