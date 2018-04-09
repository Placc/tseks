/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import com.phicaro.tseks.util.Resources;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Comparator;

/**
 *
 * @author Placc
 */
public class PrintJob {

    private PrinterJob printerJob;
    private int startCard;
    private int from;
    private int to;
    private Pages pages;
    private Page current;
    private PublishSubject<String> progressDesc;

    public PrintJob(PrinterJob printerJob, Pages pages, int from, int to) {
        this.printerJob = printerJob;
        this.startCard = 1;
        this.from = from;
        this.to = to;
        this.pages = pages;
        progressDesc = PublishSubject.create();
    }

    public Completable start() {
        return Completable.create(
                s -> {
                    try {
                        printerJob.print();
                        progressDesc.onComplete();

                        s.onComplete();
                    } catch (PrinterAbortException e) {
                        s.onComplete();
                    } catch (PrinterException e) {
                        s.onError(e);
                        progressDesc.onComplete();
                    }
                });
    }

    public void cancel() {
        printerJob.cancel();
        progressDesc.onComplete();
    }

    public Pages getPages() {
        return pages;
    }

    public Page getCurrentPage() {
        return current;
    }

    public void setCurrentPage(Page page) {
        if (current != null) {
            this.startCard += current.getCards().size();
        }

        this.current = page;

        progressDesc.onNext(getDescription());
    }

    public Observable<String> getProgressDescription() {
        return progressDesc;
    }

    public int getTotal() {
        return to - from + 1;
    }

    public int getMinCardNumber() {
        return from;
    }

    public int getMaxCardNumber() {
        return to;
    }

    public String getDescription() {
        if (current == null) {
            return "";
        }

        int endCard = startCard + current.getCards().size() - 1;
        int minCardNumber = current.getCards().stream().map(c -> c.getCardNumber()).min(Comparator.naturalOrder()).orElse(1);
        int maxCardNumber = current.getCards().stream().map(c -> c.getCardNumber()).max(Comparator.naturalOrder()).orElse(1);
        return Resources.getString("LAB_PrintingFromTo", startCard, endCard, minCardNumber, maxCardNumber, getTotal());
    }
}
