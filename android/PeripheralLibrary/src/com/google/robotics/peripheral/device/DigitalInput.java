// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

import com.google.robotics.peripheral.util.ChangeNotifier;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface DigitalInput extends ChangeNotifier {

  public abstract boolean getValue();

}
