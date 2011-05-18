#include <stdio.h>
#include <Wire.h>
#include <Max3421e.h>
#include <Max3421e_constants.h>
#include <Max_LCD.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <Servo.h>
#include <ADK.h>

AndroidAccessory acc(
   "Google, Inc.",
   "DrAwesome",
   "DemoKit Arduino Board",
   "0.1",
   "http://www.android.com",
   "0000000012345678");
   

ADK adk(acc);

void setup() {
  Serial.begin(115200);
//  adk.enableLogging(true);
//  adk.setLoggingOutput(Serial);

  acc.powerOn();
  adk.init();

  
  // Show a sign of life.
  pinMode(13, OUTPUT);
}

bool amConnected = false;
void loop() {

  
  if (acc.isConnected()) {
    // so this is broken because ... 
    // the check for input blocks til char comes in
    // so we are heartbeating the thing from android space. 
    // have to change this to interrupt out or some such.
 
    adk.checkForInput();
    digitalWrite(13, LOW);
  }
  else {
   
    digitalWrite(13, HIGH);
    delay(100);
  }
  
//  if (acc.isConnected()) {
//    if (!amConnected) {
//      Serial.println("at long last I am connected. :)");
//      amConnected = true;
//    }
//    adk.checkForInput();
//    delay(500);
//  } else {
//    if (amConnected) {
//      Serial.println("my connection is no more. :(");
//      amConnected = false;
//    }
//  }
}
