/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

/**
 *
 * @author Placc
 */
public enum PageSize {
    A0(MediaSizeName.ISO_A0), 
    A1(MediaSizeName.ISO_A1),
    A2(MediaSizeName.ISO_A2),
    A3(MediaSizeName.ISO_A3),
    A4(MediaSizeName.ISO_A4),
    A5(MediaSizeName.ISO_A5),
    A6(MediaSizeName.ISO_A6),
    A7(MediaSizeName.ISO_A7),
    A8(MediaSizeName.ISO_A8),
    A9(MediaSizeName.ISO_A9),
    A10(MediaSizeName.ISO_A10);
    
    private MediaSize size;
    
    private PageSize(MediaSizeName name) {
        size = MediaSize.getMediaSizeForName(name);
    }
    
    public MediaSize asMediaSize() {
        return size;
    }
    
    public boolean smaller(PageSize other) {
        return ordinal() > other.ordinal();
    }
}
