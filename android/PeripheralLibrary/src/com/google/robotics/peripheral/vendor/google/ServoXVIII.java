// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.vendor.google.adk.AdkController;
import com.google.robotics.peripheral.vendor.google.adk.AdkServo;

/**
 * Extend the adkcontroller to deal with connecting directly to 
 * 18 servos. Assumes dkhawks firmware is installed.
 * @author arshan
 *
 */
public class ServoXVIII extends AdkController {

  // Mirror the onboard components.
  private AdkServo servos[] = new AdkServo[18];
   
  private static final String TAG = "Controller::ServoKit";

  public ServoXVIII(InputStream in, OutputStream out) {
	  super(in,out);
	  init();
  }
  
  // setup all the object state
  protected void init() {
    // All of the world is 18 servos.
    for (int x = 0; x < 18; x++) {
      servos[x] = new AdkServo(this);
      servos[x].setPrefix(0x2);
      servos[x].setId(0x10+x);      
    }  
  }

  public Servo getServo(int num) {
    return (Servo) servos[num];
  }
  
  /**
   * When this is called the accessory will have been opened and the
   * mInput/mOutput streams are viable if things have gone without trouble.
   * 
   */
  public void run() {
    int ret = 0;
    byte[] buffer = new byte[16384];
    int i;

    while (ret >= 0) {
      try {
        ret = getInputStream().read(buffer);
      } catch (IOException e) {
        break;
      }

      i = 0;
      while (i < ret) {
        int len = ret - i;

        switch (buffer[i]) {
          default:
            Log.d(TAG, "unknown msg: " + buffer[i]);
            i = len;
            break;
        }
      }

    }
    Log.d(TAG, "thread out");
  }

}
