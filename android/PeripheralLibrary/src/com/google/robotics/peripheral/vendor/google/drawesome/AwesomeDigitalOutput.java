// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.DigitalOutput;
import com.google.robotics.peripheral.util.AbstractResource;
import com.google.robotics.peripheral.util.Pin;

import java.io.IOException;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeDigitalOutput extends AbstractResource implements DigitalOutput {

  DrAwesome mController;
  Pin mPin;
  
  public AwesomeDigitalOutput(DrAwesome theDoctor, Pin pin) {
    mController = theDoctor;
    mPin = pin;
    mPin.reserve(this);
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.DigitalOutput#setValue(boolean)
   */
  @Override
  public void setValue(boolean value) {
    try {
      mController.digitalWrite(mPin.toInteger(), value);
    } catch (IOException e) {
      // disconnect the controller here? 
      e.printStackTrace();
    }
  }
  
  public void release() {
    mPin.release(this);
    super.release();
  }
  

  public Pin getPin() {
    return mPin;
  }
  
  
}
