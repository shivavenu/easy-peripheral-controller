// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.app;

import com.google.robotics.peripheral.channel.TwiChannel;

import java.io.IOException;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class HrmiMonitor {

  private TwiChannel mTwi;
  private int mAddr = 0; // not sure how this gets allocated.
  
  public HrmiMonitor(TwiChannel channel) {
    mTwi = channel;
  }
  
  private void sendCmd(byte cmd) throws IOException {
    mTwi.send(mAddr, new byte[]{cmd}, 0, 1);
  }
  
  private void sendCmdArg(byte cmd, byte arg) throws IOException {
    mTwi.send(mAddr, new byte[]{cmd, arg}, 0 , 2);
  }
  
}
