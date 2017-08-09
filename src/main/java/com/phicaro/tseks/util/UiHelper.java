/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util;

import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.util.exceptions.EventAlreadyExistsException;
import com.phicaro.tseks.util.exceptions.LifecycleException;
import com.phicaro.tseks.util.exceptions.PersistenceException;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Placc
 */
public class UiHelper {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(Resources.getConfig("CFG_DateFormat"));
   
   public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate asLocalDate(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }
    
    public static String format(Date date) {
        return formatter.format(date);
    }
    
    public static Date parse(String date) {
        try {
            return formatter.parse(date);
        }catch(Exception e) {
            Logger.error("ui-helper parse", e);
        }
        return null;
    }
    
    private static Single<Alert> createAlert(AlertType type, String title, String message, ButtonType... buttonTypes) {
        return Single.create(s -> {
            Alert alert = new Alert(type, message, buttonTypes);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.getDialogPane().getStylesheets().add(Resources.getStylesheet());
            alert.getDialogPane().getStyleClass().add("root");
            s.onSuccess(alert);
        });
    }

    public static void showException(String title, Throwable e) {
        String message = e.getMessage();
        
        if(e instanceof EventAlreadyExistsException) {
            message = Resources.getString("DESC_EventAlreadyExists");
        } 
        else if (e instanceof LifecycleException) {
            message = Resources.getString("DESC_LifecycleError");
        }
        else if (e instanceof PersistenceException) {
            PersistenceException ex = ((PersistenceException) e);
            
            if(ex.getCause() != null) {
                showException(title, ex.getCause());
                return;
            }
            
            message = Resources.getString("DESC_PersistenceException");
        }
        
        Logger.error(message, e);
        showError(title, message);
    }

    private static void showError(String title, String message) {
        createAlert(AlertType.ERROR, title, message, ButtonType.OK)
                .subscribeOn(JavaFxScheduler.platform())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(alert -> alert.show());
    }
    
    public static Observable<Boolean> showDeleteEventDialog() {
        return createAlert(AlertType.WARNING, Resources.getString("LAB_DeleteEvent"), Resources.getString("DESC_DeleteEvent"), ButtonType.YES, ButtonType.NO)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    
    } 

    public static Observable<Boolean> showReconnectDialog() {
        return createAlert(AlertType.ERROR, Resources.getString("LAB_ConnectionError"), Resources.getString("DESC_ConnectionErrorRetry"), ButtonType.NO, ButtonType.YES)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    }
    
    public static Observable<Boolean> showDiscardChangesDialog() {
        return createAlert(AlertType.WARNING, Resources.getString("LAB_UnsavedChanges"), Resources.getString("DESC_DiscardChanges"), ButtonType.NO, ButtonType.YES)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    }
    
    public static Observable<Boolean> showSaveWarningDialog(List<String> warnings) {
        String message = Resources.getString("DESC_PotentialErrorsFound") + "\n" + warnings.stream().reduce("", (s1, s2) -> s1 + "\n" + s2);
        return createAlert(AlertType.WARNING, Resources.getString("LAB_SaveWithWarnings"), message, ButtonType.NO, ButtonType.YES)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    }
    
    public static Observable<Boolean> showEventExistsDialog() {
        return createAlert(AlertType.CONFIRMATION, Resources.getString("LAB_EventAlreadyExists"), Resources.getString("DESC_RenameEvent"), ButtonType.NO, ButtonType.YES)
                .subscribeOn(JavaFxScheduler.platform())
                .flatMapObservable(alert -> JavaFxObservable.fromDialog((Alert) alert))
                .map(result -> result.equals(ButtonType.YES));
    }
    
    public static String combine(List<String> list) {
        if(list.isEmpty()) {
            return "";
        }
        
        String result = list.get(0);
        
        for(int idx = 1; idx < list.size(); idx++) {
            result += ", " + list.get(idx);
        }
        
        return result;
    }
    
    public static void toggleSpinner(StackPane content, boolean visible) {
        Platform.runLater(() -> {
            if(!visible) {
                content.getChildren().forEach(c -> c.setDisable(false));
                Optional<Node> indicator = content.getChildren().stream().filter(c -> c instanceof VBox).findAny();
                if(indicator.isPresent()) {
                    content.getChildren().remove(indicator.get());
                }
            } else {
                ProgressIndicator pi = new ProgressIndicator();
                VBox box = new VBox(pi);
                box.setAlignment(Pos.CENTER);
                Paint paint = new Color(0.5, 0.5, 0.5, 0.25);
                BackgroundFill fill = new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY);
                Background background = new Background(fill);
                box.backgroundProperty().set(background);
                content.getChildren().stream().forEach(c -> c.setDisable(true));
                content.getChildren().add(box);
            }
        });
    }
    
    public static void showMessage(StackPane header, String message, boolean error) {
        //TODO
    }
    
    public static ColorAdjust getColorAdjust(Color targetColor) {
        ColorAdjust colorAdjust = new ColorAdjust();

        double hue = map((targetColor.getHue() + 180) % 360, 0, 360, -1, 1);
        colorAdjust.setHue(hue);

        double saturation = targetColor.getSaturation();
        colorAdjust.setSaturation(saturation);

        double brightness = map(targetColor.getBrightness(), 0, 1, -1, 0);
        colorAdjust.setBrightness(brightness);

        return colorAdjust;
    }

    private static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
    }
    
    public static boolean isIntersection(int start1, int end1, int start2, int end2) {
        boolean i1 = end2 >= start1 && start2 <= start1;
        boolean i2 = end1 >= start2 && start1 <= start2;
        return i1 || i2;
    }
}
