package com.google.robotics.peripheral.connector;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.robotics.peripheral.device.Controller;

public interface PeripheralConnector {
	public OutputStream getOutputStream();
	public InputStream getInputStream();
	public void disconnected(Controller demoKit);
}
