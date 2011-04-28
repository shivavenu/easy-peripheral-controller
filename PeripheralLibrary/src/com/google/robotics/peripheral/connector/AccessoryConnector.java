package com.google.robotics.peripheral.connector;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.robotics.peripheral.device.Controller;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Considered implementing this as a Service of some sort, but since the 
 * underlying usbaccesssory is already a service seemed unneccessary to 
 * layer another on top. This is simply a utility that deals with the 
 * usbaccessory service in the context of the Peripheral Library.
 * 
 * @author arshan
 *
 */
public class AccessoryConnector implements PeripheralConnector {

	private static final String TAG = "AccessoryWrapper";

	// Intents
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	private String mUsbPermissionString;

	// Usb accessory classes;
	private UsbManager mUsbManager;
	UsbAccessory mAccessory;

	// IO
	ParcelFileDescriptor mFileDescriptor = null;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	Context mContext;
	
	private boolean mExitOnDetach = true;
	
	public AccessoryConnector(Context context, String acc_string) {
		mContext = context;
		mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		setAccessoryString(acc_string);
	}
	

	public void setAccessoryString(String str) {
		mUsbPermissionString = str + ".action.USB_PERMISSION";
	}

	public UsbAccessory getAccessory() {
		// First, see if there are any accessories attached at all.
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		return (accessories == null ? null : accessories[0]);
	}

	public boolean isConnected() {
		return mFileDescriptor != null;
	}
	
	
	public boolean connect() {

		mAccessory = getAccessory();
		if (mAccessory == null) {
			return false;
		}

		if (mUsbManager.hasPermission(mAccessory)) {
			closeIO();
			openIO(mAccessory);
		} else {
			mPermissionIntent = PendingIntent.getBroadcast(
					mContext, 0, new Intent(mUsbPermissionString), 0);
			// Fires a dialog to get permission from the user. Result will come
			// back to the mUsbReceiver.
			synchronized (mUsbReceiver) {
				if (!mPermissionRequestPending) {
					mUsbManager.requestPermission(mAccessory, mPermissionIntent);
					mPermissionRequestPending = true;
				}
			}
		}

		return mAccessory == null;
	}

	public void disconnect() {
		closeIO();
	}
	
	private void openIO(UsbAccessory accessory) {
		Log.d(TAG, "openAccessory: " + accessory);
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Log.d(TAG, "openAccessory succeeded");
		} else {
			Log.d(TAG, "openAccessory fail");
			if (accessory.getManufacturer() == null) {
				throw new RuntimeException("Appears to be hung on the usb accessory handle");
			}
		}
	}

	public void closeIO() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mInputStream = null;
			mOutputStream = null;
		}
	}

	public FileOutputStream getOutputStream() {
		return mOutputStream;
	}
	
	public FileInputStream getInputStream() {
		return mInputStream;
	}
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (mUsbPermissionString.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = mUsbManager.getAccessoryList()[0];
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openIO(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				Log.d(TAG, "detach");
				closeIO();
				if (mUsbReceiver != null) {
					mContext.unregisterReceiver(mUsbReceiver);
				}
				
				if (mExitOnDetach) {
					// This is a hack to deal with bad tear down badness.
				//	System.exit(0); 
				}

			}
		}
	};

	public void disconnected(Controller demoKit) {
		disconnect();
	}

}
