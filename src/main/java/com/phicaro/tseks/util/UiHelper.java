/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 *
 * @author Placc
 */
public class UiHelper {
    
    private static Single<Alert> createAlert(AlertType type, String title, String message, ButtonType... buttonTypes) {
        return Single.create(s -> {
            Alert alert = new Alert(type, message, buttonTypes);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.getDialogPane().getStylesheets().add(Resources.getStylesheet());
            alert.getDialogPane().getStyleClass().add("root");
            s.onSuccess(alert);
        });
    }
    
    public static void showException(Throwable e) {
        showError(Resources.getString("LAB_ErrorOccured"), e.getMessage());
    }
    
    public static void showError(String title, String message) {
        createAlert(AlertType.ERROR, title, message, ButtonType.OK)
                .subscribeOn(JavaFxScheduler.platform())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(alert -> alert.show());
    }
    
    public static Observable<Boolean> showReconnectDialog() {
        return createAlert(AlertType.ERROR, Resources.getString("LAB_ConnectionError"), Resources.getString("DESC_ConnectionErrorRetry"), ButtonType.NO, ButtonType.YES)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    }
}
