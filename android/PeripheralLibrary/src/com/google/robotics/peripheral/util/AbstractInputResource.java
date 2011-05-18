package com.google.robotics.peripheral.util;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author arshan
 */
public abstract class AbstractInputResource {

	 /*
	  * Simple notifications on change. The concrete implementation has to decide 
	  * when a change has occurred.
	  */
	  private LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();
	  
	  protected void notifyListeners() {
		for (ChangeListener listener : listeners ){
	      listener.onChange(this);
	    }
	  }
	  
	  public void registerListener(ChangeListener handler) {
	    listeners.add(handler);
	  }
	  
	  public void unregisterListener(ChangeListener handler) {
	    listeners.remove(handler);
	  }
}
