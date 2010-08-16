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


package com.colt.nicity.http.server.methods;

import com.colt.nicity.http.server.HttpConstants;
import com.colt.nicity.http.server.HttpMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/**
*
* @author Jonathan Colt
*/
public class GetFileMethod extends HttpMethod {
     /* mapping of file extensions to content-types */
    static HashMap<String,String> map = new HashMap<String, String>();
    
    static {
        fillMap();
    }

    static void setSuffix(String k, String v) {
        map.put(k, v);
    }

    static void fillMap() {
        setSuffix("", "content/unknown");
        setSuffix(".uu", "application/octet-stream");
        setSuffix(".exe", "application/octet-stream");
        setSuffix(".ps", "application/postscript");
        setSuffix(".zip", "application/zip");
        setSuffix(".sh", "application/x-shar");
        setSuffix(".tar", "application/x-tar");
        setSuffix(".snd", "audio/basic");
        setSuffix(".au", "audio/basic");
        setSuffix(".wav", "audio/x-wav");
        setSuffix(".gif", "image/gif");
        setSuffix(".jpg", "image/jpeg");
        setSuffix(".jpeg", "image/jpeg");
        setSuffix(".htm", "text/html");
        setSuffix(".html", "text/html");
        setSuffix(".text", "text/plain");
        setSuffix(".c", "text/plain");
        setSuffix(".cc", "text/plain");
        setSuffix(".c++", "text/plain");
        setSuffix(".h", "text/plain");
        setSuffix(".pl", "text/plain");
        setSuffix(".txt", "text/plain");
        setSuffix(".java", "text/plain");
    }

    @Override
    public void invoke(File root,Map<String, String> args,PrintStream ps) throws IOException {
        String fileName = args.get("fileName");
        File file = new File(root, fileName);
        if (file.exists()) {
            sendFile(file, ps);
        }
    }

    byte[] sendBuffer = new byte[2048];
    void sendFile(File file, PrintStream ps) throws IOException {
        ps.print("HTTP/1.0 " + HttpConstants.cOK + " OK");
        ps.write(EOL);
        ps.print("Server: Simple java");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);
        ps.print("Content-length: " + file.length());
        ps.write(EOL);
        ps.print("Last Modified: " + (new Date(file.lastModified())));
        ps.write(EOL);
        String name = file.getName();
        int ind = name.lastIndexOf('.');
        String ct = null;
        if (ind > 0) {
            ct = map.get(name.substring(ind).toLowerCase());
        }
        if (ct == null) {
            ct = "unknown/unknown";
        }
        ps.print("Content-type: " + ct);
        ps.write(EOL);
        InputStream is = null;
        is = new FileInputStream(file.getAbsolutePath());
        ps.write(EOL);
        try {
            int n;
            while ((n = is.read(sendBuffer)) > 0) {
                ps.write(sendBuffer, 0, n);
            }
        }
        finally {
            is.close();
        }
    }

   


   
}
