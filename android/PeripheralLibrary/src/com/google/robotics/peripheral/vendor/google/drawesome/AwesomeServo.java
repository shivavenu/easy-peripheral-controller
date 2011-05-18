// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.util.Pin;
import com.google.robotics.peripheral.util.Range;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeServo implements Servo {

  Range mRange = new Range(1000,2000);
  DrAwesome mController;
  Pin mPin;
  
  public AwesomeServo(DrAwesome theDoctor, Pin pin) {
    mPin = pin;
    mController = theDoctor;
//    mController.setupServo(servoN, pin, initUSec);
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Servo#setPosition(float)
   */
  @Override
  public void setPosition(float position) {
//    mRange.setPosition(position);
 //   mController.setServoPulse(mNum, mRange.getPosition());
    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Servo#setBounds(int, int)
   */
  @Override
  public void setBounds(int min, int max) {
    
  }
}
