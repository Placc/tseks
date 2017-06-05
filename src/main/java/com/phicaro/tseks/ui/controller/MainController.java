/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.TseksApp;
import com.phicaro.tseks.services.IDatabaseService;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.UiHelper;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane content;

    private static MainController instance;

    private TseksApp tseksApp;

    public static MainController instance() {
        return instance;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        
        RxJavaPlugins.setErrorHandler(e -> {
            Logger.error("rx-java-plugins on-error", e);
            System.exit(e.hashCode());
        });

        Consumer<IDatabaseService.ConnectionState> onConnectionError = state -> {
            Logger.error("main-app start on-connection-error");

            tseksApp.reconnect()
                    .retry(ex -> UiHelper.showReconnectDialog()
                    .blockingFirst())
                    .subscribeOn(Schedulers.io())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe();
        };

        Consumer<TseksApp> onComplete = app -> {
            tseksApp = app;

            app.connectionState()
                    .filter(state -> state.equals(IDatabaseService.ConnectionState.CLOSED))
                    .subscribe(onConnectionError);

            switchToOverview();
        };

        Consumer<Throwable> onError = e -> {
            Logger.error("main-app start on-error", e);
            System.exit(e.hashCode());
        };

        TseksApp.startApp()
                .retry(ex -> UiHelper.showReconnectDialog()
                .blockingFirst())
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(onComplete, onError);
    }

    public void switchToStartup() throws IOException {
        BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/Startup.fxml"));
        content.setCenter(root);
    }

    public void switchToOverview() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Overview.fxml"));
        content.setCenter(root);
    }

    public TseksApp getTseksApp() {
        return tseksApp;
    }

}
