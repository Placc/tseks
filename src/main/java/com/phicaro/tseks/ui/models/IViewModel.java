/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

/**
 *
 * @author Placc
 */
public interface IViewModel<T> {
    T getModel();
    boolean matches(T o);
}
