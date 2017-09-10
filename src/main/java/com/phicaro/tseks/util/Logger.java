/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 * @author Placc
 */
public class Logger {

    private static boolean DEBUG = false;
    private static File logFile;

    private static void ensureLog() {
        if (DEBUG) {
            logFile = new File(Platform.getAppData(), "log");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void setDebugMode(boolean debug) {
        DEBUG = debug;
    }

    public static void error(String message, Throwable e) {
        write("ERROR", message, e);
    }

    public static void info(String message) {
        write("INFO", message, null);
    }

    private static void write(String tag, String message, Throwable e) {
        System.out.println(message);

        if (e != null) {
            e.printStackTrace();
        }

        if (DEBUG) {
            ensureLog();

            try {
                PrintWriter writer = new PrintWriter(logFile);
                writer.print("[" + new Date() + " | " + tag + "] " + message);

                if (e != null) {
                    e.printStackTrace(writer);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void error(String message) {
        error(message, null);
    }
}
