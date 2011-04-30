// Copyright 2011 Google Inc. All Rights Reserved.

// has to be android.hardware to extend the Sensor* classes
package com.google.robotics.peripheral.vendor.google.adk;

import android.os.Handler;
import android.os.Message;

import com.google.robotics.peripheral.device.ChangeNotifier;
import com.google.robotics.peripheral.device.LightSensor;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkLightSensor implements LightSensor, ChangeNotifier {
  
  private int lux = 0;
  Handler listener;
  
  public AdkLightSensor(AdkController controller) {
    super();
  }
  
  public int getLux() {
    return lux;
  }
  
  public void setValue(int val) {
    lux = val;
    if (listener != null) {
      listener.sendMessage(Message.obtain(listener, 1, this));
    }
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.ChangeNotifier#registerHandler(android.os.Handler)
   */
  @Override
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
