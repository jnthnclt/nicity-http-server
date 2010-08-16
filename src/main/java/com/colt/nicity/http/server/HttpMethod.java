/*
 * HttpMethod.java
 *
 * Created on Aug 15, 2010, 6:47:47 PM
 *
 * Copyright Aug 15, 2010 Jonathan Colt 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.colt.nicity.http.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;



/**
*
* @author Jonathan Colt
*/
abstract public class HttpMethod {
    static final protected byte[] EOL = {(byte) '\r', (byte) '\n'};

    abstract public void invoke(File root,Map<String,String> args,PrintStream out) throws IOException;
}
