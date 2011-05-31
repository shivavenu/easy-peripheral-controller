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

import com.yggdrasil.easyperipheral.device.TemperatureSensor;
import com.yggdrasil.easyperipheral.util.AbstractInputResource;

public class AdkTemperatureSensor extends AbstractInputResource implements TemperatureSensor {
    
    private int temp = 0;
    
    public AdkTemperatureSensor(AdkController controller) {     
    }
    
    public void setTemperature(int val) {
      temp = val;
      notifyListeners();
    }
    
    public int getValue() {
      return temp;
    }
}
