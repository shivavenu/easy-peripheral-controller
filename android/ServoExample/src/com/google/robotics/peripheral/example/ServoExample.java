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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        adk = new DemoKit(this, connectionListener);      
      
        setContentView(R.layout.main);
    }
    
    public void onDestroy() {
      Log.d(TAG, "onDestroy");
      adk.onDestroy();
      super.onDestroy();
    }
    
    public void moveServo(View view) {
      if (adk.isConnected()) {
        new ServoMover().execute();
      }
      else {
        Log.d(TAG, "cant move servo until we're connected");
      }
    }
    
    @SuppressWarnings("unused")
    private class ServoMover extends AsyncTask<Void, Void, Void> {

      @Override
      protected Void doInBackground(Void... params) {
        float pos = 0;
        Log.d("servo mover", "moving servo");
        while (pos <= 1f && adk != null) {
          adk.getServo(0).setPosition(pos);
          adk.getServo(1).setPosition(pos);
          pos += .01;
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        adk.getServo(0).setPosition(.5f);
        adk.getServo(1).setPosition(.5f);
        return null;
      } 
      
    }
    
    /**
     * Respond to changes in the connection state of the DemoKit.
     */
    private ConnectionListener connectionListener = new ConnectionListener() {
      public void connected(UsbAccessory accessory) {
        Log.d(TAG, "Connected");        
        adk.getLed(1).setColor(Color.GREEN);
        adk.getServo(0).setPosition(.5f);
        adk.getServo(1).setPosition(.5f);
      }

      public void connectionFailed(UsbAccessory accessory) {       
        Log.d(TAG, "Connection Failed");
      }

      public void disconnected() {
        Log.d(TAG, "Disconnected");
      }
    };
}