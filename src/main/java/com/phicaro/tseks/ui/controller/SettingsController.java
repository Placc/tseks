/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.database.DatabaseType;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    @FXML
    private RadioButton settingsDatabaseSQLiteRadioButton;
    @FXML
    private RadioButton settingsDatabasePostgreSqlRadioButton;
    @FXML
    private Label settingsDatabaseUrlLabel;
    @FXML
    private Label settingsDatabaseUserNameLabel;
    @FXML
    private Label settingsDatabasePasswordLabel;
    @FXML
    private TextField settingsDatabaseUrlTextField;
    @FXML
    private TextField settingsDatabaseUserNameTextField;
    @FXML
    private TextField settingsDatabasePasswordTextField;
    @FXML
    private Label settingsDatabaseChangeLabel;

    private static PrinterService printerService = MainController.instance().getTseksApp().getPrinterService();
    private static PrintSettings printSettings = MainController.instance().getTseksApp().getSettingsService().getPrintSettings();
    private static DatabaseSettings databaseSettings = MainController.instance().getTseksApp().getSettingsService().getDatabaseSettings();
    private ToggleGroup radioButtonGroup;

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

        settingsDatabaseUrlLabel.setText(Resources.getString("LAB_DatabaseURL"));
        settingsDatabaseUserNameLabel.setText(Resources.getString("LAB_DatabaseUserName"));
        settingsDatabasePasswordLabel.setText(Resources.getString("LAB_DatabasePassword"));

        settingsDatabaseSQLiteRadioButton.setText(Resources.getString("LAB_SqliteDatabase"));
        settingsDatabasePostgreSqlRadioButton.setText(Resources.getString("LAB_PostgresqlDatabase"));

        //TODO remove
        settingsDatabasePostgreSqlRadioButton.setDisable(true);
        
        settingsDatabaseChangeLabel.setText("");

        loadSettings();

        radioButtonGroup = new ToggleGroup();
        settingsDatabasePostgreSqlRadioButton.setToggleGroup(radioButtonGroup);
        settingsDatabaseSQLiteRadioButton.setToggleGroup(radioButtonGroup);

        radioButtonGroup.selectedToggleProperty().addListener(toggle -> {
            disablePostgresqlEditText(radioButtonGroup.getSelectedToggle().equals(settingsDatabaseSQLiteRadioButton));
            if (!mapDatabaseType(radioButtonGroup.getSelectedToggle())) {
                settingsDatabaseChangeLabel.setText(Resources.getString("LAB_WarningDatabaseChange"));
            } else {
                settingsDatabaseChangeLabel.setText("");
            }
            enableButtons(hasChanges());
        });

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

        Boolean sqLiteDatabase = databaseSettings.getDatabaseType().equals(DatabaseType.SQLite);
        settingsDatabaseSQLiteRadioButton.setSelected(sqLiteDatabase);
        settingsDatabasePostgreSqlRadioButton.setSelected(!sqLiteDatabase);

        if (!sqLiteDatabase) {
            settingsDatabaseUrlTextField.setText(databaseSettings.getConnection());
            settingsDatabaseUrlTextField.visibleProperty().addListener((args, oldVal, newVal) -> enableButtons(hasChanges()));
            settingsDatabaseUserNameTextField.setText(databaseSettings.getUser());
            settingsDatabaseUserNameTextField.visibleProperty().addListener((args, oldVal, newVal) -> enableButtons(hasChanges()));
            settingsDatabasePasswordTextField.setText(databaseSettings.getPassword());
            settingsDatabasePasswordTextField.visibleProperty().addListener((args, oldVal, newVal) -> enableButtons(hasChanges()));
        } else {
            disablePostgresqlEditText(true);
        }

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
                || !settingsPrinterChoiceBox.getValue().equals(printSettings.getPrinter())
                || !settingsDatabaseUrlTextField.getText().equals(databaseSettings.getConnection())
                || !settingsDatabaseUserNameTextField.getText().equals(databaseSettings.getUser())
                || !settingsDatabasePasswordTextField.getText().equals(databaseSettings.getPassword())
                || !mapDatabaseType(radioButtonGroup.getSelectedToggle());
    }

    private void onSaveClicked() {
        try {
            printSettings.setPageSize(PageSize.valueOf(settingsPaperSizeChoiceBox.getValue()));
        } catch (InvalidPageSizeException ex) {
            UiHelper.showException(Resources.getString("LAB_ErrorOccured"), ex);
        }

        printSettings.setPrinter(settingsPrinterChoiceBox.getValue());

        Toggle selected = radioButtonGroup.getSelectedToggle();
        if (radioButtonGroup.getSelectedToggle().equals(settingsDatabaseSQLiteRadioButton)) {
            databaseSettings.setDatabaseType(DatabaseType.SQLite);
        } else {
            databaseSettings.setDatabaseType(DatabaseType.PostgreSQL);
            databaseSettings.setConnection(settingsDatabaseUrlTextField.getText());
            databaseSettings.setUser(settingsDatabaseUserNameTextField.getText());
            databaseSettings.setPassword(settingsDatabasePasswordTextField.getText());
        }

        try {
            MainController.instance().getTseksApp().getSettingsService().saveSettings();
        } catch (IOException ex) {
            UiHelper.showException("LAB_ErrorOccured", ex);
        }
        enableButtons(hasChanges());
    }

    private void disablePostgresqlEditText(boolean bool) {
        settingsDatabaseUrlLabel.setDisable(bool);
        settingsDatabaseUserNameLabel.setDisable(bool);
        settingsDatabasePasswordLabel.setDisable(bool);
        settingsDatabaseUrlTextField.setDisable(bool);
        settingsDatabaseUrlTextField.clear();
        settingsDatabaseUserNameTextField.setDisable(bool);
        settingsDatabaseUserNameTextField.clear();
        settingsDatabasePasswordTextField.setDisable(bool);
        settingsDatabasePasswordTextField.clear();
    }

    private void onDiscardClicked() {
        MainController.instance().navigateAwayFrom(this);
    }

    private boolean mapDatabaseType(Toggle tog) {
        DatabaseType type = databaseSettings.getDatabaseType();
        switch (type) {
            case SQLite:
                return tog.equals(settingsDatabaseSQLiteRadioButton);

            case PostgreSQL:
                return tog.equals(settingsDatabasePostgreSqlRadioButton);

            default:
                return false;
        }
    }
    
    private void enableButtons(boolean enable) {
        settingsSaveButton.setDisable(!enable);
        settingsDiscardButton.setDisable(!enable);
    }

}
