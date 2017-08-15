/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.util.views;

import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Resources;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 * @author Placc
 */
public class InfoPopup extends Pane {
    
    public InfoPopup(Pane parent, String message, boolean error) {
        super();
        
        Label label = new Label(message);
        label.setFont(new Font("System", 24.0));
        
        Color color = error ? Color.RED : Color.GREEN;
        
        label.setTextFill(color);
        
        ColorAdjust colorAdjust = UiHelper.getColorAdjust(color);
        
        ImageView imageView = new ImageView();
        imageView.setEffect(colorAdjust);
        
        String imgId = error ? "error.png" : "check.png";
        Image image = Resources.getImage(imgId, Resources.ImageSize.LARGE);
        
        imageView.setImage(image);
        
        HBox.setMargin(imageView, new Insets(0, 50, 0, 0));
        
        HBox content = new HBox(imageView, label);
        content.setAlignment(Pos.CENTER);
        
        getChildren().add(content);
        
        this.sceneProperty().addListener((args, oldScene, newScene) -> {
            if(newScene != null) {
                FadeTransition transition = new FadeTransition(Duration.seconds(5), this);
                transition.setToValue(0.0);
                
                transition.setOnFinished(e -> parent.getChildren().remove(this));
                
                transition.play();
            }
        });
    }
}
