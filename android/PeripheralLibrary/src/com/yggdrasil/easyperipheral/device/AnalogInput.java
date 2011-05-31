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

import com.yggdrasil.easyperipheral.util.ChangeNotifier;

/**
 * @author arshan@google.com (Your Name Here)
 *
 */
public interface AnalogInput extends ChangeNotifier {
    /**
     * Analog Value as a percentage of the Reference Voltage between 0.0 and
     * 1.0.
     */
    public abstract float getValue();

    /**
     * Voltage value of the Reference Voltage.  This is the maximum value this
     * Analog Input can read.
     */
    public abstract float getReference();

}
