package com.google.robotics.peripheral.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.usb.UsbAccessory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.robotics.peripheral.connector.AccessoryConnector;
import com.google.robotics.peripheral.connector.ConnectionListener;
import com.google.robotics.peripheral.device.Joystick;
import com.google.robotics.peripheral.device.LightSensor;
import com.google.robotics.peripheral.vendor.google.adk.DemoKit;

public class KitchenSinkExample extends Activity {
  public static final String TAG = "ServoExample";

  private volatile Point lastTouch = new Point(500,500);
  Paint mainText = new Paint();

  private TestView mTestView;
  private DemoKit adk;
  private AccessoryConnector connector;

  /** Called when the activity is first created.; */
  @Override
  public void onCreate(Bundle savedInstanceState) { 

    super.onCreate(savedInstanceState);

    mTestView = new TestView(this);
    setContentView(mTestView);
    mTestView.requestFocus();
    
    connector = new AccessoryConnector(this, DemoKit.ACCESSORY_STRING, connectionListener);
    
    logIt("created");
  }

  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    connector.disconnect();
    connector = null;
    super.onDestroy();
  }
  

  class TestView extends View implements OnTouchListener {

    private String text;

    /**
     * @param context
     */
    public TestView(Context context) {
      super(context);

      setFocusable(true);
      setFocusableInTouchMode(true);

      this.setOnTouchListener(this);

      mainText.setColor(Color.RED);
      mainText.setAntiAlias(true);
      mainText.setStrokeWidth(6);
      text = "no  touchy";
    }

    @Override
    protected void onDraw(Canvas canvas) {
      canvas.drawText(text, 50, 50, mainText);
      canvas.drawCircle(lastTouch.x, lastTouch.y, 30, mainText);
    }
    
    public void moveIt(int x , int y) {
      lastTouch.x = x;
      lastTouch.y = y;
      
      // change the on screen message
      text = "touched " + lastTouch.x + " x " + lastTouch.y;
      invalidate();
      
      if (adk == null) {
    	  // Polling for a connected UsbAccessory.
    	  connector.connect();
      } else {
    	 
        float xfactor = (float) (lastTouch.x / 1280f);
        float yfactor = (float) (lastTouch.y / 720f);

        adk.getServo(0).setPosition(xfactor);
        adk.getServo(1).setPosition(yfactor);

        adk.getLed(0).setBlue((int) (255 * xfactor));
        adk.getLed(2).setGreen((int) (255 * yfactor));
      }
    }

    public boolean onTouch(View view, MotionEvent event) {
      // if(event.getAction() != MotionEvent.ACTION_DOWN)
      // return super.onTouchEvent(event);
      moveIt((int) event.getX(), (int) event.getY());    
      return true;
    }
  }


  private ConnectionListener connectionListener = new ConnectionListener() {

    public void connected(UsbAccessory accessory) {
      logIt("connected");
      adk = new DemoKit(connector);
      /*
       * PeripheralManager.getInstance().registerListener( lightEventListener,
       * PeripheralManager.getDefault(PeripheralSensor.TYPE_LIGHT),
       * PeripheralManager.SENSOR_DELAY_NORMAL );
       */
      // adk.getServo(0).setBounds(600, 240);
      adk.getRelay(0).setValue(true); // if we want to use for on switch.
      
      adk.getLightSensor().registerHandler(lightSensor);
      adk.getJoystick().registerHandler(new JoystickHandler(mTestView));      
    }

    public void connectionFailed(UsbAccessory accessory) {
      logIt("connection Failed");
    }

    public void disconnected() {
      adk = null;
      logIt("disconnected");
    }
    
  };
  
  Handler lightSensor = new Handler() {
    public void handleMessage(Message msg) {
      LightSensor sensor = (LightSensor)(msg.obj);
      if (adk != null) {
        int val = sensor.getLux() >> 3;
        adk.getLed(1).setColor(val, val, val);
      
      }
    }
  };
  
  public class JoystickHandler extends Handler {
    TestView mView;
    
    public JoystickHandler(TestView view) {
      mView = view;
    }
    public void handleMessage(Message msg) {      
      Joystick jstick = (Joystick)(msg.obj);
      mView.moveIt((int)(jstick.getX()*1280), (int)(jstick.getY()*960));
      Log.d(TAG, "jstick : " + jstick.getX() + "x" + jstick.getY());
      mView.invalidate();
    }
  };

  // Utility methods.
  ///////////////////////

  long lastToast = 0;
  long maxToast = 500; // only one every half second ...

  // Put a toast up, but only if there has been no other in the last
  // @maxToast milliseconds.
  public void toastIt(String msg) {
    if ((System.currentTimeMillis() - lastToast) > maxToast) {
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      lastToast = System.currentTimeMillis();
    } else {
      logIt("not toasting : " + msg);
    }
  }

  public void logIt(String msg) {
    Log.d(TAG, msg);
  }
}
