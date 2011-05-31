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


import android.util.Log;

import com.yggdrasil.easyperipheral.device.AnalogInput;
import com.yggdrasil.easyperipheral.util.AbstractInputResource;
import com.yggdrasil.easyperipheral.util.Pin;

import java.io.IOException;

public class AwesomeAnalogInput extends AbstractInputResource implements AnalogInput {

  private static final String TAG = "AwesomeAnalogInput";
  
  float mValue;
  float mReference = 3.3f;
  private Pin mPin;
  private DrAwesome mController;
  private boolean running;
  private PollingLoop mPoll;
  
  public AwesomeAnalogInput(DrAwesome theDoctor, Pin pin) {
    mController = theDoctor;
    mPin = pin;
    mPin.reserve(this);
    startPolling(100);
  }

  @Override
  public float getValue() {
    return mValue;
  }

  protected void setValue(int fl) {
    mValue = fl; // this is in LSB's gotta convert to Volts ...
    notifyListeners();
  }


  @Override
  public float getReference() {
    return mReference;
  }

  @Override
  public void release() {
    running = false;
    mPin.release(this);
    super.release();
  }

  public Pin getPin() {
    return mPin;
  }

  private void startPolling(int period) {
    Log.d(TAG, "starting polling loop");
    mPoll = new PollingLoop(100);
    mPoll.start();
  }

  private class PollingLoop extends Thread {
    private int mPeriod = 1000;

    public PollingLoop(int p) {
      mPeriod = p;
    }

    public void run() {
      while (running) {
        try {
          mController.analogRead(mPin.toInteger());
          try {
            Thread.sleep(mPeriod);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
