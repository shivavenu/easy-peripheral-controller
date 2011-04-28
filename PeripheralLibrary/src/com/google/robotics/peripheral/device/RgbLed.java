// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;



/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface RgbLed {

  /**
   * Sets the color of the Led.
   *
   * @param c color value defined in android.graphics.Color.
   *
   */
  public void setColor(int c);

  public abstract void setColor(int r, int g, int b);

  public abstract int getColor();

  public abstract void setRed(int r);

  public abstract void setGreen(int g);

  public abstract void setBlue(int b);

}
