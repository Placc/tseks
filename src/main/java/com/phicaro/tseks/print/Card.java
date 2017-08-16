/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.util.Resources;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Placc
 */
public class Card implements Printable {

    private static final Font CARD_FONT = new Font("Times New Roman", Font.BOLD, 11);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private int cardNumber;
    private int tableNumber;
    private double price;
    private PageSize cardSize;

    private String title;
    private String description;
    private String location;
    private Date date;

    public Card(int cardNumber, Event event, int tableNumber, double price, PageSize cardSize) {
        this.cardNumber = cardNumber;
        this.tableNumber = tableNumber;
        this.price = price;
        this.cardSize = cardSize;

        this.title = event.getTitle();
        this.description = event.getDescription();
        this.location = event.getLocation().getLocationDescription();
        this.date = event.getDate();
    }

    public PageSize getCardSize() {
        return cardSize;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D graphics2d = (Graphics2D) graphics;

        //Text begin: 2,8 cm from top = 1.10236 inches
        //Text offset: 0,4 cm = 0.23622 inches
        //Number right: 0,6 cm = 0.15748 inches
       
        double xPos = pageFormat.getImageableX() + pageFormat.getImageableWidth() / 2.0;
        double yPos = pageFormat.getImageableY() + 1.10236 * PrinterService.DEFAULT_DPI;

        Point2D.Double position = new Point2D.Double(xPos, yPos);
        
        //Title
        drawLine(graphics2d, position, title);

        //Cardnumber
        AttributedString attrString = new AttributedString("" + cardNumber);
        attrString.addAttribute(TextAttribute.FONT, CARD_FONT);
        attrString.addAttribute(TextAttribute.FOREGROUND, Color.black);

        AttributedCharacterIterator charIterator = attrString.getIterator();
        TextMeasurer measurer = new TextMeasurer(charIterator, graphics2d.getFontRenderContext());
        TextLayout layout = measurer.getLayout(0, ("" + cardNumber).length());

        Point2D.Float pen = new Point2D.Float();
        pen.y = (float) yPos + layout.getAscent();
        pen.x = (float) (pageFormat.getImageableX() + pageFormat.getImageableWidth() - 0.23622 * PrinterService.DEFAULT_DPI) - layout.getAdvance();

        layout.draw(graphics2d, pen.x, pen.y);
        
        drawLine(graphics2d, position, " ");

        //Location
        drawLine(graphics2d, position, location);
        drawLine(graphics2d, position, " ");

        //Description
        drawLine(graphics2d, position, description);
        drawLine(graphics2d, position, " ");

        //Date + Time
        String dateText = Resources.getString("LAB_AtDateCard", dateFormat.format(date));
        String timeText = Resources.getString("LAB_AtTimeCard", timeFormat.format(date));

        String dateTime = (dateText + "    " + timeText);
        drawLine(graphics2d, position, dateTime);
        drawLine(graphics2d, position, " ");
        

        //TablePrice
        String tableNumber = Resources.getString("LAB_TableNumberCard", this.tableNumber);
        String price = Resources.getString("LAB_PriceCard", this.price);
        String space = "       ".substring(1 + (int) Math.log10(this.tableNumber));

        String tablePrice = tableNumber + space + price;
        drawLine(graphics2d, position, tablePrice);

        return Printable.PAGE_EXISTS;
    }
    
    private void drawLine(Graphics2D graphics2d, Point2D.Double position, String text) {
        AttributedString attrString = new AttributedString(text);
        attrString.addAttribute(TextAttribute.FONT, CARD_FONT);
        attrString.addAttribute(TextAttribute.FOREGROUND, Color.black);

        AttributedCharacterIterator charIterator = attrString.getIterator();
        TextMeasurer measurer = new TextMeasurer(charIterator,
                graphics2d.getFontRenderContext());
        TextLayout layout = measurer.getLayout(0, text.length());

        float xPos = (float) position.getX() - layout.getAdvance() / 2.0f;
        float yPos = (float) position.getY() + layout.getAscent();
        
        layout.draw(graphics2d, xPos, yPos);
        
        position.setLocation(position.getX(), yPos);
    }

}
