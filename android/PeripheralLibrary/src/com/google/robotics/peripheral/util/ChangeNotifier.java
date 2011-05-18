// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.util;


/**
 * 
 * @author arshan@google.com (Arshan Poursohi)
 */
public interface ChangeNotifier<T> {
  public void registerListener(ChangeListener<T> listener);
  public void unregisterListener(ChangeListener<T> listener);  
}
