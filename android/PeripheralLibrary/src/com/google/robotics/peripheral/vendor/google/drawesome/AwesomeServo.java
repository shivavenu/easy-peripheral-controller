/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.util.AbstractResource;
import com.google.robotics.peripheral.util.Pin;
import com.google.robotics.peripheral.util.Range;

import java.io.IOException;

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
      setOperational(true);
    } catch (IOException e) {
      setOperational(false);
      e.printStackTrace(); 
    }
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Servo#setPosition(float)
   */
  @Override
  public void setPosition(float position) {
    mRange.setRelative(position);
    updateController();
  }
  
  public void setPulseWidth(int microseconds) {
    mRange.setPosition(microseconds);
    updateController();
  }
  
  private void updateController() {
    if (isOperational()) {
      try {
        mController.setServoPulse(mNum, mRange.getPosition());
      } catch (IOException e) {
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
  

  public Pin getPin() {
    return mPin;
  }
  
}
