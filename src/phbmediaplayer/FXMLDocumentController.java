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
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {

    private MediaPlayer mediaPlayer;

    @FXML private MediaView mediaView;
    @FXML private StackPane sPane;
    @FXML private Button playPause;
    @FXML private Slider volume;
    @FXML private Slider seek;
    @FXML private BorderPane bPane;
    @FXML private ListView<String> playlistView;

    private final List<String> playlist = new ArrayList<>();
    private final List<String> sourceName = new ArrayList<>();
    private int INDEX = 0;
    private int PLAY = 0;

    @FXML
    private void openFiles(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP3 Files (*.mp3)", "*.mp3");
        fc.getExtensionFilters().add(filter);
        List<File> files = fc.showOpenMultipleDialog(null);

        if (files != null && !files.isEmpty()) {
            boolean firstTime = playlist.isEmpty(); // hanya play otomatis jika playlist kosong

            for (File file : files) {
                String uri = file.toURI().toString();
                if (!playlist.contains(uri)) {
                    playlist.add(uri);
                    sourceName.add(file.getName());
                    playlistView.getItems().add(file.getName());
                }
            }

            if (firstTime) {
                INDEX = 0;
                playlistView.getSelectionModel().select(INDEX);
                playMedia(INDEX);
            }
        }
    }

    private void playMedia(int index) {
        if (index < 0 || index >= playlist.size()) return;

        String source = playlist.get(index);
        Media media = new Media(source);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        mediaPlayer = new MediaPlayer(media);

        // Tidak menampilkan video karena hanya MP3
        mediaView.setMediaPlayer(null);

        volume.setValue(50);
        volume.valueProperty().addListener((Observable observable) -> {
            mediaPlayer.setVolume(volume.getValue() / 100);
        });

        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable,
                Duration oldValue, Duration newValue) -> {
            seek.setValue(newValue.toSeconds());
        });

        mediaPlayer.setOnReady(() -> {
            seek.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
        });

        seek.setOnMouseClicked(event1 -> mediaPlayer.seek(Duration.seconds(seek.getValue())));
        seek.setOnMouseDragged(event1 -> mediaPlayer.seek(Duration.seconds(seek.getValue())));

        mediaPlayer.play();
        PLAY = 1;
        playPause.setText("Pause");
    }

    @FXML private void seekBackward(ActionEvent event) {
        if (mediaPlayer != null) mediaPlayer.setRate(0.5);
    }

    @FXML private void backward(ActionEvent event) {
        if (INDEX > 0) {
            INDEX--;
            playlistView.getSelectionModel().select(INDEX);
            playMedia(INDEX);
        }
    }

    @FXML private void stop(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            PLAY = 0;
            playPause.setText("Play");
        }
    }

    @FXML private void pausePlay(ActionEvent event) {
        if (!playlist.isEmpty()) {
            if (PLAY == 1) {
                mediaPlayer.pause();
                playPause.setText("Play");
                PLAY = 0;
            } else {
                mediaPlayer.play();
                playPause.setText("Pause");
                PLAY = 1;
            }
        } else {
            showAlert("Please open an MP3 file first.");
        }
    }

    @FXML private void forward(ActionEvent event) {
        if (INDEX < playlist.size() - 1) {
            INDEX++;
            playlistView.getSelectionModel().select(INDEX);
            playMedia(INDEX);
        }
    }

    @FXML private void seekForward(ActionEvent event) {
        if (mediaPlayer != null) mediaPlayer.setRate(1.5);
    }

    @FXML private void handlePlaylistClick(MouseEvent event) {
        int selectedIndex = playlistView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < playlist.size()) {
            INDEX = selectedIndex;
            playMedia(INDEX);
        }
    }

    private void showAlert(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Message");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.initStyle(StageStyle.DECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}




