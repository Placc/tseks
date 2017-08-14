/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.settings.SettingsService;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.exceptions.InvalidPageSizeException;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.print.PrintService;
import javax.print.attribute.standard.MediaSize;

/**
 *
 * @author Placc
 */
public class PrinterService {

    public static final double DEFAULT_DPI = 72.0;
    
    private SettingsService settingsService;
    
    public PrinterService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private PrintService getDefaultPrinter() throws PrinterException {
        if(settingsService.getPrintSettings().getPrinter() == null) {
            throw new PrinterException("MSG_NoPrinterSelected");
        }

        PrintService[] services = PrinterJob.lookupPrintServices();

        Optional<PrintService> serviceOptional = Stream.of(services)
                .filter(s -> s.getName().equals(settingsService.getPrintSettings().getPrinter()))
                .findAny();

        if(!serviceOptional.isPresent()) {
            throw new PrinterException(Resources.getString("MSG_PrinterNotAvailable"));
        } else {
            return serviceOptional.get();
        }
    }
        
    public List<String> listPrinters() throws PrinterException {
        PrintService[] services = PrinterJob.lookupPrintServices();

        if(services.length == 0) {
            throw new PrinterException(Resources.getString("MSG_NoPrintersAvailable"));
        } else {
            return Observable.fromArray(services)
                    .map(service -> service.getName())
                    .toList()
                    .blockingGet();
        }
    }

    public Single<PrintJob> print(Event event) {
        return createPages(event, getPageFormat())
               .toList()
               .map(list -> createPrintJob(new Pages(list)));
    }
    
    private Observable<Page> createPages(Event event, PageFormat format) {
        int total = event.getTableCategories().stream()
                .flatMap(category -> category.getTables().stream())
                .map(table -> table.getSeats())
                .reduce(0, (a, b) -> a + b);
        
        PageSize cardSize = settingsService.getPrintSettings().getCardSize();
        
        int cardsPerPage = computeCardsPerPage(total, format, cardSize);
        
        if(cardsPerPage < 1) {
            return Observable.error(new InvalidPageSizeException());
        }
        
        AtomicInteger count = new AtomicInteger();
        
        return Observable.fromIterable(event.getTableCategories())
                    .flatMap(tableCategory -> Observable.fromIterable(tableCategory.getTables()))
                    .sorted((t1, t2) -> t1.getTableNumber() - t2.getTableNumber())
                    .flatMap(table -> Observable.range(count.incrementAndGet(), table.getSeats())
                                        .map(number -> new Card(number, event, table.getTableNumber(), table.getTableCategory().getPrice().getPrice(), cardSize))
                                        .doOnNext(__ -> count.incrementAndGet()))
                    .buffer(cardsPerPage)
                    .map(list -> new Page(list, format));
    }
    
    private int computeCardsPerPage(int total, PageFormat format, PageSize cardSize) {
        int horizontal = (int)((DEFAULT_DPI * format.getImageableWidth()) / cardSize.asMediaSize().getX(MediaSize.INCH));
        int vertical = (int)((DEFAULT_DPI * format.getImageableHeight()) / cardSize.asMediaSize().getY(MediaSize.INCH));
        return horizontal * vertical;
    }
    
    private PrintJob createPrintJob(Pages pages) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(pages);
        
        try {
            job.setPrintService(getDefaultPrinter());
        } catch (PrinterException e) {
            Logger.error("card-printer-service create-print-job default printservice", e);
            throw new PrinterException(Resources.getString("MSG_DefaultPrinterNotAvailable"));
        }
        
        return new PrintJob(job);
    }
    
    private PageFormat getPageFormat() {
        MediaSize size = settingsService.getPrintSettings().getPageSize().asMediaSize();
        
        double paperWidth = size.getX(MediaSize.INCH) / DEFAULT_DPI;
        double paperHeight = size.getY(MediaSize.INCH) / DEFAULT_DPI;

        Paper paper = new Paper();
        
        paper.setSize(paperWidth, paperHeight);
        paper.setImageableArea(0, 0, paperWidth, paperHeight);
       
        PageFormat format = new PageFormat();

        format.setPaper(paper);
        format.setOrientation(PageFormat.PORTRAIT);
        
        return format;
    }
}
