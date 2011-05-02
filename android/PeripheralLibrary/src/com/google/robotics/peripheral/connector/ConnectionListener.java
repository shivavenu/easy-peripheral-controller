// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.connector;

import android.hardware.usb.UsbAccessory;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface ConnectionListener {
  public void connected(UsbAccessory accessory);
  public void connectionFailed(UsbAccessory accessory);
  public void disconnected();
}
