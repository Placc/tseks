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
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    private ConcurrentHashMap<String, PrintJob> runningJobs;
    private PublishSubject<String> printJobChanged;

    public PrinterService(SettingsService settingsService) {
        this.settingsService = settingsService;
        this.runningJobs = new ConcurrentHashMap<>();
        this.printJobChanged = PublishSubject.create();
    }

    private PrintService getDefaultPrinter() throws PrinterException {
        if (settingsService.getPrintSettings().getPrinter() == null) {
            throw new PrinterException("MSG_NoPrinterSelected");
        }

        PrintService[] services = PrinterJob.lookupPrintServices();

        Optional<PrintService> serviceOptional = Stream.of(services)
                .filter(s -> s.getName().equals(settingsService.getPrintSettings().getPrinter()))
                .findAny();

        if (!serviceOptional.isPresent()) {
            throw new PrinterException(Resources.getString("MSG_PrinterNotAvailable"));
        } else {
            return serviceOptional.get();
        }
    }

    public List<String> listPrinters() throws PrinterException {
        PrintService[] services = PrinterJob.lookupPrintServices();

        if (services.length == 0) {
            throw new PrinterException(Resources.getString("MSG_NoPrintersAvailable"));
        } else {
            return Observable.fromArray(services)
                    .map(service -> service.getName())
                    .toList()
                    .blockingGet();
        }
    }

    public void print(Event event, int fromTableNumber, int toTableNumber) {
        createPages(event, fromTableNumber, toTableNumber, getPageFormat())
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .toList()
                .map(list -> createPrintJob(new Pages(list)))
                .subscribe(job -> {
                    synchronized (runningJobs) {
                        runningJobs.put(event.getId(), job);
                        printJobChanged.onNext(event.getId());
                    }
                    job.start()
                            .onErrorComplete()
                            .subscribe(() -> {
                                synchronized (runningJobs) {
                                    runningJobs.remove(event.getId());
                                    printJobChanged.onNext(event.getId());
                                }
                            });
                });
    }

    public void print(Event event) {
        print(event, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public PrintJob getRunningJobByEvent(Event event) {
        synchronized (runningJobs) {
            return runningJobs.get(event.getId());
        }
    }

    public Observable<String> runningJobChanged() {
        return printJobChanged;
    }

    private Observable<Page> createPages(Event event, int fromTable, int toTable, PageFormat format) {
        PageSize cardSize = settingsService.getPrintSettings().getCardSize();

        int cardsPerPage = computeCardsPerPage(format, cardSize);

        AtomicInteger count = new AtomicInteger(1);

        return Observable.fromIterable(event.getTableCategories())
                .sorted((t1, t2) -> t1.getMinTableNumber() - t2.getMinTableNumber())
                .flatMap(category -> {
                    int categoryStartCardNumber = count.get();
                    return Observable.range(categoryStartCardNumber, category.getSeatsNumber() * category.getNumberOfTables())
                            .map(number -> new Card(number, event, category.getMinTableNumber() + (number - categoryStartCardNumber) / category.getSeatsNumber(), category.getPrice().getPrice(), cardSize))
                            .doOnNext(__ -> count.incrementAndGet());
                })
                .filter(card -> card.getTableNumber() >= fromTable && card.getTableNumber() <= toTable)
                .window(cardsPerPage)
                .flatMapSingle(observable -> observable.toList())
                .map(list -> new Page(list, format));
    }

    private int computeCardsPerPage(PageFormat format, PageSize cardSize) {
        int horizontal = (int) ((format.getImageableWidth() / DEFAULT_DPI) / cardSize.asMediaSize().getY(MediaSize.INCH));
        int vertical = (int) ((format.getImageableHeight() / DEFAULT_DPI) / cardSize.asMediaSize().getX(MediaSize.INCH));
        return horizontal * vertical;
    }

    private PrintJob createPrintJob(Pages pages) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(pages);

        PrintService printer = getDefaultPrinter();

        try {
            job.setPrintService(printer);
        } catch (PrinterException e) {
            Logger.error("card-printer-service create-print-job default printservice", e);
            throw new PrinterException(Resources.getString("MSG_DefaultPrinterNotAvailable"));
        }

        int fromCard = 0;
        int toCard = 0;
        if (!pages.getPages().isEmpty()) {
            Page firstPage = pages.getPages().get(0);
            Card firstCard = firstPage.getCards().get(0);
            Page lastPage = pages.getPages().get(pages.getNumberOfPages() - 1);
            Card lastCard = lastPage.getCards().get(lastPage.getCards().size() - 1);

            fromCard = firstCard.getCardNumber();
            toCard = lastCard.getCardNumber();
        }

        PrintJob result = new PrintJob(job, fromCard, toCard);

        pages.setPrintJob(result);

        return result;
    }

    private PageFormat getPageFormat() {
        MediaSize size = settingsService.getPrintSettings().getPageSize().asMediaSize();

        double paperWidth = size.getX(MediaSize.INCH) * DEFAULT_DPI;
        double paperHeight = size.getY(MediaSize.INCH) * DEFAULT_DPI;

        Paper paper = new Paper();

        paper.setSize(paperWidth, paperHeight);
        paper.setImageableArea(0, 0, paperWidth, paperHeight);

        PageFormat format = new PageFormat();

        format.setPaper(paper);
        format.setOrientation(PageFormat.PORTRAIT);

        return format;
    }
}
