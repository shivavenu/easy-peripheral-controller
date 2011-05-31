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
import android.graphics.Color;

import com.yggdrasil.easyperipheral.device.RgbLed;

public class AdkRgbLed extends AdkMessage implements RgbLed{
  
    private int color;
    private AdkLed red;
    private AdkLed green;
    private AdkLed blue;
    
    public AdkRgbLed(DemoKit controller) {
      super(controller);
      red = new AdkLed(controller);
      green = new AdkLed(controller);
      blue = new AdkLed(controller);   
      
      // Note that we do not register this module.
    }
        
    @Override 
    public void setPrefix(int value) {
      super.setPrefix(value);
      red.setPrefix(value);
      green.setPrefix(value);
      blue.setPrefix(value);
    }

    @Override
    public void setId(int value){
      super.setId(value);
      red.setId(value);
      green.setId(value+1);
      blue.setId(value+2);
    }
    
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#setColor(int, int, int)
     */
    public void setColor(int r, int g, int b) {
      color = Color.rgb(r,g,b);
      red.setValue(r);
      green.setValue(g);
      blue.setValue(b);
    }
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#getColor()
     */
    public int getColor() {
      return color;
    }
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#setRed(int)
     */
    public void setRed(int r){
      color = Color.rgb(r, Color.green(color), Color.blue(color));
      red.setValue(r);
    }
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#setGreen(int)
     */
    public void setGreen(int g){
      color = Color.rgb(Color.red(color), g, Color.blue(color));
      green.setValue(g);
    }
    
    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#setBlue(int)
     */
    
    public void setBlue(int b){
      color = Color.rgb(Color.red(color), Color.green(color), b);
      blue.setValue(b);
    }

    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.RgbLed#setColor(int)
     */
    public void setColor(int value) {
      setColor(Color.red(value), Color.green(value), Color.blue(value));
    }
}
