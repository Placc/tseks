/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

/**
 *
 * @author Placc
 */
public class Logger {

    public static void error(String message, Throwable e) {
        System.out.println(message);
        
        if(e != null) {
            e.printStackTrace();
        }
    }

    public static void error(String message) {
        error(message, null);
    }
}
