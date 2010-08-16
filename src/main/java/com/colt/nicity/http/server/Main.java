/*
 * GetFileMethod.java
 *
 * Created on Aug 15, 2010, 7:25:36 PM
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

import com.colt.nicity.http.server.methods.GetFileMethod;
import com.colt.nicity.http.server.methods.ListFileMethod;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        int timeout = 10000;
        int maxNumberOfRequestHandlers = 5;
        int port = 9090;
        HttpServer server = new HttpServer(new File("C:/"), new File("C:/log.txt"), timeout, maxNumberOfRequestHandlers, port);
        server.addMethod("ls", new ListFileMethod());
        server.addMethod("cp", new GetFileMethod());
        server.start();
    }
}
