// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.Servo;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkServo extends AdkMessage implements Servo {
  
  private static final String TAG = "Controller::Servo";
  private static boolean serialize = true;
  
  int maxWidth = 2000;
  int minWidth = 1000;  
  int pulseWidth = 0;
  
  protected AdkController mController;
  
  public AdkServo(AdkController controller) {
    mController = controller;
    controller.register(this);    
  }

  /**
   * @param f
   */
  
  public void setPosition(float f) {
    setPulseWidth((int)(((maxWidth - minWidth) * f) + minWidth));
  }
 
  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.Servo#setMinMax(int, int)
   */
  
  public void setBounds(int min, int max) {
    minWidth = min;
    maxWidth = max;    
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.Servo#getPulseWidth()
   */

  public int getPulseWidth() {
    return pulseWidth;
  }
  
  
  public void setPulseWidth(int microseconds) {
   // if (microseconds != pulseWidth) {
     //  Log.d(TAG, "setting PW : " + microseconds);
      pulseWidth = microseconds;    
      invalidate();
   // }
  }
  
 
}
