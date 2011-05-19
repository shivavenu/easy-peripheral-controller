// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.channel;

import java.io.IOException;

public interface TwiChannel {

  public int send(int addr, byte[] message, int offset, int length) throws IOException;
  public void receive(int addr, int length) throws IOException;
  public int available();
  public int read(byte[] buffer, int offset, int length);

}
