// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.drawesome;

import com.google.robotics.peripheral.device.AnalogInput;
import com.google.robotics.peripheral.util.AbstractInputResource;
import com.google.robotics.peripheral.util.Pin;

import java.io.IOException;

/**
 * @author arshan@google.com (Arshan Poursohi)
 * 
 */
public class AwesomeAnalogInput extends AbstractInputResource implements AnalogInput {

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
