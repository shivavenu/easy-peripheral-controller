// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.AnalogInput;
import com.google.robotics.peripheral.util.AbstractInputResource;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeAnalogInput extends AbstractInputResource implements AnalogInput {

  float mValue;
  float mReference = 3.3f;
  private Pin mPin;
  
  public AwesomeAnalogInput(DrAwesome theDoctor, Pin pin) {
    mPin = pin;
    mPin.reserve(this);
  }

  @Override
  public float getValue() {
    return mValue;
  }
  
  protected void setValue(int fl) {
    mValue = fl; // this is in LSB's gotta convert to Volts ... 
    notifyListeners();
  }

  
  @Override
  public float getReference() {
    return mReference;
  }
  
  @Override
  public void release() {
    mPin.release(this);
    super.release();
  }
  
  public Pin getPin() {
    return mPin;
  }
  
}
