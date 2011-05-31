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

import com.yggdrasil.easyperipheral.device.Joystick;
import com.yggdrasil.easyperipheral.util.AbstractInputResource;
import com.yggdrasil.easyperipheral.util.Range;

public class AdkJoystick extends AbstractInputResource implements Joystick {

  private AdkSwitch button;
  private Range xPosition = new Range(-120,120);
  private Range yPosition = new Range(-120,120);
  
  private AdkController mController;
  
  public AdkJoystick(AdkController controller) {
    mController = controller;
    button = new AdkSwitch(controller);
  }
  
  public void setButton(boolean val) {
    button.setClosed(val);
    // Notify listeners only on the button down.
    if (val) notifyListeners();
  }
  
  public void setPosition(int x, int y) {
    if (x != xPosition.getPosition() || y != yPosition.getPosition()) {
    xPosition.setPosition(x);
    yPosition.setPosition(y);
    notifyListeners();
    }
  }
  
  public float getX() {
    return xPosition.toFloat();    
  }
  
  public float getY() {
    return yPosition.toFloat();
  }
  
  public boolean isButtonPressed() {
    return button.isClosed();
  }  
  
}
