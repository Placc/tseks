/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.components;

import com.phicaro.tseks.database.IDatabaseInitializer;
import com.phicaro.tseks.database.TseksDatabaseFactory;
import com.phicaro.tseks.print.PageSize;
import com.phicaro.tseks.print.PrinterService;
import com.phicaro.tseks.settings.DatabaseSettings;
import com.phicaro.tseks.settings.InvalidPageSizeException;
import com.phicaro.tseks.settings.PrintSettings;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
import io.reactivex.Observable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * FXML Controller class
 *
 * @author Carola
 */
public class SettingsController extends Dialog implements Initializable {

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
    @FXML
    private Button clipboardButton;

    private PrinterService printerService = MainController.instance().getTseksApp().getPrinterService();
    private PrintSettings printSettings = MainController.instance().getTseksApp().getSettingsService().getPrintSettings();
    private DatabaseSettings databaseSettings = MainController.instance().getTseksApp().getSettingsService().getDatabaseSettings();

    private Button settingsSaveButton;
    private Button settingsDiscardButton;

    public SettingsController() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Settings.fxml"));
            loader.setController(this);

            getDialogPane().setContent(loader.load());
        } catch (Exception e) {
            Logger.error("print-from-to-dialog-controller constructor", e);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setTitle(Resources.getString("LAB_Options"));

        ButtonType saveType = new ButtonType(Resources.getString("LAB_Save"));
        ButtonType discardType = new ButtonType(Resources.getString("LAB_Discard"));

        this.getDialogPane().getStylesheets().add(Resources.getStylesheet());
        this.getDialogPane().getButtonTypes().add(saveType);
        this.getDialogPane().getButtonTypes().add(discardType);

        settingsSaveButton = (Button) this.getDialogPane().lookupButton(saveType);
        settingsSaveButton.setGraphic(new ImageView(Resources.getImage("save.png", Resources.ImageSize.NORMAL)));
        settingsSaveButton.getStyleClass().add("success");
        settingsSaveButton.setOnAction(evt -> onSaveClicked());

        settingsDiscardButton = (Button) this.getDialogPane().lookupButton(discardType);
        settingsDiscardButton.setGraphic(new ImageView(Resources.getImage("clear.png", Resources.ImageSize.NORMAL)));
        settingsDiscardButton.getStyleClass().add("danger");

        enableButtons(false);

        settingsPaperSizeLabel.setText(Resources.getString("LAB_SelectPaperSize"));
        settingsPrinterLabel.setText(Resources.getString("LAB_SelectPrinter"));
        settingsDatabaseLabel.setText(Resources.getString("LAB_SelectDatabase"));

        clipboardButton.setText(Resources.getString("LAB_CopyDatabaseToClipboard"));
        clipboardButton.setOnAction(e -> copyDatabaseToClipboard());

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

    private void copyDatabaseToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        List<File> filesList = new ArrayList<>();

        IDatabaseInitializer initializer = TseksDatabaseFactory.getDatabase(databaseSettings.getDatabaseType());
        filesList.add(initializer.databaseFile());

        content.putFiles(filesList);
        clipboard.setContent(content);
    }

    private void enableButtons(boolean enable) {
        settingsSaveButton.setDisable(!enable);
    }

}
