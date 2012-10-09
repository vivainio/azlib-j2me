/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.helyx.basics4me.lang;


public class CharUtil {
	
   public static boolean isWhitespace(char c) {
        // Optimized case for ASCII
        if ((c >= 0x1c && c <= 0x20) || (c >= 0x9 && c <= 0xd)) {
            return true;
        }
        if (c == 0x1680) {
            return true;
        }
        if (c < 0x2000 || c == 0x2007) {
            return false;
        }
        return c <= 0x200b || c == 0x2028 || c == 0x2029 || c == 0x3000;
    }

}

