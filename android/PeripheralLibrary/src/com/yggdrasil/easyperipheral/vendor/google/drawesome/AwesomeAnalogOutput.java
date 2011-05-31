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


import com.yggdrasil.easyperipheral.device.PwmOutput;
import com.yggdrasil.easyperipheral.util.AbstractResource;
import com.yggdrasil.easyperipheral.util.Pin;

public class AwesomeAnalogOutput extends AbstractResource implements PwmOutput {
  
  DrAwesome mController;
  Pin mPin;
  private int period = 2004; // this is based on the 490Hz from the arduino docs.
  private int pulseWidth = 0; 
  private int dutyCycle = 0;
  
  public AwesomeAnalogOutput(DrAwesome theDoctor, Pin pin) {
    mController = theDoctor;
    mPin = pin;
  }

  @Override
  public void setActive(boolean on) {
    
  }

  @Override
  public void setPeriod(int usec) {
    // Hmm, cant really support this, other then deriving the dutyCycle 
    // based on the user pulse width and period? 
  }

  /*
   * Since we can only request duty cycle, compute that based on 2004 usec 
   * period.
   */
  @Override
  public void setPulseWidth(int usec) {    
    pulseWidth = usec;
    
  }

  public Pin getPin() {
    return mPin;
  }
  
  
}
