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


/**
 * Utility class that represents the range between two integers as 
 * a float range precentage.
 */
public class Range {
  
  private int max;
  private int min;
  private int current;
  
  public Range() {
    max = 100;
    min = -100;
  }
  
  public Range(int min, int max) {
    setBounds(min,max);
  }
  
  public float toFloat() {
    if (max == min) { return 0;}
    return (float)(current-min) / (float)(max-min);
  }
  
  public int getPosition() {
    return current;
  }
  
  public void setPosition(int val) {
    current = val;
  }
  
  public void setRelative(float fl) {
    current = (int)((max-min) * fl) + min;
  }
  
  public void setBounds(int min, int max) {
    this.max = max;
    this.min = min;
  }
  
}
