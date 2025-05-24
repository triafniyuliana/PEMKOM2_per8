/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package phbmediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author FLEX5
 */
public class FXMLDocumenController implements Initializable {
       private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private StackPane sPane;

    @FXML
    private Button pausePlay;

    @FXML
    private Slider seek;

    @FXML
    private Slider volume;

    @FXML
    BorderPane bPane;

    List<String> playlist = new ArrayList<>();
    List<String> sourceName = new ArrayList<>();
    static int INDEX_PLAY = 0;

    @FXML
    private void openFiles(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
            "Media File", ".mp4", ".mp3");
        fc.getExtensionFilters().add(filter);
        List<File> f = fc.showOpenMultipleDialog(null);
        if (f != null && !f.isEmpty()) {
            if (!playlist.isEmpty()) {
                playlist.clear();
                sourceName.clear();
            }

            for (int i = 0; i < f.size(); i++) {
                playlist.add(f.get(i).toURI().toString());
                sourceName.add(f.get(i).getName());
            }

            INDEX_PLAY = 0;
            playMedia(INDEX_PLAY);
        }
    }

    private void playMedia(int index) {
        String source = playlist.get(index);
        Media media = new Media(source);

        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }

        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        volume.setValue(50);
        volume.valueProperty().addListener((Observable observable) -> {
            mediaPlayer.setVolume(volume.getValue() / 100);
        });

        mediaPlayer.currentTimeProperty().addListener((Observable observable) -> {
            Duration current = mediaPlayer.getCurrentTime();
            Duration total = mediaPlayer.getTotalDuration();
            seek.setValue(current.toSeconds() / total.toSeconds() * 100);
        });

        seek.setOnMouseClicked((MouseEvent event1) -> {
            double seekTime = mediaPlayer.getTotalDuration().toSeconds() * seek.getValue() / 100;
            mediaPlayer.seek(Duration.seconds(seekTime));
        });

        mediaPlayer.play();
//        Image imagePause = new Image(getClass().getResourceAsStream("/images/pause.png"));
//        pausePlay.setGraphic(new ImageView(imagePause));
    }

    @FXML
    private void seekBackward(ActionEvent event) {
        mediaPlayer.seek(Duration.seconds(0.5));
    }

    @FXML
    private void backward(ActionEvent event) {
        if (INDEX_PLAY > 0) {
            INDEX_PLAY--;
            playMedia(INDEX_PLAY);
        }
    }

    @FXML
    private void stop(ActionEvent event) {
        mediaPlayer.stop();
//        Image imagePlay = new Image(getClass().getResourceAsStream("/images/pause.png"));
//        pausePlay.setGraphic(new ImageView(imagePlay));
        INDEX_PLAY = 0;
    }

    @FXML
    private void pausePlay(ActionEvent event) {
        if (playlist.isEmpty()) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("INFO");
            dialog.setContentText("Playlist kosong. Silakan buka file terlebih dahulu.");
            dialog.getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.CLOSE);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.show();
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
//            Image imagePause = new Image(getClass().getResourceAsStream("/images/pause.png"));
//            pausePlay.setGraphic(new ImageView(imagePause));
        } else {
            mediaPlayer.play();
//            Image imagePlay = new Image(getClass().getResourceAsStream("/images/pause.png"));
//            pausePlay.setGraphic(new ImageView(imagePlay));
        }
    }

    @FXML
    private void forward(ActionEvent event) {
        if (INDEX_PLAY < playlist.size() - 1) {
            INDEX_PLAY++;
            playMedia(INDEX_PLAY);
        }
    }

    @FXML
    private void seekForward(ActionEvent event) {
        mediaPlayer.setRate(1.5);
    }


    /**
     * Initializes the controller class.
     * @param url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code if needed
    }
}
