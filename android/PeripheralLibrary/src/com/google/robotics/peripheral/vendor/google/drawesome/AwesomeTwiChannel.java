// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.channel.TwiChannel;
import com.google.robotics.peripheral.util.AbstractInputResource;
import com.google.robotics.peripheral.util.Bus;

import java.io.IOException;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AwesomeTwiChannel extends AbstractInputResource implements TwiChannel {

  private DrAwesome mController;
  private Bus mBus;
  
  public AwesomeTwiChannel(DrAwesome controller, Bus bus) {
    mController = controller;
    mBus = bus;
    mBus.reserve(this);
  }
  
  @Override
  public int send(int addr, byte[] message, int offset, int length) throws IOException {
    mController.writeTwi(addr, message, offset, length);
    return length;
  }

  
  @Override
  public void receive(int addr, int length) throws IOException {
    mController.readTwi(addr, length);
  }

  @Override
  public void onChange() {
    notifyListeners();
  }
  
  @Override
  public int available() {
    return mBus.available();
  }


  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.channel.TwiChannel#read(byte[], int, int)
   */
  @Override
  public int read(byte[] buffer, int offset, int length) {
    return mBus.read(buffer, offset, length);
  }
  

  
}
