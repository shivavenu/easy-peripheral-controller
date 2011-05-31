/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */package com.yggdrasil.easyperipheral.util;

import java.util.ArrayList;
import java.util.List;

/**
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
