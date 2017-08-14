/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import io.reactivex.Completable;
import java.awt.print.PrinterJob;

/**
 *
 * @author Placc
 */
public class PrintJob {
    
    private PrinterJob printerJob;
    
    public PrintJob(PrinterJob printerJob) {
        this.printerJob = printerJob;
    }
    
    public Completable start() {
        return Completable.fromAction(printerJob::print)
                .doOnDispose(printerJob::cancel);
    }
    
    public Object getPreview() {
        return null;
    }
}
