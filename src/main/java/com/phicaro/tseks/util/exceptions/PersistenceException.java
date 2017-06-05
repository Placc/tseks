/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util.exceptions;

/**
 *
 * @author Placc
 */
public class PersistenceException extends Exception {
    
    public PersistenceException(Exception innerException) {
        super(innerException);
    }
 
    public PersistenceException() {
        super();
    }
}
