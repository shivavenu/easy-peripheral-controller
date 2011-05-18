package com.google.robotics.peripheral.util;

import com.google.robotics.peripheral.device.Reservable;

public abstract class AbstractResource implements Reservable {

    private boolean reserved = false;
    private boolean operational = true;
    
	public void reserve() {
	  reserved = true;
	}

	public void release() {
		reserved = false;
	}

	public boolean isReserved() {
		return reserved;
	}
	
	public boolean isOperational() {
	    return operational;
	}
	
	public void setOperational(boolean value) {
	  operational = value;
	}

}
