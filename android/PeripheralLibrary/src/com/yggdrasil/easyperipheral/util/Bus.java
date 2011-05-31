/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.yggdrasil.easyperipheral.util;

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
