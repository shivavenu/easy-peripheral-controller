package com.google.robotics.peripheral.connector;

import java.io.InputStream;
import java.io.OutputStream;

public interface PeripheralConnector {
	public OutputStream getOutputStream();
	public InputStream getInputStream();
  /**
   * 
   */
  public void disconnect();
}
