/*
 * HttpRequestHandler.java
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

package com.colt.nicity.http.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler implements Runnable {

    final HttpServer server;
    final static int BUF_SIZE = 2048;
    static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    byte[] requestBuffer;
    private Socket socketToClient;

    public HttpRequestHandler(HttpServer server) {
        this.server = server;
        requestBuffer = new byte[BUF_SIZE];
        socketToClient = null;
    }

    synchronized void setRequestingSocket(Socket socketToClient) {
        this.socketToClient = socketToClient;
        notify();
    }

    @Override
    public synchronized void run() {
        while (true) {
            if (socketToClient == null) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    continue;
                }
            }
            try {
                handleClientRequest();
            }
            catch (Exception e) {
                server.log(e.toString());// todo
                for(StackTraceElement ste:e.getStackTrace()){
                    server.log(ste.toString());
                }
            }

            socketToClient = null;
            if (server.requestHandlers.size() >= server.maxNumberOfRequestHandlers) {
                return;
            }
            else {
                server.requestHandlers.add(this);
            }
        }
    }

    void handleClientRequest() throws IOException {
        InputStream is = new BufferedInputStream(socketToClient.getInputStream());
        PrintStream ps = new PrintStream(socketToClient.getOutputStream());
        // client has server.timeout to produce a request
        socketToClient.setSoTimeout(server.timeout);
        socketToClient.setTcpNoDelay(true);
        /* zero out the buffer from last time */
        for (int i = 0; i < BUF_SIZE; i++) {
            requestBuffer[i] = 0;
        }
        try {
            // Only support HTTP GET/HEAD
            int nread = 0, r = 0;

            outerloop:
            while (nread < BUF_SIZE) {
                r = is.read(requestBuffer, nread, BUF_SIZE - nread);
                if (r == -1) {
                    return;
                }
                int i = nread;
                nread += r;
                for (; i < nread; i++) {
                    if (requestBuffer[i] == (byte) '\n' || requestBuffer[i] == (byte) '\r') {
                        break outerloop;
                    }
                }
            }

            /* are we doing a GET or just a HEAD */
            boolean doingGet;
            /* beginning of file name */
            int index;
            if (requestBuffer[0] == (byte) 'G' &&
                requestBuffer[1] == (byte) 'E' &&
                requestBuffer[2] == (byte) 'T' &&
                requestBuffer[3] == (byte) ' ') {
                doingGet = true;
                index = 4;
            }
            else if (requestBuffer[0] == (byte) 'H' &&
                requestBuffer[1] == (byte) 'E' &&
                requestBuffer[2] == (byte) 'A' &&
                requestBuffer[3] == (byte) 'D' &&
                requestBuffer[4] == (byte) ' ') {
                doingGet = false;
                index = 5;
            }
            else {
                /* we don't support this method */
                ps.print("HTTP/1.0 " + HttpConstants.cBAD_METHOD +
                    " unsupported method type: ");
                ps.write(requestBuffer, 0, 5);
                ps.write(EOL);
                ps.flush();
                socketToClient.close();
                return;
            }

            int i = 0;
            /* find the file name, from:
             * GET /foo/bar.html HTTP/1.0
             * extract "/foo/bar.html"
             */
            for (i = index; i < nread; i++) {
                if (requestBuffer[i] == (byte) ' ') {
                    break;
                }
            }
            String restRequest = new String(requestBuffer,index+1,(i - index)-1);// drop /
            
            server.log(restRequest);
            String[] methodAndArgs = restRequest.split("\\?");
            HttpMethod method = server.getMethod(methodAndArgs[0]);
            if (method == null) {
                send404(ps,"'"+methodAndArgs[0]+"' is not supported");
            } else {
                Map<String,String> args = new HashMap<String,String>();
                if (methodAndArgs.length > 1 && methodAndArgs[1].length() > 0) {
                    String[] methodArgs = methodAndArgs[1].split("\\&");
                    for(String methodArg:methodArgs) {
                        String[] keyValue = methodArg.split("\\=");
                        args.put(keyValue[0], keyValue[1]);
                    }
                }
                method.invoke(server.root,args,ps);
            }
        }
        finally {
            socketToClient.close();
        }
    }

    

    void send404(PrintStream ps,String message) throws IOException {
        ps.write(EOL);
        ps.write(EOL);
        ps.println("Not Found\n\n" +
            "The requested resource was not found.\n\n"+message+"\n");
    }

    
}