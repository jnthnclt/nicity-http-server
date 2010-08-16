/*
 * ListFileMethod.java
 *
 * Created on Aug 15, 2010, 6:10:43 PM
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
public class ListFileMethod extends HttpMethod {

    @Override
    public void invoke(File root,Map<String, String> args,PrintStream ps) throws IOException {
        String directoryName = args.get("directoryName");
        listDirectory(root,directoryName, ps);
        
    }
    
    void listDirectory(File root,String directoryName, PrintStream ps) throws IOException {

        File dir = null;
        if (directoryName != null) dir = new File(root, directoryName);
        else dir = root;

        if (!dir.exists()) {
            ps.print("HTTP/1.0 " + HttpConstants.cNOT_FOUND + " not found");
            ps.write(EOL);
        }
        else {
            ps.print("HTTP/1.0 " + HttpConstants.cOK + " OK");
            ps.write(EOL);
        }
        ps.print("Server: Simple java");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);
        ps.print("Content-type: text/html");
        ps.write(EOL);

        ps.println("<TITLE>Directory listing</TITLE><P>\n");
        ps.println("<A HREF=\"ls?directoryName="+directoryName+"/\">Parent Directory</A><BR>\n");
        String[] list = dir.list();
        for (int i = 0; list != null && i < list.length; i++) {
            File f = new File(dir, list[i]);
            String relativeName = f.getAbsolutePath().substring(root.getAbsolutePath().length());
            relativeName = relativeName.replace(File.separatorChar, '/');
            if (f.isDirectory()) {
                ps.println("<A HREF=\"ls?directoryName=" + relativeName + "/\">" + list[i] + "/</A><BR>");
            }
            else {
                ps.println("<A HREF=\"cp?fileName=" + relativeName + "\">" + list[i] + "</A><BR");
            }
        }
        ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
    }
}
