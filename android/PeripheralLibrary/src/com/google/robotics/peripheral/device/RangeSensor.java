// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

import com.google.robotics.peripheral.util.ChangeNotifier;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public interface RangeSensor extends ChangeNotifier {

  /**
   * return range from sensor in centimeters
   */
  public float getRange(); 
}
