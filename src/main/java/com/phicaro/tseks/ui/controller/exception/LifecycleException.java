/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.exception;

import com.phicaro.tseks.util.Resources;

/**
 *
 * @author Placc
 */
public class LifecycleException extends Exception {
    
    public LifecycleException() {
        super(Resources.getString("MSG_LifecycleError"));
    }
}
