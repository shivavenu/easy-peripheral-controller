// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface Servo {

  /**
   * Position of the servo relative to the bounds.
   *
   * @param position
   */
  public void setPosition(float position);

  /**
   * Minimum and maximum bounds for the pulse width in usec.
   *
   * SHOULD default to (1000, 2000) in implementations.
   *
   * @param min
   * @param max
   */
  public void setBounds(int min, int max);

}
