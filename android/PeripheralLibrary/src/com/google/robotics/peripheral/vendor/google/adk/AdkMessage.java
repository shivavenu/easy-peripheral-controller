// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.robotics.peripheral.vendor.google.adk;

import android.util.Log;

import com.google.robotics.peripheral.util.AbstractResource;

/**
 * Keep some simple info that all 'controlled' devices have to keep track of
 * for the ADK firmware. 
 * 
 * @author arshan@google.com (Arshan Poursohi)
 *
 */
public class AdkMessage extends AbstractResource {

  private byte[] message = new byte[3];
  private AdkController mController;

  public AdkMessage(AdkController controller) {
    mController = controller;
  }
  
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

  public void invalidate() {
    mController.queueOutputMessage(this);
  }
  
  public byte[] toBytes(){
    return message;
  }  
}
