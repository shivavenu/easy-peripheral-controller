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

package com.google.robotics.peripheral.vendor.google.adk;

import android.util.Log;

import com.google.robotics.peripheral.device.Servo;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkServo extends AdkMessage implements Servo {
  
  private static final String TAG = "AdkServo";
 
  int maxWidth = 2000;
  int minWidth = 1000;  
  int pulseWidth = -1;
 
  public AdkServo(DemoKit controller) {
    super(controller);
    controller.register(this);    
  }

  public void setPosition(float f) {
    setPulseWidth((int)(((maxWidth - minWidth) * f) + minWidth));
  }
  
  public void setBounds(int min, int max) {
    minWidth = min;
    maxWidth = max;    
  }

  public int getPulseWidth() {
    return pulseWidth;
  }
  
  /*
   * The bounds of the servo driver in the firmware is actually 
   * 600-2400 usec, we have to map that range into the one byte value 
   * in the message. 
   * Therefore 7usec per increment, lower bound = 600;
   */
  public void setPulseWidth(int microseconds) {
    if (microseconds < 0 ) {
      throw new RuntimeException("Pulse width must be 0 or greater.");
    }
    if (microseconds != pulseWidth) {
      pulseWidth = microseconds;
      int mappedValue = Math.max(0, (microseconds - 600) / 7);
      setValue(Math.min(255, mappedValue));
      invalidate();
    }
  }

}
