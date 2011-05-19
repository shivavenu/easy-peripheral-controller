// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.DigitalOutput;
import com.google.robotics.peripheral.device.RangeSensor;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeMaxBotix extends AwesomeAnalogInput implements RangeSensor {

  private DigitalOutput mTrigger;
  private int mRange;
  
  /**
   * @param theDoctor
   * @param pin
   */
  public AwesomeMaxBotix(DrAwesome theDoctor, Pin pin) {
    super(theDoctor, pin);
    
  }
  
  public AwesomeMaxBotix(DrAwesome theDoctor, Pin pin, DigitalOutput trigger) {
    super(theDoctor, pin);
    mTrigger = trigger;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.RangeSensor#getRange()
   */
  @Override
  public float getRange() {
    return mRange;
  }
  
  @Override
  public void setValue(int value) {
    
  }
    
  
}
