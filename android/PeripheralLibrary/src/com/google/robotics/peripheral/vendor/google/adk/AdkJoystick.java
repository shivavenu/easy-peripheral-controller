// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import android.os.Handler;
import android.os.Message;

import com.google.robotics.peripheral.device.ChangeNotifier;
import com.google.robotics.peripheral.device.Joystick;
import com.google.robotics.peripheral.util.Range;


/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkJoystick implements Joystick, ChangeNotifier  {

  private AdkSwitch button;
  private Range xPosition = new Range(-120,120);
  private Range yPosition = new Range(-120,120);
  
  private AdkController mController;
  
  private Handler listener;
  
  public AdkJoystick(AdkController controller) {
    mController = controller;
    button = new AdkSwitch(controller);
  }
  
  public void setButton(boolean val) {
    button.setClosed(val);
  }
  
  public void setPosition(int x, int y) {
    if (x != xPosition.getPosition() || y != yPosition.getPosition()) {
    xPosition.setPosition(x);
    yPosition.setPosition(y);
    if (listener != null) {
      listener.sendMessage(Message.obtain(listener, 1, this));
    }
    }
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
  
  public void registerHandler(Handler handler) {
    listener = handler;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.ChangeNotifier#unregisterHandler(android.os.Handler)
   */
  @Override
  public void unregisterHandler(Handler handler) {
    if (listener == handler) {
      listener = null;
    }
  }
}
