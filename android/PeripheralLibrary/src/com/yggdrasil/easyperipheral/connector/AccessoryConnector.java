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
 */
package com.yggdrasil.easyperipheral.connector;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Considered implementing this as a Service of some sort, but since the
 * underlying usbaccesssory is already a service seemed unneccessary to layer
 * another on top. This is simply a utility that deals with the usbaccessory
 * service in the context of the Peripheral Library.
*/
public class AccessoryConnector implements PeripheralConnector {

  private static final String TAG = "AcccessoryConnector";

  // Intents
  private PendingIntent mPermissionIntent;
  private boolean mPermissionRequestPending;
  private String mUsbPermissionString;

  private ConnectionListener mListener;

  // Usb accessory classes
  private UsbManager mUsbManager;
  UsbAccessory mAccessory;

  // IO
  ParcelFileDescriptor mFileDescriptor = null;
  FileInputStream mInputStream;
  FileOutputStream mOutputStream;

  Context mContext;

  private int failCnt = 0;
  
  public AccessoryConnector(Context context, String acc_string, ConnectionListener listener) {
    mContext = context;
    mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    mListener = listener;
    setAccessoryString(acc_string);
    
    IntentFilter filter = new IntentFilter(mUsbPermissionString);
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
    context.registerReceiver(mUsbReceiver, filter);
  }

  public void setAccessoryString(String str) {
    mUsbPermissionString = str + ".action.USB_PERMISSION";
  }

  private UsbAccessory getAccessory() {
    // First, see if there are any accessories attached at all.
    UsbAccessory[] accessories = mUsbManager.getAccessoryList();
    return (accessories == null ? null : accessories[0]);
  }

  public boolean isConnected() {
    return mFileDescriptor != null;
  }

  public void connect() {

    if (isConnected()) {
      Log.d(TAG, "already connected");
      mListener.connected(mAccessory);
      return;
    }

    mAccessory = getAccessory();
    if (mAccessory == null) {
      mListener.connectionFailed(null);
      return;
    }

    try {
      if (mUsbManager.hasPermission(mAccessory)) {
        closeIO();
        openIO(mAccessory);
      } else {
        mPermissionIntent =
            PendingIntent.getBroadcast(mContext, 0, new Intent(mUsbPermissionString), 0);
        // Fires a dialog to get permission from the user. Result will come
        // back to the mUsbReceiver.
        synchronized (mUsbReceiver) {
          if (!mPermissionRequestPending) {
            mUsbManager.requestPermission(mAccessory, mPermissionIntent);
            mPermissionRequestPending = true;
          }
        }
      }
    } catch (IllegalArgumentException e) {
      // This is thrown by UsbManager if no accessory is connected
      mListener.connectionFailed(null);
    }
  }

  private void doDisconnect() {
    mListener.disconnected();
    closeIO();
  }
   
  public void onDestroy() {
    mContext.unregisterReceiver(mUsbReceiver);
    closeIO();
  }

  public FileOutputStream getOutputStream() {
    return mOutputStream;
  }

  public FileInputStream getInputStream() {
    return mInputStream;
  }
  
  public boolean permissionPending() {
    return mPermissionRequestPending;
  }

  private void openIO(UsbAccessory accessory) {
    Log.d(TAG, "openAccessory: " + accessory);
    mFileDescriptor = mUsbManager.openAccessory(accessory);
    if (mFileDescriptor != null) {
      
      FileDescriptor fd = mFileDescriptor.getFileDescriptor();
      mInputStream = new FileInputStream(fd);
      mOutputStream = new FileOutputStream(fd);
      
      mListener.connected(accessory);
      failCnt = 0;
      Log.d(TAG, "openAccessory succeeded");
    } else {
      mListener.connectionFailed(accessory);
      Log.d(TAG, "openAccessory fail");
      
      closeIO();
      if (failCnt++ > 10) {
        throw new RuntimeException("Too many failures trying to connect, bailing.");
      }
      if (accessory.getManufacturer() == null) {
        throw new RuntimeException("Appears to be hung on the usb accessory handle");
      }
    }
  }

  /*
   * Close all objects having to do with the connection to the filesystem.
   */
  public void closeIO() {
    try {
      if (mFileDescriptor != null) {
        mFileDescriptor.close();
        mInputStream.close();
        mOutputStream.close();
      }
    } catch (IOException e) {
    } finally {
      mFileDescriptor = null;
      mInputStream = null;
      mOutputStream = null;
    }
  }

  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (mUsbPermissionString.equals(action)) {
        synchronized (this) {
          UsbAccessory[] accessoryArray = mUsbManager.getAccessoryList();
          if (accessoryArray == null) {
            Log.d(TAG, "usb accessory returned null array");
          }
          if (accessoryArray != null && 
              accessoryArray.length == 1) {
            UsbAccessory accessory = accessoryArray[0];

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
              Log.d(TAG, "I gots mad permission");
              openIO(accessory);
            } else {
              Log.d(TAG, "permission denied for accessory " + accessory);
              mListener.connectionFailed(accessory);
            }
            mPermissionRequestPending = false;
          } else {
            Log.d(TAG, "Unexpected accessory list size : " + 
              (accessoryArray == null? null : accessoryArray.length));
          }
        }
      } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
        Log.d(TAG, "detach");
        doDisconnect();
      } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
        Log.d(TAG, "attach");
        // I try and I try, but never do get this.
      }
    }
  };

  /* (non-Javadoc)
   * @see com.google.robotics.peripheral.connector.PeripheralConnector#disconnect()
   */
  @Override
  public void disconnect() {
    Log.d(TAG, "DISCONNECT CALLED !@^%#$");
  }
}
