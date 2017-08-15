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
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String label) {
        super(label);
    }
}
