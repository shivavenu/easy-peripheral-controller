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


import com.yggdrasil.easyperipheral.device.DigitalInput;
import com.yggdrasil.easyperipheral.util.AbstractInputResource;
import com.yggdrasil.easyperipheral.util.Pin;

public class AwesomeDigitalInput extends AbstractInputResource implements DigitalInput {

  private boolean mValue;
  private Pin mPin;

  public AwesomeDigitalInput(DrAwesome theDoctor, Pin pin) {
    mPin = pin;
    mPin.reserve(this);
  }

  public void setValue(boolean val) {
    mValue = val;
    this.notifyListeners();
  }

  @Override
  public boolean getValue() {
    return mValue;
  }

  @Override
  public void release() {
    super.release();
    mPin.release(this);
  }

  public Pin getPin() {
    return mPin;
  }  
  
}
