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
package com.yggdrasil.easyperipheral.vendor.google.drawesome;


import com.yggdrasil.easyperipheral.channel.TwiChannel;
import com.yggdrasil.easyperipheral.util.AbstractInputResource;
import com.yggdrasil.easyperipheral.util.Bus;

import java.io.IOException;

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
