// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google;

import android.util.Log;

import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.vendor.google.adk.AdkController;
import com.google.robotics.peripheral.vendor.google.adk.AdkMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Extend the adkcontroller to deal with connecting directly to 18 servos.
 * Assumes dkhawks firmware is installed.
 * 
 * @author arshan
 * 
 */
public class ServoXVIII extends AdkController {

  // Mirror the onboard components.
  private XVIIIServo servos[] = new XVIIIServo[18];

  private static final String TAG = "Controller::ServoKit";
  DeviceSync dSync;

  List<AdkMessage> controlledDevices = new LinkedList<AdkMessage>();
  private final Object controlledDevicesLock = new Object();
  
  public ServoXVIII(InputStream in, OutputStream out) {
    super(in, out);
    init();
  }

  // setup all the object state
  protected void init() {
    // All of the world is 18 servos.
    for (int x = 0; x < 18; x++) {
      servos[x] = new XVIIIServo(this);
      servos[x].setPrefix(0x2);
      servos[x].setId(0x10 + x);
    }
       
    new Thread(this).start();
    startDeviceSync();
  }

  protected void startDeviceSync() {
    dSync = new DeviceSync();
    dSync.start();    
  }
  
  public Servo getServo(int num) {
    return (Servo) servos[num];
  }

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

  public class XVIIIServo extends AdkMessage implements Servo {

    private static final String TAG = "Controller::Servo";
    
    int maxWidth = 2000;
    int minWidth = 1000;  
    int pulseWidth = 0;
    
    
    public XVIIIServo(ServoXVIII controller) {
      controller.register(this);
    }
    
    public void setPosition(float f) {
      setPulseWidth((int)(((maxWidth - minWidth) * f) + minWidth));
    }
   
    public void setBounds(int min, int max) {
      minWidth = min;
      maxWidth = max;    
    }

    public int getPulseWidth() {
      return pulseWidth;
    }
    
    public void setPulseWidth(int microseconds) {
        pulseWidth = microseconds;    
        setValue((getPulseWidth() - 1000) / 4);
        invalidate();
    }
  }

  /**
   * An outgoing spooler thread, that scans the registered peripherals and 
   * sends a control packet for any that have been marked as changed (isInvalid())
   * 
   * Can throttle bandwidth _to_ the controller board here.
   *
   */
  public class DeviceSync extends Thread{
    
    private boolean running = true;
    private int failures = 0;
    
    @Override
    public void run() {      
      while (running) {
        try {
          if (getOutputStream() != null) {
          // Clearly this can be optimized. so do it.
            synchronized (controlledDevicesLock) {
              for (AdkMessage device : controlledDevices) {
                if (!device.isValid()) {
                  // Log.d(TAG, "sending control msg : " + printBytes(device.getMessage()));
                  getOutputStream().write(device.getMessage());
                  device.validate();
                  failures = 0;
                }
              }              
            }
          }
        //  Thread.yield();
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
          // there seems to be a few of these at init, before the device is
          // setup maybe? ... I must be using the wrong signal to start
          //if (failures++ > 10){
            running = false;
            Log.d(TAG, "exiting due to channel breaking");
            onDisconnected();
          //}
        }
      }
    }
  }
  
  public void register(AdkMessage device) {
    synchronized (controlledDevicesLock) {
      controlledDevices.add(device);
    }
  }

  public void unregister(AdkMessage device) {
    synchronized (controlledDevicesLock) {
      controlledDevices.remove(device);
    }
  }
  

}
