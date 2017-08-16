/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.components;

import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class PrintFromToDialogController extends Dialog<Pair<Integer, Integer>> implements Initializable {

    @FXML
    private Label printFromLabel;
    @FXML
    private TextField printFromEditText;
    @FXML
    private Label printToLabel;
    @FXML
    private TextField printToEditText;

    private int minCardNumber;
    private int maxCardNumber;
    
    public PrintFromToDialogController(int minCardNumber, int maxCardNumber) {
        super();

        this.minCardNumber = minCardNumber;
        this.maxCardNumber = maxCardNumber;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/PrintFromToDialog.fxml"));
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
        this.setTitle(Resources.getString("LAB_PrintFromTo"));

        ButtonType printType = new ButtonType(Resources.getString("LAB_Print"));
        
        this.getDialogPane().getStylesheets().add(Resources.getStylesheet());
        this.getDialogPane().getButtonTypes().add(printType);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        this.setHeaderText(Resources.getString("LAB_CardNumbers"));
        
        printFromLabel.setText(Resources.getString("LAB_From"));
        printToLabel.setText(Resources.getString("LAB_To"));
        
        printFromEditText.setText("" + minCardNumber);
        printToEditText.setText("" + maxCardNumber);
        
        Button printButton = (Button) this.getDialogPane().lookupButton(printType);
        printButton.setGraphic(new ImageView(Resources.getImage("print.png", Resources.ImageSize.NORMAL)));
        printButton.getStyleClass().add("success");
        
        ChangeListener<String> changeListener = (args, oldVal, newVal) -> {
            String valid = printFromEditText.getText().replaceAll("[^\\d]", "");
            printFromEditText.setText(valid);

            valid = printToEditText.getText().replaceAll("[^\\d]", "");
            printToEditText.setText(valid);

            if(printFromEditText.getText().isEmpty() || printToEditText.getText().isEmpty()) {
                printButton.setDisable(true);
                return;
            } 

            int from = Integer.parseInt(printFromEditText.getText());
            int to = Integer.parseInt(printToEditText.getText());

            if(from < minCardNumber || to > maxCardNumber || from > to) {
                printButton.setDisable(true);
                return;
            }

            printButton.setDisable(false);
        };
        
        printFromEditText.textProperty().addListener(changeListener);
        printToEditText.textProperty().addListener(changeListener);
        
        setResultConverter((param) -> {
            if(param.equals(printType)) {
                int from = Integer.parseInt(printFromEditText.getText());
                int to = Integer.parseInt(printToEditText.getText());
                
                return new Pair<Integer, Integer>(from, to);
            } 
            
            return null;
        });
    }    
}
