/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.print.PageSize;
import com.phicaro.tseks.print.PrinterService;
import com.phicaro.tseks.settings.DatabaseSettings;
import com.phicaro.tseks.settings.InvalidPageSizeException;
import com.phicaro.tseks.settings.PrintSettings;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Resources;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Carola
 */
public class SettingsController implements INavigationController, Initializable {

    @FXML
    private Button settingsSaveButton;
    @FXML
    private Button settingsDiscardButton;
    @FXML
    private Label settingsPaperSizeLabel;
    @FXML
    private ChoiceBox<String> settingsPaperSizeChoiceBox;
    @FXML
    private Label settingsPrinterLabel;
    @FXML
    private ChoiceBox<String> settingsPrinterChoiceBox;
    @FXML
    private Label settingsDatabaseLabel;

    private static PrinterService printerService = MainController.instance().getTseksApp().getPrinterService();
    private static PrintSettings printSettings = MainController.instance().getTseksApp().getSettingsService().getPrintSettings();
    private static DatabaseSettings databaseSettings = MainController.instance().getTseksApp().getSettingsService().getDatabaseSettings();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        settingsSaveButton.setText(Resources.getString("LAB_Save"));
        settingsSaveButton.setGraphic(new ImageView(Resources.getImage("save.png", Resources.ImageSize.NORMAL)));
        settingsSaveButton.setOnAction(e -> onSaveClicked());

        settingsDiscardButton.setText(Resources.getString("LAB_Discard"));
        settingsDiscardButton.setGraphic(new ImageView(Resources.getImage("clear.png", Resources.ImageSize.NORMAL)));
        settingsDiscardButton.setOnAction(e -> onDiscardClicked());

        enableButtons(false);

        settingsPaperSizeLabel.setText(Resources.getString("LAB_SelectPaperSize"));
        settingsPrinterLabel.setText(Resources.getString("LAB_SelectPrinter"));
        settingsDatabaseLabel.setText(Resources.getString("LAB_SelectDatabase"));

        loadSettings();
    }

    private void loadSettings() {
        settingsPaperSizeChoiceBox.setValue(printSettings.getPageSize().name());
        settingsPaperSizeChoiceBox.valueProperty().addListener((args, oldVal, newVal) -> enableButtons(hasChanges()));

        ObservableList<String> pageSizesList = FXCollections.observableArrayList(Observable.fromArray(PageSize.values())
                .filter(pageSize -> !pageSize.smaller(printSettings.getCardSize()))
                .map(pageSize -> pageSize.name())
                .toList()
                .blockingGet());
        settingsPaperSizeChoiceBox.setItems(pageSizesList);

        settingsPrinterChoiceBox.setValue(printSettings.getPrinter());
        settingsPrinterChoiceBox.valueProperty().addListener((args, oldVal, newVal) -> enableButtons(hasChanges()));

        ObservableList<String> printerList = null;
        try {
            printerList = FXCollections.observableArrayList(printerService.listPrinters());
        } catch (PrinterException ex) {
        }

        settingsPrinterChoiceBox.setItems(printerList);
    }

    @Override
    public Single<Boolean> onNavigateAway() {
        if (hasChanges() && !UiHelper.showDiscardChangesDialog().blockingFirst()) {
            return Single.just(false);
        }
        return Single.just(true);
    }

    private boolean hasChanges() {

        return !PageSize.valueOf(settingsPaperSizeChoiceBox.getValue()).equals(printSettings.getPageSize())
                || !settingsPrinterChoiceBox.getValue().equals(printSettings.getPrinter());
    }

    private void onSaveClicked() {
        try {
            printSettings.setPageSize(PageSize.valueOf(settingsPaperSizeChoiceBox.getValue()));
        } catch (InvalidPageSizeException ex) {
            UiHelper.showException(Resources.getString("LAB_ErrorOccured"), ex);
        }

        printSettings.setPrinter(settingsPrinterChoiceBox.getValue());

        try {
            MainController.instance().getTseksApp().getSettingsService().saveSettings();
        } catch (IOException ex) {
            UiHelper.showException("LAB_ErrorOccured", ex);
        }
        enableButtons(hasChanges());
    }

    private void onDiscardClicked() {
        MainController.instance().navigateAwayFrom(this);
    }

    private void enableButtons(boolean enable) {
        settingsSaveButton.setDisable(!enable);
        settingsDiscardButton.setDisable(!enable);
    }

}
