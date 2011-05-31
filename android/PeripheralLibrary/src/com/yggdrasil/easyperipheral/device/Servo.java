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
package com.yggdrasil.easyperipheral.device;

public interface Servo extends Reservable {

  /**
   * Position of the servo relative to the bounds.
   *
   * @param position
   */
  public void setPosition(float position);

  /**
   * Minimum and maximum bounds for the pulse width in usec.
   *
   * SHOULD default to (1000, 2000) in implementations.
   *
   * @param min
   * @param max
   */
  public void setBounds(int min, int max);

}
