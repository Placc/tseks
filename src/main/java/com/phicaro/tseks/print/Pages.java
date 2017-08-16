/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.List;

/**
 *
 * @author Placc
 */
public class Pages implements Pageable {

    private List<Page> pages;
    private PrintJob printJob;
    
    public Pages(List<Page> pages) {
        this.pages = pages;
    }
    
    public void setPrintJob(PrintJob job) {
        this.printJob = job;
    }
    
    public void addPage(Page page) {
        pages.add(page);
    }
    
    public List<Page> getPages() {
        return pages;
    }
    
    @Override
    public int getNumberOfPages() {
        return pages.size();
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pages.get(pageIndex).getFormat();
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        if(printJob != null) {
            printJob.setCurrentPage(pages.get(pageIndex));
        }
        return pages.get(pageIndex);
    }
}
