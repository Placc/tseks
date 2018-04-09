/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import io.reactivex.Observable;

/**
 *
 * @author Philip
 */
public interface IErrorHandler {

    Observable<Boolean> resumeOnError(Throwable throwable);
}
