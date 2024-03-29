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
package com.yggdrasil.easyperipheral.vendor.google.adk;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.util.Log;

import com.yggdrasil.easyperipheral.connector.ConnectionListener;
import com.yggdrasil.easyperipheral.device.DigitalOutput;
import com.yggdrasil.easyperipheral.device.Joystick;
import com.yggdrasil.easyperipheral.device.LightSensor;
import com.yggdrasil.easyperipheral.device.RgbLed;
import com.yggdrasil.easyperipheral.device.Servo;
import com.yggdrasil.easyperipheral.device.Switch;
import com.yggdrasil.easyperipheral.device.TemperatureSensor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Slightly subversive version of controller that assumes the whole ADK. ie. the
 * shield and the main board. 
 * 
 * Code is largely swiped from the AndroidAccessory DemoKit application.
 * 
 */
public class DemoKit extends AdkController implements Runnable, ConnectionListener {

	public static final String ACCESSORY_STRING = 
			"com.google.android.DemoKit";
	
  // Mirror the onboard components.
  private AdkServo servos[] = new AdkServo[3];
  private AdkRgbLed rgb[] = new AdkRgbLed[3];
  private AdkJoystick joystick;
  private AdkSwitch buttons[] = new AdkSwitch[3];
  private AdkSwitch touchDroid;
  private AdkRelay relays[] = new AdkRelay[2];
  private AdkLightSensor lightSensor;
  private AdkTemperatureSensor temperatureSensor;
  
  private static final String TAG = "Controller::DemoKit";
  
  DeviceSync dSync;

  List<AdkMessage> controlledDevices = new LinkedList<AdkMessage>();
  LinkedBlockingQueue<AdkMessage> outputQueue = new LinkedBlockingQueue<AdkMessage>();
  private final Object controlledDevicesLock = new Object();
  
  public DemoKit(Context context, ConnectionListener listener) {
    super(context, listener);
  }
  
  public DemoKit(InputStream in, OutputStream out) {
    super(in,out);
    init();
  }

  
  /**
   * Catch the connected signal and start our own threads. 
   * Make sure you call the super.connected so that things are properly 
   * initialized.
   */
  @Override
  public void connected(UsbAccessory accessory) {
    Log.d(TAG, "connected");
    init();
    
    super.connected(accessory);   
    
    Log.d(TAG, "starting i/o threads");
    
    new Thread(this).start();
    startDeviceSync();
  }
  
  @Override 
  public void queueOutputMessage(AdkMessage message){
    if (! outputQueue.contains(message) ) {
      outputQueue.offer(message);
    }
  }
  
  // setup all the object state
  protected void init() {   
    for (int x = 0; x < 3; x++) {
      
      servos[x] = new AdkServo(this);
      servos[x].setPrefix(0x2);
      servos[x].setId(0x10+x);
      
      rgb[x] = new AdkRgbLed(this);
      rgb[x].setPrefix(0x2);
      rgb[x].setId(x*3);
      
      buttons[x] = new AdkSwitch(this);
      
    }
    
    for (int x = 0; x< 2; x++) {
      relays[x] = new AdkRelay(this);
      relays[x].setPrefix(0x3);
      relays[x].setId(x);
    }
    
    touchDroid = new AdkSwitch(this);
    joystick = new AdkJoystick(this);
    temperatureSensor = new AdkTemperatureSensor(this);
    lightSensor = new AdkLightSensor(this);    
  }
  
  protected void startDeviceSync() {
    dSync = new DeviceSync();
    dSync.start();    
  }

  public Servo getServo(int num) {
    return servos[num];
  }
  
  public RgbLed getLed(int num) {
    return rgb[num];
  }
  
  public Joystick getJoystick() {
    return joystick;
  }
  
  public Switch getButton(int num) {
    return buttons[num];
  }

  public LightSensor getLightSensor() {
    return lightSensor;
  }
  
  public TemperatureSensor getTemperatureSensor() {
    return temperatureSensor;
  }
  
  public DigitalOutput getRelay(int num) {
    return relays[num];
  }
  
  private int composeInt(byte hi, byte lo) {
    int val = (int) hi & 0xff;
    val *= 256;
    val += (int) lo & 0xff;
    return val;
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
          case 0x1:
            if (len >= 3) {
              handleSwitchMessage(buffer[i + 1], buffer[i + 2]);
            }
            i += 3;
            break;

          case 0x4:
            if (len >= 3) {
              handleTemperatureMessage(composeInt(buffer[i + 1], buffer[i + 2]));
            }
            i += 3;
            break;

          case 0x5:
            if (len >= 3) { 
              handleLightMessage(composeInt(buffer[i + 1], buffer[i + 2]));
            }
            i += 3;
            break;

          case 0x6:
            if (len >= 3) {
              handleJoystickPositionMessage(buffer[i + 1], buffer[i + 2]);
            }
            i += 3;
            break;

          default:
            Log.d(TAG, "unknown msg: " + buffer[i]);
            i = len;
            break;
        }
      }
    }
  
    Log.d(TAG, "exiting input thread, and disconnecting accessory");
    disconnected();
    
  }
  
  
  private void handleSwitchMessage(int sw, int state) {    
    if (sw < 3) {
      buttons[sw].setClosed(state == 0);
    }
    else if (sw == 3) {
      touchDroid.setClosed(state == 0);         
    }
    else if (sw == 4) {
      joystick.setButton(state == 0); 
    }
    else {
      throw new RuntimeException("Bad message from DemoKit");
    }
  }

  private void handleTemperatureMessage(int temp) {
    temperatureSensor.setTemperature(temp);
  }
  
  private void handleLightMessage(int light) {
    lightSensor.setValue(light);
  }
  
  private void handleJoystickPositionMessage(int x, int y) {
    joystick.setPosition(x, y);
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
      AdkMessage message;
      while (running) {
        try {
          message = outputQueue.take();
          if (getOutputStream() != null) {
           getOutputStream().write(message.toBytes());
          }
          else {
            running = false;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
          // there seems to be a few of these at init, before the device is
          // setup maybe? ... I must be using the wrong signal to start
          //if (failures++ > 10){
            running = false;
            Log.d(TAG, "exiting due to channel breaking");
            disconnected();
          //}
        }
      }
      Log.d(TAG, "Exiting sync thread");
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
  
  // for debug only, erase me.
  public String printBytes(byte[] arr) {
    String result = "[";
    for (byte x : arr) {
      result += " "+x;
    }
    result += "]";
    return result;
  }  
}
