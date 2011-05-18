// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.AnalogInput;
import com.google.robotics.peripheral.util.ChangeListener;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeAnalogInput implements AnalogInput {

  public AwesomeAnalogInput(DrAwesome theDoctor, Pin pin) {
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.util.ChangeNotifier#registerListener(com.google.robotics.peripheral.util.ChangeListener)
   */
  @Override
  public void registerListener(ChangeListener listener) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.util.ChangeNotifier#unregisterListener(com.google.robotics.peripheral.util.ChangeListener)
   */
  @Override
  public void unregisterListener(ChangeListener listener) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.AnalogInput#getValue()
   */
  @Override
  public float getValue() {
    // TODO Auto-generated method stub
    return 0;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.AnalogInput#getReference()
   */
  @Override
  public float getReference() {
    // TODO Auto-generated method stub
    return 0;
  }
  
}
