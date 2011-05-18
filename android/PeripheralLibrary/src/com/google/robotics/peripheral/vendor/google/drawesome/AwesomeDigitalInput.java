// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.DigitalInput;
import com.google.robotics.peripheral.util.ChangeListener;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeDigitalInput implements DigitalInput {

  public AwesomeDigitalInput(DrAwesome theDoctor, Pin pin) {
    
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
   * @see com.google.robotics.peripheral.device.DigitalInput#getValue()
   */
  @Override
  public boolean getValue() {
    // TODO Auto-generated method stub
    return false;
  }
  
}

