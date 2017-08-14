/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phicaro.tseks.util.Platform;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Placc
 */
public class SettingsService {
    
    private PrintSettings printSettings;
    private DatabaseSettings databaseSettings;
    
    public SettingsService() {
    }
    
    public PrintSettings getPrintSettings() {
        return printSettings;
    }
    
    public DatabaseSettings getDatabaseSettings() {
        return databaseSettings;
    }
    
    public void loadSettings() throws IOException {
        printSettings = (PrintSettings) loadSettings(PrintSettings.class);
        databaseSettings = (DatabaseSettings) loadSettings(DatabaseSettings.class);
    }
    
    public void saveSettings() throws IOException {
        saveSettings(printSettings);
        saveSettings(databaseSettings);
    }
    
    private ISettings loadSettings(Class<? extends ISettings> settingsClass) throws IOException {
        File settingsFile = new File(Platform.getWorkingDirectory(), settingsClass.getSimpleName());
        
        if(!settingsFile.exists()) {
            try {
                ISettings settings = settingsClass.newInstance();
                
                saveSettings(settings);
                return settings;
            } catch(IllegalAccessException | InstantiationException e) {
                throw new IOException(e);
            }
        }
        
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(settingsFile, PrintSettings.class);
    }
    
    private void saveSettings(ISettings settings) throws IOException {
        File settingsFile = new File(Platform.getWorkingDirectory(), settings.getClass().getSimpleName());
        
        if(!settingsFile.exists()) {
            settingsFile.createNewFile();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(settingsFile, settings);
    }
}
