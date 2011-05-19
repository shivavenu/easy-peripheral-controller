// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.util;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class Bus extends Pin {

  private int mAddr;
  private int mSize;
  private byte[] mBuffer;
  
  /**
   * @param id
   * @param name
   * @param capabilities
   */
  public Bus(String name, Capability ... capabilities) {
    super(0, name, capabilities);
  }

  public void setBufferSize(int size) {
    mBuffer = new byte[size];
  }
  
  public synchronized void incoming(byte addr, byte[] arr, int offset, int len) {
    mAddr = addr;
    mSize = len;
    System.arraycopy(arr, offset, mBuffer, 0, len);
  }
  
  public int available() {
    return mSize;
  }

  public int fromAddress() {
    return mAddr;
  }
  
  public int read(byte[] arr, int offset, int len) {
    System.arraycopy(mBuffer, 0, arr, offset, len);
    return len;
  }  
}
