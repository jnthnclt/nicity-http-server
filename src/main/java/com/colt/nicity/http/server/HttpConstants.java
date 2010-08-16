/*
 * HttpConstants.java
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

public class HttpConstants {
    /** 2XX: generally "OK" */
    public static final int cOK = 200;
    public static final int cCREATED = 201;
    public static final int cACCEPTED = 202;
    public static final int cNOT_AUTHORITATIVE = 203;
    public static final int cNO_CONTENT = 204;
    public static final int cRESET = 205;
    public static final int cPARTIAL = 206;

    /** 3XX: relocation/redirect */
    public static final int cMULT_CHOICE = 300;
    public static final int cMOVED_PERM = 301;
    public static final int cMOVED_TEMP = 302;
    public static final int cSEE_OTHER = 303;
    public static final int cNOT_MODIFIED = 304;
    public static final int cUSE_PROXY = 305;

    /** 4XX: client error */
    public static final int cBAD_REQUEST = 400;
    public static final int cUNAUTHORIZED = 401;
    public static final int cPAYMENT_REQUIRED = 402;
    public static final int cFORBIDDEN = 403;
    public static final int cNOT_FOUND = 404;
    public static final int cBAD_METHOD = 405;
    public static final int cNOT_ACCEPTABLE = 406;
    public static final int cPROXY_AUTH = 407;
    public static final int cCLIENT_TIMEOUT = 408;
    public static final int cCONFLICT = 409;
    public static final int cGONE = 410;
    public static final int cLENGTH_REQUIRED = 411;
    public static final int cPRECON_FAILED = 412;
    public static final int cENTITY_TOO_LARGE = 413;
    public static final int cREQ_TOO_LONG = 414;
    public static final int cUNSUPPORTED_TYPE = 415;

    /** 5XX: server error */
    public static final int cSERVER_ERROR = 500;
    public static final int cINTERNAL_ERROR = 501;
    public static final int cBAD_GATEWAY = 502;
    public static final int cUNAVAILABLE = 503;
    public static final int cGATEWAY_TIMEOUT = 504;
    public static final int cVERSION = 505;
}
