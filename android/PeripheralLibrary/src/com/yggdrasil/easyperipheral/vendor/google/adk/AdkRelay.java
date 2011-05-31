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

import com.yggdrasil.easyperipheral.device.DigitalOutput;

public class AdkRelay extends AdkMessage implements DigitalOutput {

  boolean state;
  DemoKit mController;
  
  public AdkRelay(DemoKit controller) {
    super(controller);
    mController = controller;
    mController.register(this);
  }
  
  
  public void setValue(boolean val) {
    state = val;
    setValue(val?0x1:0x0);
    invalidate();
  }
  
  public boolean getState() {
    return state;
  }

}
