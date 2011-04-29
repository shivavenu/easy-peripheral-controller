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
  
  List<AdkMessage> controlledDevices = new LinkedList<AdkMessage>();
  private final Object controlledDevicesLock;
  DeviceSync dSync;

  private Handler callbackHandler;
  
  PeripheralConnector mConnector;
  
  public AdkController(PeripheralConnector connector) {
	  this(connector.getInputStream(), connector.getOutputStream());
	  mConnector = connector;
  }
  
  public AdkController(InputStream in,
                       OutputStream out) {
    controlledDevicesLock = new Object(); 
    mInputStream = in;
    mOutputStream = out;
    
    dSync = new DeviceSync();
    dSync.start();
    
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
   */
  public void onDisconnected() {
    

    mConnected = false;
	    
	if (callbackHandler != null) {
      callbackHandler.sendEmptyMessage(DISCONNECTED);
    }
    
    if (mConnector != null) {
    	Log.d(TAG, "disconnecting the peripheral connector");
    	// TODO (*arshan) this might have to come back.
    	mConnector.disconnect();
    }
    // cheap hack, but causes the demokit to exit, which allows
    // a nice clean one on the next round.
    // System.exit(0); // maybe make into an option
    // return;
  }
  
 
  
  public void onDestroy() {
    Log.d(TAG, "ADK Destroyed");
   
    try {
      if (dSync != null ) {
        dSync.join();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  public boolean isConnected() {
    return mConnected;
  }

  public void register(AdkMessage device) {
    synchronized (controlledDevicesLock) {
      controlledDevices.add(device);
    }    
  }
 
 public void unregister(AdkMessage device) {
   synchronized (controlledDevicesLock) {
     controlledDevices.remove(device);    
   }
 }
  


  

  /**
   * An outgoing spooler thread, that scans the registered peripherals and 
   * sends a control packet for any that have been marked as changed (isInvalid())
   * 
   * Can throttle bandwidth _to_ the controller board here.
   *
   */
  public class DeviceSync extends Thread{
    
    private boolean running = true;
    private int failures = 0;
    
    @Override
    public void run() {      
      while (running) {
        try {
          if (mOutputStream != null) {
          // Clearly this can be optimized. so do it.
            synchronized (controlledDevicesLock) {
              for (AdkMessage device : controlledDevices) {
                if (!device.isValid()) {
                  // Log.d(TAG, "sending control msg : " + printBytes(device.getMessage()));
                  mOutputStream.write(device.getMessage());
                  device.validate();
                  failures = 0;
                }
              }              
            }
          }
        //  Thread.yield();
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
          // there seems to be a few of these at init, before the device is
          // setup maybe? ... I must be using the wrong signal to start
          //if (failures++ > 10){
            running = false;
            Log.d(TAG, "exiting due to channel breaking");
            onDisconnected();
          //}
        }
      }
    }
  }
  
  // for debug only, erase me.
  public String printBytes(byte[] arr) {
    String result = "[";
    for (byte x : arr) {
      result += " "+x;
    }
    result += "]";
    return result;
  }
  
}
