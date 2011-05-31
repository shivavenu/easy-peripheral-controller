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

import com.yggdrasil.easyperipheral.device.Led;

/**
 * Led controller, we assume that we have a pwm that takes a byte value. 
 * If its the on/off variety of Led your after please use the Relay class.
 */
public class AdkLed extends AdkMessage implements Led {
  
  boolean mIsOn = false;
 
  DemoKit mController;
  
  public AdkLed(DemoKit controller) {
    super(controller);
    mController = controller;
    mController.register(this);
  }
  
  public boolean isOn() {
    return toBytes()[3] > 0;
  }

  public void setValue(float value) {
    super.setValue((int)value*255);
  }
  
}
