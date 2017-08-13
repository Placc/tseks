/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author Placc
 */
public class Platform {
    
    public static String getAppData() {
        String path = "";
        String OS = System.getProperty("os.name").toUpperCase();
        
        if (OS.contains("WIN"))
            path = System.getenv("APPDATA");
        else if (OS.contains("MAC"))
            path = System.getProperty("user.home") + "/Library/";
        else if (OS.contains("NUX"))
            path = System.getProperty("user.home");
        else path =  System.getProperty("user.dir");

        return path;
    }
    
    public static File getWorkingDirectory() {
        File dir = Paths.get(getAppData(), Resources.getConfig("CFG_ApplicationPackage")).toFile();
        
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        return dir;
    }
}
