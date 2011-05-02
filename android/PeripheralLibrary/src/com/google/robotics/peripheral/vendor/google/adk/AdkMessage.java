// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

/**
 * Keep some simple info that all 'controlled' devices have to keep track of
 * for the ADK firmware. 
 * 
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AdkMessage {

  private boolean synced = false;
  
  private byte[] message = new byte[3];
  
  public void setPrefix(int val) {
    message[0] = (byte)(val&0xff);
  }
  
  public void setId(int val) {
    message[1] = (byte)(val&0xff);
  }
  
  public void setValue(int val) {
    message[2] = (byte)(val&0xff);
    invalidate();
  }

  public boolean isValid() {
    return synced;
  }
  
  public void invalidate() {
    synced = false;
  }
  
  public void validate() {
    synced = true;
  }
  
  public byte[] getMessage(){
    return message;
  }  
}
