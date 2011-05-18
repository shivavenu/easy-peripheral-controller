// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.util;

import com.google.robotics.peripheral.device.Controller;
import com.google.robotics.peripheral.device.Reservable;

/**
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class Pin implements Reservable {
  
  public enum Capability {
    NONE          (0x0000),
    DIGITAL_INPUT (0x0001),
    DIGITAL_OUTPUT(0x0002),
    ANALOG_INPUT  (0x0004),
    ANALOG_OUTPUT (0x0008),
    PWM_INPUT     (0x0010),
    PWM_OUTPUT    (0x0020),
    PULSE_OUTPUT  (0x0040),
    PULSE_INPUT   (0x0080),
    PULSE_COUNTER (0x0100),
    QUAD_ENCODER  (0x0200),
    BUS_IO        (0x0400),
    SERVO_DRIVER  (0x0800);

    private int mFlag = 0;

    private Capability(int flag) {
      this.mFlag = flag;
    }
    
    public int getFlag() {
      return mFlag;
    }   
  };
   
  private int mCapabilities;
  private int mId;
  private String mName;
  private boolean mReserved;
  
  public Pin(int id, Capability ... capabilities) {
    this(id, "Pin " + id, capabilities);
  }

  public Pin(int id, String name, Capability ... capabilities) {
    for (Capability c : capabilities) {
      mCapabilities |= c.getFlag();
    }
    mId = id;
    mName = name; 
  }
  
  public boolean supports(Capability c){
    return (mCapabilities & c.getFlag()) > 0;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Reservable#reserve()
   */
  @Override
  public void reserve() {
    mReserved = true;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Reservable#release()
   */
  @Override
  public void release() {
    mReserved = false;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.Reservable#isReserved()
   */
  @Override
  public boolean isReserved() {
    return mReserved;
  }
  
  @Override
  public String toString() {
    return mName;
  }
  
  public int toInteger() {
    return mId;
  }
}
