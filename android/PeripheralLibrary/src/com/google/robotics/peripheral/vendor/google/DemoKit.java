// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.robotics.peripheral.connector.PeripheralConnector;
import com.google.robotics.peripheral.device.DigitalOutput;
import com.google.robotics.peripheral.device.Joystick;
import com.google.robotics.peripheral.device.LightSensor;
import com.google.robotics.peripheral.device.RgbLed;
import com.google.robotics.peripheral.device.Servo;
import com.google.robotics.peripheral.device.Switch;
import com.google.robotics.peripheral.device.TemperatureSensor;
import com.google.robotics.peripheral.vendor.google.adk.AdkController;
import com.google.robotics.peripheral.vendor.google.adk.AdkLightSensor;
import com.google.robotics.peripheral.vendor.google.adk.AdkRelay;
import com.google.robotics.peripheral.vendor.google.adk.AdkRgbLed;
import com.google.robotics.peripheral.vendor.google.adk.AdkServo;
import com.google.robotics.peripheral.vendor.google.adk.AdkTemperatureSensor;
import com.google.robotics.peripheral.vendor.google.adk.AdkSwitch;
import com.google.robotics.peripheral.vendor.google.adk.AdkJoystick;

/**
 * Slightly subversive version of controller that assumes the whole ADK. ie. the
 * shield and the main board. 
 * 
 * Code is largely swiped from the AndroidAccessory DemoKit application.
 * 
 */
public class DemoKit extends AdkController implements Runnable {

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

  private static final int MESSAGE_SWITCH = 1;
  private static final int MESSAGE_TEMPERATURE = 2;
  private static final int MESSAGE_LIGHT = 3;
  private static final int MESSAGE_JOY = 4;
  
  public DemoKit(PeripheralConnector connector) {
	  super(connector);
	  init();
  }
  
  public DemoKit(InputStream in, OutputStream out) {
    super(in,out);
    init();
  }
  
  // setup all the object state
  protected void init() {
    for (int x = 0; x < 3; x++) {
      
      servos[x] = new DkServo(this);
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
 
    new Thread(this).start();
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

      if (mHandler == null) {
        // something just stopped working
        super.mConnected = false;
        break;
      }
      i = 0;
      while (i < ret) {
        int len = ret - i;

        switch (buffer[i]) {
          case 0x1:
            if (len >= 3) {
              Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
              m.obj = new SwitchMsg(buffer[i + 1], buffer[i + 2]);
              mHandler.sendMessage(m);
            }
            i += 3;
            break;

          case 0x4:
            if (len >= 3) {
              Message m = Message.obtain(mHandler, MESSAGE_TEMPERATURE);
              m.obj = new TemperatureMsg(composeInt(buffer[i + 1], buffer[i + 2]));
              mHandler.sendMessage(m);
            }
            i += 3;
            break;

          case 0x5:
            if (len >= 3) { 
              Message m = Message.obtain(mHandler, MESSAGE_LIGHT);
              m.obj = new LightMsg(composeInt(buffer[i + 1], buffer[i + 2]));
              mHandler.sendMessage(m);
            }
            i += 3;
            break;

          case 0x6:
            if (len >= 3) {
              Message m = Message.obtain(mHandler, MESSAGE_JOY);
              m.obj = new JoyMsg(buffer[i + 1], buffer[i + 2]);
              mHandler.sendMessage(m);
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
  
    onDisconnected();
    
  }
  
  Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MESSAGE_SWITCH:
          SwitchMsg o = (SwitchMsg) msg.obj;

          if (o.getSw() == 0)
            buttons[0].setClosed(o.getState() != 0 ? false : true);
          else if (o.getSw() == 1)
            buttons[1].setClosed(o.getState() != 0 ? false : true);
          else if (o.getSw() == 2)
            buttons[2].setClosed(o.getState() != 0 ? false : true);
          else if (o.getSw() == 3)
            touchDroid.setClosed(o.getState() != 0 ? false : true);
          else if (o.getSw() == 4)
            joystick.setButton(o.getState() != 0 ? false : true);
          break;

        case MESSAGE_TEMPERATURE:
          TemperatureMsg t = (TemperatureMsg) msg.obj;
          temperatureSensor.setTemperature(t.getTemperature());
          break;

        case MESSAGE_LIGHT:
          LightMsg l = (LightMsg) msg.obj;
          lightSensor.setValue(l.getLight());
          break;

        case MESSAGE_JOY:
          JoyMsg j = (JoyMsg) msg.obj;          
          joystick.setPosition(j.getX(), j.getY());
          break;

      }
    }
  };

  private class SwitchMsg {
    private byte sw;
    private byte state;

    public SwitchMsg(byte sw, byte state) {
      this.sw = sw;
      this.state = state;
    }

    public byte getSw() {
      return sw;
    }

    public byte getState() {
      return state;
    }
  }

  private class TemperatureMsg {
    private int temperature;

    public TemperatureMsg(int temperature) {
      this.temperature = temperature;
    }

    public int getTemperature() {
      return temperature;
    }
  }

  private class LightMsg {
    private int light;

    public LightMsg(int light) {
      this.light = light;
    }

    public int getLight() {
      return light;
    }
  }

  private class JoyMsg {
    private int x;
    private int y;

    public JoyMsg(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }
  }

  /**
   * implementation of a servo that is specific to DemoKit
   * @author arshan@google.com (Your Name Here)
   *
   */
  public class DkServo extends AdkServo {
      
      DkMessage message;
      
      public DkServo(AdkController controller) {
        super(controller);
        // This is artificial limiting
        setBounds(1200,1800);
      }
      
      @Override
      public void setPosition(float position) {
        super.setPosition(position);
        
        // Odd but this is how the proto on demo kit wants the value. 
        // will have to fix.
        setValue((getPulseWidth() - 1000) / 4);
        invalidate();        
      }
  }
  
  public class DkMessage {

    private boolean synced = false;
    
    private byte[] message = new byte[3];
   
    public DkMessage setPrefix(int val) {
      message[0] = (byte)(val&0xff);
      return this;
    }
    
    public DkMessage setId(int val) {
      message[1] = (byte)(val&0xff);
      return this;
    }
    
    public DkMessage setValue(int val) {
      message[2] = (byte)(val&0xff);
      return this;
    }

    public boolean isValid() {
      return synced;
    }
    
    public void invalidate() {
      synced = false;
    }
    
    public void validate() {
      synced = true;
    }
    
    public byte[] getMessage(){
      return message;
    }
  }

  
}
