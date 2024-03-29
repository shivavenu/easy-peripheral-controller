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

import java.util.ArrayList;
import java.util.List;

public class Pin {
  
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
    SERVO_DRIVER  (0x0800), 
    UART          (0x1000), // These are to be used as virtual pins, 
    TWI           (0x2000), // still have to come up with something to allow
    SPI           (0x4000); // grouping pins together into functional units.

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
  private List<AbstractResource> mResources = new ArrayList<AbstractResource>();
  
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

  public void reserve(AbstractResource resource) {
    if (! mResources.contains(resource)) {
      mResources.add(resource);
    }
  }

  public void release(AbstractResource resource) {
    mResources.remove(resource);
  }

  public boolean isReserved() {
    return mResources.size() != 0;
  }
  
  @Override
  public String toString() {
    return mName;
  }
  
  public int toInteger() {
    return mId;
  }
  
  public void onChange() {
    for (AbstractResource res : mResources) {
      if (res instanceof AbstractInputResource) {
        ((AbstractInputResource)res).onChange();
      }
    }
  }
  
  public final List<AbstractResource> getResourcesAttached() {
    return mResources;
  }
  
  public void releaseAll(){
    mResources.clear();
  }
  
}
