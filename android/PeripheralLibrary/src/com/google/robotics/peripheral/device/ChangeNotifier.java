// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.device;

import android.os.Handler;

/**
 * 
 * @author arshan@google.com (Arshan Poursohi)
 */
public interface ChangeNotifier {

  public void registerHandler(Handler handler);
  public void unregisterHandler(Handler handler);
  
}
