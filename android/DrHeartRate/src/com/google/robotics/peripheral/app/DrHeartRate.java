package com.google.robotics.peripheral.app;

import android.R;
import android.app.Activity;
import android.hardware.usb.UsbAccessory;
import android.os.Bundle;

import com.yggdrasil.easyperipheral.connector.ConnectionListener;
import com.yggdrasil.easyperipheral.vendor.google.drawesome.DrAwesome;

import java.io.IOException;

public class DrHeartRate extends Activity {
  
    public static final String TAG = "DrHeartRate";
    
    private DrAwesome drAwesome;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drAwesome = new DrAwesome(this, connectionListener);
        setContentView(R.layout.main);
    }

    Thread monitor;
    
    public void startMonitor() {
      monitor = new Thread() {
        public void run() {
          try {
            HrmiMonitor hrmi = new HrmiMonitor(drAwesome.getTwiChannel());
            
            
            
          } catch (IOException e) {
            
            e.printStackTrace();
          }
        }
      };
      monitor.start();
    }
    
    public void stopMonitor() {
      System.exit(0); // could be slicker :)
    }
    
    ConnectionListener connectionListener = new ConnectionListener() {

      @Override
      public void connected(UsbAccessory accessory) {
        startMonitor();
      }

      @Override
      public void connectionFailed(UsbAccessory accessory) {
     
      }

      @Override
      public void disconnected() {
        stopMonitor();
      }
      
    };
   
}