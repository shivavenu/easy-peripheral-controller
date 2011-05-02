// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;



/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface Switch extends ChangeNotifier {

  /**
   * Returns the state of the switch.
   *
   * @returns true if the switch is closed, defined by the implementer.
   */
  public boolean isClosed();

}
