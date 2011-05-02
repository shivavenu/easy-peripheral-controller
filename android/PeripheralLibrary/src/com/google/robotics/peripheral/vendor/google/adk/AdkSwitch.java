// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.Switch;
import com.google.robotics.peripheral.util.AbstractInputResource;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkSwitch extends AbstractInputResource implements Switch {

    private boolean state = false;
    
    public AdkSwitch(AdkController controller) {      
    }
    
    public void setClosed(boolean val) {
      if (val != state) {
    	state = val;
      	notifyListeners();
      }
    }

	public boolean isClosed() {
		return state;
	}
}
