// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.util.Log;

import com.google.robotics.peripheral.connector.PeripheralConnector;
import com.google.robotics.peripheral.device.Controller;

/**
 * Expose an interface of what a controller is expected to support (this might
 * move into a proper interface soon)
 * 
 * Handle some static tasks of catching connections of controller boards, and
 * queueing up classes to
 * 
 * @author arshan@google.com (Arshan Poursohi)
 */

public abstract class AdkController extends Controller {

  private static final String TAG = "AdkController";

  public static final int CONNECTED = 0;
  public static final int DISCONNECTED = 1;

  InputStream mInputStream;
  OutputStream mOutputStream;
  
  protected boolean mConnected = false;

  private Handler callbackHandler;
  
  PeripheralConnector mConnector;
  
  public AdkController(PeripheralConnector connector) {
	  this(connector.getInputStream(), connector.getOutputStream());
	  mConnector = connector;
  }
  
  public AdkController(InputStream in,
                       OutputStream out) {
    mInputStream = in;
    mOutputStream = out;    
   
    mConnected = true;
  }
 
  public InputStream getInputStream() {
	  return mInputStream;
  }
  
  public OutputStream getOutputStream() {
	  return mOutputStream;
  }

  /**
   * Override this method to do something when connected.
   */
  public void onConnected() {
    if (callbackHandler != null) {
      callbackHandler.sendEmptyMessage(CONNECTED);
    }
  }
  
  /**
   * This call back should be made by the handler of the inputstream 
   * when that stream breaks.
   */
  public void onDisconnected() {
    mConnected = false;
	    
	if (callbackHandler != null) {
      callbackHandler.sendEmptyMessage(DISCONNECTED);
    }
    
    if (mConnector != null) {
    	Log.d(TAG, "disconnecting the peripheral connector");
    	mConnector.disconnect();
    }   
  }

  public void onDestroy() {
    onDisconnected();
    Log.d(TAG, "ADK Destroyed");
  }

  public boolean isConnected() {
    return mConnected;
  }
}
