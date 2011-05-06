// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import android.util.Log;

import com.google.robotics.peripheral.device.Servo;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkServo extends AdkMessage implements Servo {
  
  private static final String TAG = "Controller::Servo";
 
  int maxWidth = 2000;
  int minWidth = 1000;  
  int pulseWidth = 1500;
 
  public AdkServo(DemoKit controller) {
    super(controller);
    controller.register(this);    
    invalidate();
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
  
  public void setPulseWidth(int microseconds) {
    if (microseconds != pulseWidth) {
      pulseWidth = microseconds;    
      setValue((getPulseWidth() - 1000) / 4);
      invalidate();
    }
  }
}
