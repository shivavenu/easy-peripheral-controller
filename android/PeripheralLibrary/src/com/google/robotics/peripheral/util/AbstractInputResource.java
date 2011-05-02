package com.google.robotics.peripheral.util;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author arshan
 */
public class AbstractInputResource {

	 /*
	  * Simple notifications on change. The concrete implementation had to decide 
	  * when a change has occurred.
	  */
	  private LinkedList<Handler> listeners = new LinkedList<Handler>();
	  
	  protected void notifyListeners() {
		for (Handler listener : listeners ){
	      listener.sendMessage(Message.obtain(listener, 1, this));
	    }
	  }
	  
	  public void registerHandler(Handler handler) {
	    listeners.add(handler);
	  }
	  
	  public void unregisterHandler(Handler handler) {
	    listeners.remove(handler);
	  }
}
