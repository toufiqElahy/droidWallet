/*
 *
Copyright (c) 2013 by Samsung Electronics Co., Ltd. Media Solution Center
 * All rights reserved.

This software is the confidential and proprietary information
 * of Samsung Electronics Co., Ltd("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Samsung Electronics Co., Ltd.
 */

package com.wallet.crypto.trustapp.utility;

import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * <h1>Overview</h1>
 * <hr>
 * <p>
 * Provide logs for development and debugging.
 * CWLog forging preventing is included for security.
 */
public class CWLog {

    public static String TAG_PREFIX = "CWSample";
    //public static boolean V_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;
    //public static boolean V_ENABLED = android.os.Debug.isProductShip() != 1;
    public static boolean V_ENABLED = true;  // not user binary //todo : need to remove after branch_out. V_ENABLED set false
    public static boolean D_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;
    public static boolean E_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;
    public static boolean I_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;
    public static boolean W_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;
    public static final boolean WTF_ENABLED = true;//DebugUtil.DEBUG_MODE_ON;

    private static final int VERBOSE = android.util.Log.VERBOSE;
    private static final int DEBUG = android.util.Log.DEBUG;
    private static final int INFO = android.util.Log.INFO;
    private static final int WARN = android.util.Log.WARN;
    private static final int ERROR = android.util.Log.ERROR;

    /**
     * @param tag
     * @param msg
     */
    @SuppressWarnings("unused")
    public static void v(String tag, String msg) {

        if (V_ENABLED == false) {
            return;
        }
        
        android.util.Log.println(VERBOSE, TAG_PREFIX, buildMsg(tag, msg));
        //android.util.secutil.CWLog.secV(TAG_PREFIX, buildMsg(tag, msg));
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    @SuppressWarnings("unused")
    public static void v(String tag, String msg, Throwable tr) {

        if (V_ENABLED == false) {
            return;
        }

        android.util.Log.println(VERBOSE, TAG_PREFIX, buildMsg(tag, msg));
        android.util.Log.println(VERBOSE, TAG_PREFIX, getStackTraceString(tr));

        //android.util.secutil.CWLog.secV(TAG_PREFIX, buildMsg(tag, msg));
        //android.util.secutil.CWLog.secV(TAG_PREFIX, getStackTraceString(tr));
    }

    /**
     * @param tag
     * @param tr
     */
    @SuppressWarnings("unused")
    public static void v(String tag, Throwable tr) {
        v(tag, "", tr);
    }


    /**
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (D_ENABLED == false) {
            return;
        }
        android.util.Log.println(DEBUG, TAG_PREFIX, buildMsg(tag, msg));
    }

    private static String buildMsg(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        //sb.append(String.format("[%d]", Thread.currentThread().getId()));
        sb.append("[").append(tag).append("] ");
        sb.append(msg);
        return sb.toString();
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (D_ENABLED == false) {
            return;
        }
        android.util.Log.println(DEBUG, TAG_PREFIX, buildMsg(tag, msg));
        android.util.Log.println(DEBUG, TAG_PREFIX, getStackTraceString(tr));
    }

    /**
     * @param tag
     * @param tr
     */
    public static void d(String tag, Throwable tr) {
        d(tag, "", tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (I_ENABLED == false) {
            return;
        }
        android.util.Log.println(INFO, TAG_PREFIX, buildMsg(tag, msg));
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (I_ENABLED == false) {
            return;
        }
        android.util.Log.println(INFO, TAG_PREFIX, buildMsg(tag, msg));
        android.util.Log.println(INFO, TAG_PREFIX, getStackTraceString(tr));
    }

    /**
     * @param tag
     * @param tr
     */
    public static void i(String tag, Throwable tr) {
        i(tag, "", tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (W_ENABLED == false) {
            return;
        }
        android.util.Log.println(WARN, TAG_PREFIX, buildMsg(tag, msg));
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (W_ENABLED == false) {
            return;
        }
        android.util.Log.println(WARN, TAG_PREFIX, buildMsg(tag, msg));
        android.util.Log.println(WARN, TAG_PREFIX, getStackTraceString(tr));
    }

    /**
     * @param tag
     * @param tr
     */
    public static void w(String tag, Throwable tr) {
        w(tag, "", tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (E_ENABLED == false) {
            return;
        }
        android.util.Log.println(ERROR, TAG_PREFIX, buildMsg(tag, msg));
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (E_ENABLED == false) {
            return;
        }
        android.util.Log.println(ERROR, TAG_PREFIX, buildMsg(tag, msg));
        android.util.Log.println(ERROR, TAG_PREFIX, getStackTraceString(tr));
    }

    /**
     * @param tag
     * @param tr
     */
    public static void e(String tag, Throwable tr) {
        e(tag, "", tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void wtf(String tag, String msg) {
        if (WTF_ENABLED == false) {
            return;
        }

        //@keehwan.seol This is tempory code for O version. This code make FC on api 26. But I can't find which caused FC,
        if (Build.VERSION.SDK_INT >= 26) {
            return;
        }
        android.util.Log.wtf(TAG_PREFIX, buildMsg(tag, msg), null);
    }

    /**
     * @param tag
     * @param tr
     */
    public static void wtf(String tag, Throwable tr) {
        if (WTF_ENABLED == false) {
            return;
        }
        android.util.Log.wtf(TAG_PREFIX.concat(tag), tr.getMessage(), tr);
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void wtf(String tag, String msg, Throwable tr) {
        if (WTF_ENABLED == false) {
            return;
        }
        android.util.Log.wtf(TAG_PREFIX, buildMsg(tag, msg), tr);
    }

    /**
     * @return Stack Trace
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }
}