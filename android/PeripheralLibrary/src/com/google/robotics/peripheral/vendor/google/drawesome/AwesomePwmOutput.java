// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.PwmOutput;
import com.google.robotics.peripheral.util.Pin;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomePwmOutput implements PwmOutput {
  
  DrAwesome mController;
  Pin mPin;
  
  public AwesomePwmOutput(DrAwesome theDoctor, Pin pin) {
    mController = theDoctor;
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.PwmOutput#setActive(boolean)
   */
  @Override
  public void setActive(boolean on) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.PwmOutput#setPeriod(int)
   */
  @Override
  public void setPeriod(int usec) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.PwmOutput#setPulseWidth(int)
   */
  @Override
  public void setPulseWidth(int usec) {
    
    
  }

  
  public Pin getPin() {
    return mPin;
  }
  
  
}
