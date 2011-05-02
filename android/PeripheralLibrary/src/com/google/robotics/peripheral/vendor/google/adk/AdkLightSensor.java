// Copyright 2011 Google Inc. All Rights Reserved.

// has to be android.hardware to extend the Sensor* classes
package com.google.robotics.peripheral.vendor.google.adk;

import android.os.Handler;
import android.os.Message;

import com.google.robotics.peripheral.device.LightSensor;
import com.google.robotics.peripheral.util.AbstractInputResource;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkLightSensor extends AbstractInputResource implements LightSensor {
  
  private int lux = 0;
  
  
  public AdkLightSensor(AdkController controller) {
    super();
  }
  
  public int getLux() {
    return lux;
  }
  
  public void setValue(int val) {
    lux = val;
    notifyListeners();
  }
  
}
