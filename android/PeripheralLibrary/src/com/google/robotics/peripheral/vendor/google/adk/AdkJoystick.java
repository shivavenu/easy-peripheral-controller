// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;


import com.google.robotics.peripheral.device.Joystick;
import com.google.robotics.peripheral.util.AbstractInputResource;
import com.google.robotics.peripheral.util.ChangeNotifier;
import com.google.robotics.peripheral.util.Range;


/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkJoystick extends AbstractInputResource implements Joystick {

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
    // Notify listeners only on the button down.
    if (val) notifyListeners();
  }
  
  public void setPosition(int x, int y) {
    if (x != xPosition.getPosition() || y != yPosition.getPosition()) {
    xPosition.setPosition(x);
    yPosition.setPosition(y);
    notifyListeners();
    }
  }
  
  public float getX() {
    return xPosition.toFloat();    
  }
  
  public float getY() {
    return yPosition.toFloat();
  }
  
  public boolean isButtonPressed() {
    return button.isClosed();
  }  
  
}
