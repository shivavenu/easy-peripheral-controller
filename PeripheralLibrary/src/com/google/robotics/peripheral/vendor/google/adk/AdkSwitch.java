// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import com.google.robotics.peripheral.device.Switch;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public class AdkSwitch implements Switch {

    private boolean state = false;
    
    public AdkSwitch(AdkController controller) {
      
    }
    
    public void setClosed(boolean val) {
      state = val;
    }


    /* (non-Javadoc)
     * @see com.google.robotics.peripheral.Switch#isClosed()
     */
    
    public boolean isClosed() {
      // TODO Auto-generated method stub
      return state;
    }

}
