// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.util.Pin;
import com.google.robotics.peripheral.util.Pin.Capability;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomePin extends Pin {

  /**
   * @param id
   * @param name
   * @param capabilities
   */
  public AwesomePin(int id, String name, Capability ... capabilities) {
    super(id, name, capabilities);
  }
}
