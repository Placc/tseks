/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.components;

import com.phicaro.tseks.util.ImageViewPane;
import com.phicaro.tseks.ui.controller.IEventController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class PreviewController implements IEventController {

    @FXML
    private StackPane previewRoot;
    @FXML
    private Label previewCardNumber;
    @FXML
    private Label previewTitle;
    @FXML
    private Label previewLocation;
    @FXML
    private Label previewDescription;
    @FXML
    private Label previewDate;
    @FXML
    private Label previewNumberPrice;
    @FXML
    private VBox previewVBox;
    
    private ImageViewPane imageView;
    
    private EventViewModel event;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image image = Resources.getPreviewBackground();
        ImageView view = new ImageView(image);

        view.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        view.setPreserveRatio(true);
        
        imageView = new ImageViewPane(view);
        imageView.boundsInParentProperty().addListener(this::onBoundsChanged);
        
        previewRoot.getChildren().add(0, imageView);
        
        previewRoot.translateYProperty().set(60);
    }
    
    @Override
    public void setEvent(EventViewModel event) {
        this.event = event;
        
        
        previewTitle.textProperty().bind(event.getTitleProperty());
        previewDescription.textProperty().bind(event.getDescriptionProperty());
        previewLocation.textProperty().bind(event.getLocationProperty());
        
        updateDateLabel(event);
        event.getDateProperty().addListener(__ -> updateDateLabel(event));
        
        onChanged();
    }
    
    public void onChanged() {
        updateNumberPriceLabel(event);
        updateCardNumberLabel(event);
    }
    
    private void updateNumberPriceLabel(EventViewModel event) {
        if(event.getTableGroups().size() > 0) {
            TableCategoryViewModel viewModel = event.getTableGroups().get(event.getTableGroups().size() - 1);
            
            String tableNumber = Resources.getString("LAB_TableNumberCard", viewModel.getStartNumber());
            String price = Resources.getString("LAB_PriceCard", viewModel.getPrice());
            String space = "       ".substring(1 + (int) Math.log10(viewModel.getStartNumber()));
            
            previewNumberPrice.setText(tableNumber + space + price);
        } else {
            previewNumberPrice.setText("");
        }
    }
    
    private void updateCardNumberLabel(EventViewModel event) {
        if(event.getTableGroups().size() > 0) {
            TableCategoryViewModel viewModel = event.getTableGroups().get(event.getTableGroups().size() - 1);
            int cardNumber = 1 + event.getTableGroups().stream()
                .filter(group -> group.getEndNumber() < viewModel.getStartNumber())
                .map(group -> group.getNumberOfTables() * group.getSeats())
                .reduce(0, (a, b) -> a + b);
            
            previewCardNumber.setText("" + cardNumber);
        } else {
            previewCardNumber.setText("");
        }
    }
    
    private void updateDateLabel(EventViewModel event) {
        String date = UiHelper.format(UiHelper.parse(event.getDate()), "dd.MM.yy");
        String dateText = Resources.getString("LAB_AtDateCard", date);
        
        String time = UiHelper.format(UiHelper.parse(event.getDate()), "HH:mm");
        String timeText = Resources.getString("LAB_AtTimeCard", time);
        
        previewDate.setText(dateText + "    " + timeText);
    }
    
    private void onBoundsChanged(Object args, Bounds oldVal, Bounds newVal) {
        double size = Math.min(newVal.getHeight(), newVal.getWidth());
        
        setMargin(0.035 * size);
        setFontSize(0.05 * size);
        
        previewCardNumber.translateYProperty().set(0.085 * size);
        previewCardNumber.translateXProperty().set(0.6 * size);
    }
    
    private void setMargin(double size) {
        Insets margin = new Insets(size, 0, 0, 0);

        previewVBox.translateYProperty().set(1.9 * size);
        
        VBox.setMargin(previewTitle, margin);
        VBox.setMargin(previewLocation, margin);
        VBox.setMargin(previewDescription, margin);
        VBox.setMargin(previewDate, margin);
        VBox.setMargin(previewNumberPrice, margin);
    }
    
    private void setFontSize(double size) {
        Font font = new Font("Times New Roman Bold", size);
        
        previewCardNumber.setFont(font);
        previewTitle.setFont(font);
        previewLocation.setFont(font);
        previewDescription.setFont(font);
        previewDate.setFont(font);
        previewNumberPrice.setFont(font);
    }
}
