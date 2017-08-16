/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.print.attribute.standard.MediaSize;

/**
 *
 * @author Placc
 */
public class Page implements Printable {

    private List<Card> cards;
    private PageFormat format;
    
    public Page(List<Card> cards, PageFormat format) {
        this.cards = cards;
        this.format = format;
    }
    
    public PageFormat getFormat() {
        return format;
    }
    
    public List<Card> getCards() {
        return cards;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        double xPos = pageFormat.getImageableX();
        double yPos = pageFormat.getImageableY();
        
        try {
            Graphics2D g2d = (Graphics2D) graphics;
            BufferedImage image = ImageIO.read(getClass().getResource("/images/assets/Page.png"));
            
            int imageWidth  = image.getWidth();
            int imageHeight = image.getHeight();

            double scaleX = (double)pageFormat.getImageableWidth()/imageWidth;
            double scaleY = (double)pageFormat.getImageableHeight()/imageHeight;
            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
            g2d.drawImage(image, scaleTransform, null);
        } catch (IOException e) {
            
        }
        
        
        for(int idx = 0; idx < cards.size(); idx++) {
            Card card = cards.get(idx);
            MediaSize cardSize = card.getCardSize().asMediaSize();
            
            double cardWidth = cardSize.getY(MediaSize.INCH) * PrinterService.DEFAULT_DPI;
            double cardHeight = cardSize.getX(MediaSize.INCH) * PrinterService.DEFAULT_DPI;

            if(xPos + cardWidth > pageFormat.getImageableWidth()) {
                xPos = pageFormat.getImageableX();
                yPos += cardHeight;
            }
            
            Paper paper = new Paper();
            paper.setSize(pageFormat.getPaper().getWidth(), pageFormat.getPaper().getHeight());
            paper.setImageableArea(xPos, yPos, cardWidth, cardHeight);
            
            PageFormat format = new PageFormat();
            format.setOrientation(PageFormat.PORTRAIT);
            format.setPaper(paper);

            card.print(graphics, format, pageIndex);
            
            xPos += cardWidth;
        }
        
        return PAGE_EXISTS;
    }
    
}
