package android.hardware;

/**
 * A hook into the android sensor system.
 * @author arshan
 *
 */
public class PeripheralSensor extends Sensor {
	
	public SensorEvent newSensorEvent() {
		return new SensorEvent();
	}
	
	public void registerListener(SensorEventListener listener, int sensorType, int period) {
		
	}
}
