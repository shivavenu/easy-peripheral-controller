// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.TemperatureSensor;
import com.google.robotics.peripheral.util.AbstractInputResource;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AdkTemperatureSensor extends AbstractInputResource implements TemperatureSensor {
    
    private int temp = 0;
    
    public AdkTemperatureSensor(AdkController controller) {     
    }
    
    public void setTemperature(int val) {
      temp = val;
      notifyListeners();
    }
    
    public int getValue() {
      return temp;
    }
}
