// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface Joystick extends ChangeNotifier {

  /**
   * Returns the X position of the joystick as a float from 0 to 1. 
   * Where 0 and 1 are the extremes, and .5f is the middle.
   * @return relative X position
   */
  public float getX();
  
  
  /**
   * Returns the Y position of the joystick as a float from 0 to 1. 
   * Where 0 and 1 are the extremes, and .5f is the middle.
   * @return relative Y position
   */
  public float getY();
  
  /**
   * @return Button pressed
   */
  public boolean isButtonPressed();

}
