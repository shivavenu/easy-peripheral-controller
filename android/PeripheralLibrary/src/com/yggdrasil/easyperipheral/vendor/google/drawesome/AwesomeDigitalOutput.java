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
import com.yggdrasil.easyperipheral.util.AbstractResource;
import com.yggdrasil.easyperipheral.util.Pin;

import java.io.IOException;

public class AwesomeDigitalOutput extends AbstractResource implements DigitalOutput {

  DrAwesome mController;
  Pin mPin;
  
  public AwesomeDigitalOutput(DrAwesome theDoctor, Pin pin) {
    mController = theDoctor;
    mPin = pin;
    mPin.reserve(this);
  }

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.device.DigitalOutput#setValue(boolean)
   */
  @Override
  public void setValue(boolean value) {
    try {
      mController.digitalWrite(mPin.toInteger(), value);
    } catch (IOException e) {
      // disconnect the controller here? 
      e.printStackTrace();
    }
  }
  
  public void release() {
    mPin.release(this);
    super.release();
  }
  

  public Pin getPin() {
    return mPin;
  }
  
  
}
