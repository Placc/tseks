/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print.settings;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phicaro.tseks.util.Platform;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Placc
 */
public class SettingsService {
    
    private static final String SETTINGS_FILE = "settings.json";
    
    private File settingsFile;
    
    public SettingsService() {
        settingsFile = new File(Platform.getWorkingDirectory(), SETTINGS_FILE);
    }
    
    public PrintSettings loadSettings() throws IOException {
        if(!settingsFile.exists()) {
            saveSettings(new PrintSettings());
        }
        
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(settingsFile, PrintSettings.class);
    }
    
    public void saveSettings(PrintSettings settings) throws IOException {
        if(!settingsFile.exists()) {
            settingsFile.createNewFile();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(settingsFile, settings);
    }
}
