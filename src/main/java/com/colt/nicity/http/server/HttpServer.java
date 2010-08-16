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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * Super simple, multi-threaded HTTP server.
 */
public class HttpServer {

    final ConcurrentLinkedQueue<HttpRequestHandler> requestHandlers = new ConcurrentLinkedQueue<HttpRequestHandler>();
    final File root;
    final int timeout;
    final int maxNumberOfRequestHandlers;
    final int port;
    final PrintStream log;
    private boolean isRunning = false;
    final Map<String,HttpMethod> methods = new ConcurrentHashMap<String, HttpMethod>();
    private ServerSocket serverSocket;
    public HttpServer(File root, File logFile, int timeout, int maxNumberOfRequestHandlers, int port) throws FileNotFoundException {
        this.root = root;
        this.timeout = timeout;
        this.maxNumberOfRequestHandlers = maxNumberOfRequestHandlers;
        this.port = port;
        if (!root.exists()) {
            throw new Error(root + " doesn't exist as server root");
        }
        log = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                HttpServer.this.stop();
            }
        });
    }

    public void addMethod(String name,HttpMethod method) {
        methods.put(name, method);
    }
    public HttpMethod getMethod(String name) {
        return methods.get(name);
    }

    public HttpMethod removeMethod(String name) {
        return methods.remove(name);
    }

    protected void log(String message) {
        System.out.println(message);
        synchronized (log) {
            log.println(message);
            log.flush();
        }
    }

    public boolean isRunning() {
        synchronized (this) {
            return isRunning;
        }
    }

    public void start() {
        synchronized (this) {
            if (isRunning)
                return;
            isRunning = true;
        }

        for (int i = 0; i < maxNumberOfRequestHandlers; ++i) {
            HttpRequestHandler requestHandler = new HttpRequestHandler(this);
            new Thread(requestHandler, "request handler" + i).start();
            requestHandlers.add(requestHandler);
        }


        
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException ex) {
            throw new Error(ex);
        }
        while (isRunning) {

            Socket socket;
            try {
                socket = serverSocket.accept();
            }
            catch (IOException ex) {
                throw new Error(ex);
            }

            HttpRequestHandler requestHandler = null;
            synchronized (requestHandlers) {
                if (requestHandlers.isEmpty()) {
                    HttpRequestHandler ws = new HttpRequestHandler(this);
                    ws.setRequestingSocket(socket);
                    new Thread(ws, "additional worker").start();
                }
                else {
                    requestHandler = requestHandlers.poll();
                    requestHandler.setRequestingSocket(socket);
                }
            }
        }
    }

    synchronized public void stop() {
        synchronized (this) {
            if (!isRunning)
                return;
            isRunning = false;
            try {
                serverSocket.close();
            }
            catch (IOException ex) {
                throw new Error(ex);
            }
        }
    }
}
