// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;



/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface LightSensor extends ChangeNotifier {
  public abstract int getLux();  
}
