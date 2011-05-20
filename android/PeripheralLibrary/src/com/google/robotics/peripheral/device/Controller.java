/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.robotics.peripheral.device;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Place holder to catch the commonality between the various controllers. 
 */

public abstract class Controller implements Runnable{

  // Should the controller interface necessarily support the getXXX() methods? 
  public abstract OutputStream getOutputStream();
  public abstract InputStream getInputStream();

}
