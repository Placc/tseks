/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.TseksApp;
import com.phicaro.tseks.database.IDatabaseService;
import com.phicaro.tseks.ui.controller.edit.EditEventController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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
    private Button optionButton;
    @FXML
    private BorderPane root;
    @FXML
    private Button backButton;
    @FXML
    private StackPane content;
    @FXML
    private Pane messagePane;
    @FXML
    private StackPane header;

    private static MainController instance;

    private TseksApp tseksApp;

    public static MainController instance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        optionButton.setText(Resources.getString("LAB_Options"));
        optionButton.setOnAction(e -> switchToSettings());

        Image backImage = Resources.getImage("back.png", Resources.ImageSize.NORMAL);
        ColorAdjust colorAdjust = UiHelper.getColorAdjust(Color.STEELBLUE);

        ImageView backView = new ImageView(backImage);
        backView.setEffect(colorAdjust);

        backButton.setGraphic(backView);

        switchToStartup();

        RxJavaPlugins.setErrorHandler(e -> {
            Logger.error("rx-java-plugins on-error", e);
            UiHelper.showException(Resources.getString("LAB_ErrorOccured"), e);
            toggleSpinner(false);
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

            app.connectionState()
                    .filter(state -> state.equals(IDatabaseService.ConnectionState.CONNECTED))
                    .subscribe(__ -> {
                        switchToOverview();
                    });
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

    public void hideToolbar() {
        toolbar.setVisible(false);
    }

    private void setToolbar(String label, boolean backEnabled) {
        pageTitle.setText(label);
        backButton.setVisible(backEnabled);
        toolbar.setVisible(true);
    }

    public void toggleSpinner(boolean visible) {
        backButton.setDisable(visible);
        UiHelper.toggleSpinner(content, visible);
    }

    public void switchToStartup() {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/pages/Startup.fxml"));
            hideToolbar();
            content.getChildren().setAll(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-startup", e);
        }
    }

    public void switchToOverview() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/overview/Overview.fxml"));
            setToolbar(Resources.getString("LAB_Events"), false);
            content.getChildren().setAll(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-overview", e);
        }
    }

    public void navigateAwayFrom(INavigationController from) {
        from.onNavigateAway()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(result -> {
                    if (result) {
                        hideOptionButton(false);
                        switchToOverview();
                        backButton.setOnAction(null);
                    }
                });
    }

    public void switchToEdit(EventViewModel existingEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pages/edit/EditEvent.fxml"));
            Parent root = loader.load();

            final EditEventController controller = loader.getController();

            backButton.setOnAction(e -> {
                navigateAwayFrom(controller);
            });

            String title = "LAB_CreateNewEvent";

            if (existingEvent != null) {
                controller.setEvent(existingEvent);
                title = "LAB_EditEvent";
            }

            setToolbar(Resources.getString(title), true);
            content.getChildren().setAll(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-edit", e);
        }
    }

    public void switchToSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pages/Settings.fxml"));
            Parent root = loader.load();

            final SettingsController controller = loader.getController();

            backButton.setOnAction(e -> navigateAwayFrom(controller));

            hideOptionButton(true);

            String title = "LAB_Options";

            setToolbar(Resources.getString(title), true);
            content.getChildren().setAll(root);
        } catch (IOException e) {
            Logger.error("main-controller switch-to-settings", e);
        }
    }

    public TseksApp getTseksApp() {
        return tseksApp;
    }

    public void showSuccessMessage(String message) {
        UiHelper.showMessage(messagePane, message, false);
    }

    public void showErrorMessage(String message) {
        UiHelper.showMessage(messagePane, message, true);
    }

    public void hideOptionButton(boolean hide) {
        optionButton.setVisible(!hide);
    }
}
