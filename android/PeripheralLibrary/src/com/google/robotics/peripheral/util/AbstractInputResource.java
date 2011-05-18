package com.google.robotics.peripheral.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author arshan
 * TODO(arshan) : Decide if it makes sense to have the link to Pin in here
 *                would make reserve/release of the pin much easier.
 */
public abstract class AbstractInputResource extends AbstractResource {

	 /*
	  * Simple notifications on change. The concrete implementation has to decide 
	  * when a change has occurred.
	  */
	  private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	  
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
      
	  /**
	   * Override in order to have you object deal with the change notice.
	   */
	  public void onChange()  {
	    notifyListeners();
	  }	  
	  
}
