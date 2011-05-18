// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.util;

import android.util.Log;

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
