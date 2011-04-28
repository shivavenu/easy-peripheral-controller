package com.google.robotics.peripheral.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.PeripheralManager;
import android.hardware.PeripheralSensor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.robotics.peripheral.connector.AccessoryConnector;
import com.google.robotics.peripheral.vendor.google.DemoKit;

public class ServoExample extends Activity {
	public static final String TAG = "JustConnect";
	  
	  private volatile Point lastTouch = new Point();
	  Paint mainText = new Paint();
	    
	  private TestView mTestView;
	  private DemoKit adk;
	  private AccessoryConnector connector;
	  
	  /** Called when the activity is first created.; */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		boolean justbuttons = false;

		mTestView = new TestView(this);
		setContentView(mTestView);
		mTestView.requestFocus();

		connector = new AccessoryConnector(this, DemoKit.ACCESSORY_STRING);

		logIt("created");
	}

	  public void onResume(){
	    super.onResume();
	    connect(null);
	    // toastIt("resumed");
	  }
	  
	  public void connect(View view) {    
		  if (! connector.isConnected()) {
		    	if (!connector.connect()) {
					toastIt("I gots no accessory ... sux for you");
				}
		  }
	  }  
	  

	  public void disconnect(View view) {
		connector.disconnect();	
		connector = null;
	    if (adk != null) {
	      adk.onDisconnected();
	      adk = null;	    
	    }
	  }
	  
	  public void forceClose(View view) {
	    disconnect(view);
	    finish();
	    System.exit(1);
	  }

	  long lastToast = 0;
	  long maxToast = 500; // only one every half second ... 
	  
	  public void toastIt(String msg) {
	    if ((System.currentTimeMillis() - lastToast) > maxToast ) {
	      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	      lastToast = System.currentTimeMillis();
	    }
	    else {
	      logIt("not toasting : " + msg);
	    }
	  }
	  
	  public void logIt(String msg) {
	    Log.d(TAG, msg);
	  }

	  public void onPause() {
		  // commenting this force closes ... we want that ??
		  super.onPause();		
	  }
	  
	
		  
	  private SensorEventListener lightEventListener = new SensorEventListener(){
		public void onAccuracyChanged(Sensor sensor, int arg1) {
		}

		public void onSensorChanged(SensorEvent event) {
			float lightlevel = event.values[0];
			mainText.setARGB(255, (int)(lightlevel*255), 0, 0);
		}
		  
	  };
	  
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
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	      canvas.drawText(text, 50, 50, mainText);
	      canvas.drawCircle(lastTouch.x, lastTouch.y, 30, mainText);
	    }

	    public boolean onTouch(View view, MotionEvent event) {
	      // if(event.getAction() != MotionEvent.ACTION_DOWN)
	      // return super.onTouchEvent(event);

	      lastTouch.x = (int) event.getX();
	      lastTouch.y = (int) event.getY();

	      text = 
	        "touched " + lastTouch.x + " x " + lastTouch.y;

	      invalidate();
	      
	      connect(null);
	      
	      if( connector.isConnected() ){  
	    	  if (adk == null ) {
	    		  adk = new DemoKit(connector);

	    		  /*
	    		  PeripheralManager.getInstance().registerListener(
	    				lightEventListener,
	    				PeripheralManager.getDefault(PeripheralSensor.TYPE_LIGHT),
	    				PeripheralManager.SENSOR_DELAY_NORMAL
	    		  		);
	    		  */
	        	  //adk.getServo(0).setBounds(600, 240);
	    		  adk.getRelay(0).setValue(true); // if we want to use for on switch.
	    	  }
	    	  
	    	  float xfactor = (float)(lastTouch.x/1280f);
	    	  float yfactor = (float)(lastTouch.y/720f);
	    	  
	    	  adk.getServo(0).setPosition(xfactor);
	    	  adk.getServo(1).setPosition(yfactor);

	    	  adk.getLed(0).setBlue((int)(255*xfactor));
	    	  adk.getLed(2).setGreen((int)(255*yfactor));
	    	  
	      } 
	      
	      return true;
	    }
	  }
}