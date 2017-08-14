/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util.exceptions;

import com.phicaro.tseks.util.Resources;

/**
 *
 * @author Placc
 */
public class InvalidPageSizeException extends Exception {
    
    public InvalidPageSizeException() {
        super(Resources.getString("LAB_InvalidPageSize"));
    }
}
