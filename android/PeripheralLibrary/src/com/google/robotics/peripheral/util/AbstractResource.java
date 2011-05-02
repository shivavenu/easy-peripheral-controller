package com.google.robotics.peripheral.util;

import com.google.robotics.peripheral.device.Reservable;

public abstract class AbstractResource implements Reservable {

	public void reserve() {
		
	}

	public void release() {
		
	}

	public boolean isReserved() {
	
		return false;
	}

}
