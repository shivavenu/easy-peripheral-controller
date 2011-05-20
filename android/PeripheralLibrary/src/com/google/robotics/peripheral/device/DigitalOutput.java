// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface DigitalOutput extends Reservable {
    
    public abstract void setValue(boolean value);

}
