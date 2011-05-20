// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface PwmOutput extends Reservable {
  public void setActive(boolean on);
  public void setPeriod(int usec);
  public void setPulseWidth(int usec);
  // TODO(daden): Add a helper class for setFrequency, setDutyCycle.
}
