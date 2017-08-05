/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import com.phicaro.tseks.ui.MainApp;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.util.exceptions.ResourceNotFoundException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javafx.scene.image.Image;

/**
 *
 * @author Placc
 */
public class Resources {
    
    public static enum ImageSize {
        NORMAL, LARGE
    }
    
    public static String getStylesheet() {
        return MainApp.class.getResource("/styles/Styles.css").toExternalForm();
    }

    public static String getString(final String label, Object... args) {
        return String.format(getString(label), args);
    }
    
    public static String getString(final String label) throws ResourceNotFoundException {
        return findString(label, "/strings/stringsDE");
    }
    
    public static InputStream getResourceAsStream(String resource) {
        return MainApp.class.getResourceAsStream(resource);
    }
    
    private static String findString(final String label, final String file) throws ResourceNotFoundException {
        InputStream stream = MainApp.class.getResourceAsStream(file);

        BufferedReader reader;

        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.error("resources get-string", ex);
            throw new ResourceNotFoundException(label);
        }

        Optional<String> match = reader.lines().filter((String t) -> t.trim().substring(0, t.trim().indexOf("=")).equals(label)).findFirst();

        if (!match.isPresent()) {
            throw new ResourceNotFoundException(label);
        }

        return match.get().substring(match.get().indexOf('=') + 1);
    }

    public static String getConfig(String id) {
        return findString(id, "/config/configDE");
    }
    
    public static Image getImage(String name, ImageSize size) throws ResourceNotFoundException {
        if (!name.endsWith(".png")) {
            name = name + ".png";
        }

        String path = "/images/1x/" + name;
        if (size == ImageSize.LARGE) {
            path = "/images/2x/" + name;
        }

        try {
            InputStream stream = MainApp.class.getResourceAsStream(path);

            Image image = new Image(stream);

            if (stream == null || image == null) {
                throw new ResourceNotFoundException(name);
            }

            return image;
        } catch (Exception e) {
            throw new ResourceNotFoundException(name);
        }
    }
}
