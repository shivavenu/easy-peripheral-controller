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


import com.yggdrasil.easyperipheral.device.DigitalOutput;
import com.yggdrasil.easyperipheral.device.RangeSensor;
import com.yggdrasil.easyperipheral.util.Pin;

public class AwesomeMaxBotix extends AwesomeAnalogInput implements RangeSensor {

  private DigitalOutput mTrigger;
  private int mRange;
  
  /**
   * @param theDoctor
   * @param pin
   */
  public AwesomeMaxBotix(DrAwesome theDoctor, Pin pin) {
    super(theDoctor, pin);
    
  }
  
  public AwesomeMaxBotix(DrAwesome theDoctor, Pin pin, DigitalOutput trigger) {
    super(theDoctor, pin);
    mTrigger = trigger;
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.RangeSensor#getRange()
   */
  @Override
  public float getRange() {
    return mRange;
  }
  
  @Override
  public void setValue(int value) {
    
  }
    
  
}