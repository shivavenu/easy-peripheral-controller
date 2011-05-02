// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Place holder to catch the commonality between the various controllers. 
 * So far the list is 3 
 * 
 * - ADK with stock firmware
 * - ADK with peripheralcontroller firmware 
 * - IOIO, connected via adb host
 * 
 * 
 * @author arshan@google.com
 *
 */
public abstract class Controller implements Runnable{
  
  public static String ACCESSORY_STRING;
  
  // Supported external accesss to the controller internals.
  /*
   * public abstract DigitalOutput openDigitalOutput(Pin pin); public abstract
   * DigitalInput openDigitalInput(Pin pin); public abstract PwmOutput
   * openPwmOutput(Pin pin); public abstract PwmInput openPwmInput(); public
   * abstract PulseOutput openPulseOutput(); public abstract PulseInput
   * openPulseInput(); public abstract AnalogInput openAnalogInput(); public
   * abstract AnalogOutput openAnalogOutput();
   */
  
  public abstract OutputStream getOutputStream();
  public abstract InputStream getInputStream();

}
