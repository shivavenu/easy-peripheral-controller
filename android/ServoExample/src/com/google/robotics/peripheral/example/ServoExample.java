package com.google.robotics.peripheral.example;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.robotics.peripheral.connector.AccessoryConnector;
import com.google.robotics.peripheral.connector.ConnectionListener;
import com.google.robotics.peripheral.vendor.google.adk.DemoKit;

public class ServoExample extends Activity {
    private static final String TAG = "ServoExample";
    private DemoKit adk;
    private AccessoryConnector connector;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        connector = new AccessoryConnector(this, DemoKit.ACCESSORY_STRING, connectionListener);        
        setContentView(R.layout.main);
    }
    
    public void onDestroy() {
      connector.disconnect();
      connector = null;
      super.onDestroy();
    }
    
    public void moveServo(View view) {
      if (adk == null) {
        connector.connect();
      }
      else {
        new ServoMover().execute();
      }
    }
    
    @SuppressWarnings("unused")
    private class ServoMover extends AsyncTask<Void, Void, Void> {

      @Override
      protected Void doInBackground(Void... params) {
        float pos = 0;
        while (pos <= 1f && adk != null) {
          adk.getServo(0).setPosition(pos);
          pos += .01;
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return null;
      } 
      
    }
    
    private ConnectionListener connectionListener = new ConnectionListener() {

      public void connected(UsbAccessory accessory) {
        Log.d(TAG, "Connected");        
        adk = new DemoKit(connector);
        adk.getLed(1).setColor(Color.GREEN);
        moveServo(null);
      }

      public void connectionFailed(UsbAccessory accessory) {       
        Log.d(TAG, "Connection Failed");
      }

      public void disconnected() {
        Log.d(TAG, "Disconnected");
        adk = null;        
      }
    };
}