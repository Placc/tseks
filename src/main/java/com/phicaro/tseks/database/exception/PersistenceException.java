/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database.exception;

import com.phicaro.tseks.util.Resources;

/**
 *
 * @author Placc
 */
public class PersistenceException extends Exception {
    
    public PersistenceException(Exception innerException) {
        super(innerException);
    }

    public PersistenceException(String message) {
        super(message);
    }
    
    public PersistenceException() {
        super(Resources.getString("MSG_PersistenceException"));
    }
}
