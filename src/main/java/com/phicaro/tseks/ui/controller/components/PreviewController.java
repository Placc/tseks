/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.components;

import com.phicaro.tseks.print.Card;
import com.phicaro.tseks.print.PrinterService;
import com.phicaro.tseks.settings.PrintSettings;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.ui.util.views.ImageViewPane;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class PreviewController {

    private ImageViewPane imageView;

    private EventViewModel event;
    private BufferedImage background;
    private PrintSettings settings;
    private PrinterService service;
    private PageFormat format;

    public PreviewController(Pane root) {
        Image backgroundImage = Resources.getPreviewBackground();
        background = SwingFXUtils.fromFXImage(backgroundImage, background);

        settings = MainController.instance().getTseksApp().getSettingsService().getPrintSettings();
        service = MainController.instance().getTseksApp().getPrinterService();

        Paper paper = new Paper();

        paper.setSize(background.getWidth(), background.getHeight());
        paper.setImageableArea(0, 0, background.getWidth(), background.getHeight());

        format = new PageFormat();

        format.setPaper(paper);
        format.setOrientation(PageFormat.PORTRAIT);

        ImageView view = new ImageView();
        view.setSmooth(true);
        view.setPreserveRatio(true);
        InnerShadow shadow = new InnerShadow(2, Color.GRAY);
        view.setEffect(shadow);

        imageView = new ImageViewPane(view);

        VBox.setMargin(imageView, new Insets(20));

        root.getChildren().add(imageView);
    }

    public void setEvent(EventViewModel event) {
        this.event = event;

        event.getTitleProperty().addListener((obj, oldVal, newVal) -> onChanged());
        event.getDescriptionProperty().addListener((obj, oldVal, newVal) -> onChanged());
        event.getLocationProperty().addListener((obj, oldVal, newVal) -> onChanged());
        event.getDateProperty().addListener((obj, oldVal, newVal) -> onChanged());

        onChanged();
    }

    public void onChanged() {
        Platform.runLater(() -> {
            int cardNumber = 1, tableNumber = 1;
            double price = 0.0;
            if (event.getTableGroups().size() > 0) {
                TableCategoryViewModel viewModel = event.getTableGroups().get(event.getTableGroups().size() - 1);

                price = viewModel.getPrice();
                tableNumber = viewModel.getStartNumber();
                cardNumber = 1 + event.getTableGroups().stream()
                        .filter(group -> group.getEndNumber() < viewModel.getStartNumber())
                        .map(group -> (group.getEndNumber() - group.getStartNumber() + 1) * group.getSeats())
                        .reduce(0, (a, b) -> a + b);

            }

            Card card = new Card(cardNumber, event.getTitle(), event.getDescription(), event.getLocation(), UiHelper.parse(event.getDate()), tableNumber, price, null);

            double scale = format.getImageableHeight() / service.getPageFormat(settings.getCardSize()).getImageableWidth();
            card.setScale(scale);

            WritableImage image = new WritableImage((int) format.getImageableWidth(), (int) format.getImageableHeight());
            BufferedImage buffered = new BufferedImage((int) format.getImageableWidth(), (int) format.getImageableHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = buffered.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            try {
                graphics.drawImage(background, 0, 0, null);
                card.print(graphics, format, event.getTableGroups().size() > 0 ? 0 : Card.PREVIEW_NO_CATEGORY_INDEX);
            } catch (Exception e) {
                Logger.error("preview-controller on-changed", e);
            }

            imageView.imageViewProperty().get().setImage(SwingFXUtils.toFXImage(buffered, image));
        }
        );
    }
}
