// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.TemperatureSensor;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AdkTemperatureSensor implements TemperatureSensor {
    
    private int temp = 0;
    
    public AdkTemperatureSensor(AdkController controller) {
     
    }
    
    public void setTemperature(int val) {
      temp = val;
    }
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.TemperatureSensor#getTemperature()
     */
    
    
    public int getValue() {
      return temp;
    }
}
