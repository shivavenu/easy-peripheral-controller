// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.Joystick;
import com.google.robotics.peripheral.util.Range;


/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkJoystick implements Joystick  {

  private AdkSwitch button;
  private Range xPosition = new Range(-120,120);
  private Range yPosition = new Range(-120,120);
  
  private AdkController mController;
  
  public AdkJoystick(AdkController controller) {
    mController = controller;
    button = new AdkSwitch(controller);
  }
  
  public void setButton(boolean val) {
    button.setClosed(val);
  }
  
  public void setX(int val) {
    xPosition.setPosition(val);
  }
  
  public void setY(int val) {
    yPosition.setPosition(val);
  }
  
  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.Joystick#getX()
   */
  
  public float getX() {
    return xPosition.toFloat();    
  }
  
  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.Joystick#getY()
   */
  
  public float getY() {
    return yPosition.toFloat();
  }
  

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.Joystick#isButtonPressed()
   */
  
  public boolean isButtonPressed() {
    return button.isClosed();
  }
  
}
