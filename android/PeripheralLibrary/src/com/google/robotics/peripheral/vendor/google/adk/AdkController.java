// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.robotics.peripheral.connector.AccessoryConnector;
import com.google.robotics.peripheral.connector.ConnectionListener;
import com.google.robotics.peripheral.vendor.arduino.ArduinoMega;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Expose an interface of what a controller is expected to support (this might
 * move into a proper interface soon)
 * 
 * Handle some static tasks of catching connections of controller boards, and
 * queueing up classes to
 * 
 * @author arshan@google.com (Arshan Poursohi)
 */

public abstract class AdkController extends ArduinoMega implements ConnectionListener {

  private static final String TAG = "AdkController";

  public static String ACCESSORY_STRING;  // has to be defined by concrete implementations.
  
  InputStream mInputStream;
  OutputStream mOutputStream;
  
  private ConnectionListener mListener;
  
  AccessoryConnector mConnector;
  
  enum AccessoryState {
    CONNECTED, DISCONNECTED, CONNECTING    
  };
  
  private volatile AccessoryState mState = AccessoryState.DISCONNECTED;
  
  /**
   * WARNING: this is still experimental
   * @param context
   * @param listener
   */
  public AdkController(Context context, ConnectionListener listener) {
    mConnector = new AccessoryConnector(context, ACCESSORY_STRING, this);
    mOutputStream = new SnoopedOutput();
    setListener(listener);
    startPolling();
  }
  
  public AdkController(InputStream in,
                       OutputStream out) {
    mInputStream = in;
    mOutputStream = out;       
  }
 
  private  AccessoryConnector getConnector() {
    return mConnector;
  }
  
  protected void setListener(ConnectionListener listener) {
    mListener = listener;
  }
    
  public InputStream getInputStream() {
	  return mInputStream;
  }
  
  public OutputStream getOutputStream() {
	  return mOutputStream;
  }
  
  public class SnoopedOutput extends OutputStream {
    
    OutputStream wrapped;

    public SnoopedOutput() {
      wrapped = null;
    }
    
    public SnoopedOutput(OutputStream wrap) {
      wrapped = wrap;
    }
    
    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int oneByte) throws IOException {
      // Log.d(TAG, "writing : " + oneByte);
      if (wrapped != null) {
        wrapped.write(oneByte);      
      }
    }
   
    public void write(byte[] arr, int offset, int len) throws IOException {
      // logArr(arr, offset, len);
      if (wrapped != null) {
        wrapped.write(arr, offset, len);
      }
    }
    
    public void logArr(byte[] arr, int off, int len) {
      String str = "";
      for (int x = off; x < len+off; x++) {
        str += "0x"+Integer.toHexString(arr[x]&0xFF)+".";                
      }
      Log.d(TAG, str);
    }
  }

  
  /**
   * for debug help
   */
  public void __attach__debug() {
    mOutputStream = new SnoopedOutput(getOutputStream());
  }
  
  /**
   * Override this method to do something when connected.
   */
  public void connected(UsbAccessory accessory) {
    if (mState != AccessoryState.CONNECTING) {
      Log.d(TAG, "connect hook when in " + mState);
      return;
    }
    Log.d(TAG, "Connected");
    mInputStream = mConnector.getInputStream();
    mOutputStream = mConnector.getOutputStream();
    mState = AccessoryState.CONNECTED;
    mListener.connected(accessory);
  }
  
  @Override
  public void connectionFailed(UsbAccessory accessory) {
  }
  
  /**
   * This call back should be made by the handler of the inputstream 
   * when that stream breaks.
   */
  
  public void disconnected() {
   Log.d(TAG, "disconnected");
    mState = AccessoryState.DISCONNECTED;
    
    if (mConnector != null) {
      mConnector.closeIO();
      startPolling();
    }
  }
  

  public void onDestroy() {
    Log.d(TAG, "ADK Destroyed");
    mConnector.onDestroy();
    mConnector = null;
  }

  public boolean isConnected() {
    return mState == AccessoryState.CONNECTED;
  }
  
  public void startPolling() {
    new ConnectionPoll().execute();
  }
  
  public void queueOutputMessage(AdkMessage message) {
    try {
      if (isConnected()) {
        getOutputStream().write(message.toBytes());
      }  
    } catch (IOException e) {
      throw new RuntimeException("Connection to ADK broken");
    }
  }
  
  public class ConnectionPoll extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      Log.d(TAG, "Starting polling task");
      if (mState == AccessoryState.DISCONNECTED) {
        mState = AccessoryState.CONNECTING;
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        while ( mConnector != null && 
               !mConnector.isConnected() && 
               !mConnector.permissionPending()) {
          try {
            // TODO, consider having some backoff here.           
            mConnector.connect();
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      else {
        Log.d(TAG, "Attempt to start poll loop when in " + mState + " state.");
      }
      return null;
    }
    
  }
  
}
