/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.ui.util.views.ImageViewPane;
import com.phicaro.tseks.util.Resources;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class StartupController implements Initializable {

    @FXML
    private StackPane pane;
    @FXML
    private Label title;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        title.setText(Resources.getString("LAB_ApplicationName"));

        Image backgroundImage = Resources.getPreviewBackground();

        ImageView imageView = new ImageView(backgroundImage);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setEffect(new GaussianBlur());
        imageView.setOpacity(0.5);

        ImageViewPane child = new ImageViewPane(imageView);
        StackPane.setMargin(child, new Insets(0, 40, 40, 40));

        pane.getChildren().add(0, child);
    }

}
