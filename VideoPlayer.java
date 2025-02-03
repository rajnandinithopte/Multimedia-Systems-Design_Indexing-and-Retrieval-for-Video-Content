package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


import java.io.File;

public class VideoPlayer extends Application{
    String videoPath;
    int startTime;

    public VideoPlayer(String videoPath, int startTime) {
        this.videoPath = "src/main/resources/"+videoPath+".mp4";
        this.startTime = startTime;
    }

    public VideoPlayer() {
        this.videoPath = "src/main/resources/video1.mp4";
        this.startTime = 10;
    }

    public void play(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            this.videoPath = "src/main/resources/"+getParameters().getRaw().get(0)+".mp4";
            this.startTime  = Integer.parseInt(getParameters().getRaw().get(1));
        }catch (Exception e){
            System.out.println("No arguments provided, using default values");
        }
        Duration startTime = Duration.seconds(this.startTime);
        // Instantiating Media class
        Media media = new Media(new File(this.videoPath).toURI().toString());

        // Instantiating MediaPlayer class
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Set the initial position to startTime
        mediaPlayer.seek(startTime);

        // Instantiating MediaView class
        MediaView mediaView = new MediaView(mediaPlayer);

        // setting group and scene
        Group root = new Group();
        root.getChildren().add(mediaView);

        // Create buttons
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

        // Set actions for buttons
        playButton.setOnAction(e -> mediaPlayer.play());
        pauseButton.setOnAction(e -> mediaPlayer.pause());
        resetButton.setOnAction(e -> {
            mediaPlayer.seek(startTime);
            mediaPlayer.play();
        });

        // Create a layout for buttons
        HBox buttonBox = new HBox(10, playButton, pauseButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setLayoutX(0);
        buttonBox.setLayoutY(288);
        buttonBox.setPrefHeight(50);
        buttonBox.setPrefWidth(352);

        // Add buttons to the root
        root.getChildren().addAll(buttonBox);

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.seek(startTime);
            mediaPlayer.play();
        });

        // Create scene with adjusted height
        Scene scene = new Scene(root, 352, 350); // Adjusted height to accommodate buttons
        primaryStage.setScene(scene);

        primaryStage.setTitle(this.videoPath);
        primaryStage.show();
    }
    
    public static void main(String args[]) { 
        launch(args); 
    }
}
