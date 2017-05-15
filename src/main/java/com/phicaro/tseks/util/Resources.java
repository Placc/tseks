/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import com.phicaro.tseks.ui.MainApp;
import com.phicaro.tseks.util.exceptions.ResourceNotFoundException;
import java.io.*;
import java.util.Optional;


/**
 *
 * @author Placc
 */
public class Resources {
    
    private MainApp appInstance;
    private static Resources resourceInstance;
    
    public static void initialize(MainApp appInstance) {
        resourceInstance = new Resources(appInstance);
    }
    
    private Resources(MainApp appInstance) {
        this.appInstance = appInstance;
    }
    
    public static String getString(final String label) throws ResourceNotFoundException {
        InputStream stream = resourceInstance.appInstance.getClass().getResourceAsStream("/strings/stringsDE");
        
        BufferedReader reader;
        
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.error("resources get-string", ex);
            throw new ResourceNotFoundException(label);
        }

        Optional<String> match = reader.lines().filter((String t) -> t.startsWith(label)).findFirst();

        if(!match.isPresent()) {
            throw new ResourceNotFoundException(label);
        }

        return match.get().substring(match.get().indexOf('=') + 1);
    }
    
}
