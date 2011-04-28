// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;



/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface Led {

  /**
   * Sets the value of the LED, takes a float from 0.0 to 1.0, where
   * 0.0 is off and 1.0 is full on.
   *
   * @param value
   */
  public void setValue(float value);

}
