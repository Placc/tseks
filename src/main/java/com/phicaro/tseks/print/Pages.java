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
    
    public Pages(List<Page> pages) {
        this.pages = pages;
    }
    
    public void addPage(Page page) {
        pages.add(page);
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
        return pages.get(pageIndex);
    }
}
