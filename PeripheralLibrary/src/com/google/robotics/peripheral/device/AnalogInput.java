// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface AnalogInput extends ChangeNotifier {
    /**
     * Analog Value as a percentage of the Reference Voltage between 0.0 and
     * 1.0.
     */
    public abstract float getValue();

    /**
     * Voltage value of the Reference Voltage.  This is the maximum value this
     * Analog Input can read.
     */
    public abstract float getReference();

}
