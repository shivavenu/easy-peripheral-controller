// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.util.AbstractResource;
import com.google.robotics.peripheral.util.Pin;
import com.google.robotics.peripheral.util.Range;

import java.io.IOException;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeServo extends AbstractResource implements Servo {

  Range mRange = new Range(1000,2000);
  DrAwesome mController;
  Pin mPin;
  int mNum;
  
  public AwesomeServo(DrAwesome theDoctor, Pin pin) {
    mPin = pin;
    mPin.reserve(this);
    mController = theDoctor;
    mNum = pin.toInteger();
    reserve();
    try {
      mController.setupServo(mNum, pin.toInteger(), 0);
    } catch (IOException e) {
      setOperational(false);
      e.printStackTrace(); // servo wont work.
    }
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Servo#setPosition(float)
   */
  @Override
  public void setPosition(float position) {
    mRange.setRelative(position);
    if (isOperational()) {
      try {
        mController.setServoPulse(mNum, mRange.getPosition());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        setOperational(false);
        e.printStackTrace();
      }
    }
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Servo#setBounds(int, int)
   */
  @Override
  public void setBounds(int min, int max) {
    mRange.setBounds(min,max);
  }
  
  @Override
  public void release() {
    try {
      mController.tearDownServo(mNum);
    } catch (IOException e) {
      e.printStackTrace();
    }
    setOperational(false);
    mPin.release(this);
    super.release();
  }
  
}
