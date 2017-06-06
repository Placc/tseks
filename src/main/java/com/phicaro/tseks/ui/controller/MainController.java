/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.TseksApp;
import com.phicaro.tseks.services.IDatabaseService;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class MainController implements Initializable {

    @FXML
    private HBox toolbar;
    @FXML
    private Label pageTitle;
    @FXML
    private MenuButton optionButton;
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

        optionButton.setText(Resources.getString("LAB_Options"));

        switchToStartup();

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

    private void setToolbar(String label, boolean visible) {
        pageTitle.setText(label);
        toolbar.setVisible(visible);
    }

    public void switchToStartup() {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/Startup.fxml"));
            setToolbar("", false);
            content.setCenter(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-startup", e);
        }
    }

    public void switchToOverview() throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Overview.fxml"));
            setToolbar(Resources.getString("LAB_Events"), true);
            content.setCenter(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-overview", e);
        }
    }

    public TseksApp getTseksApp() {
        return tseksApp;
    }

}
