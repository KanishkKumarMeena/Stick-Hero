package com.example.stickheroapplication;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainGameController {

    @FXML
    Pane gamePane;

    @FXML
    Label score;
    @FXML
    ImageView character;

    boolean isExtending = false;

    private Rectangle prevPlatform;
    private Line stick;
    boolean isTranslating = false;
    double stickLength = 0.0;
    private boolean ischaracterfacingup = true;
    final ArrayList<Rectangle> platforms = new ArrayList<>();
    double angle = 270.0;
    Rectangle currentplatform;


    SoundManager soundManager = SoundManager.getInstance();


    private AnimationTimer stickExtension;

    private Timeline timeline;

    private final PlatformGenerator platformGenerator = new PlatformGenerator();
    @FXML
    Pane gameOver = new Pane();

    Random random = new Random();

    @FXML
    public void initialize() {
        Rectangle initPlatform = platformGenerator.firstPlatform();
        gamePane.getChildren().add(initPlatform);
        prevPlatform = initPlatform;
        currentplatform = initPlatform;
        platforms.add(initPlatform);
        startGeneratingPlatforms();
        gamePane.setOnMousePressed(event -> {
            if (!isTranslating && !isExtending) {
                isExtending = true;
                handleMousePressed();
            }
        });
        gamePane.setOnMouseReleased(event -> {
            if (isExtending) {
                isExtending = false;
                handleMouseReleased();
            }
        });
        BackgroundFill backgroundFill = new BackgroundFill(Color.WHITE,new CornerRadii(10),new Insets(10));
        Background background = new Background(backgroundFill);
        gameOver.setBackground(background);
        gameOver.setOpacity(0);
    }

    private void startGeneratingPlatforms() {
        javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(0.1),
                event -> generatePlatform()
        );
        timeline = new javafx.animation.Timeline(keyFrame);
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private void stopGeneratingPlatforms() {
        //stop the platform generator
        timeline.stop();
    }

    private void generatePlatform() {
        Rectangle platform = platformGenerator.generatePlatform();
        double minSeperation = 30.0;
        double minLayoutX = prevPlatform.getLayoutX() + prevPlatform.getWidth() + minSeperation;
        double maxDist = 150.0;
        double maxLayoutX = prevPlatform.getLayoutX() + prevPlatform.getWidth() + maxDist;
        double layoutX = random.nextDouble() * (maxLayoutX - minLayoutX) + minLayoutX;
        platform.setLayoutX(layoutX);
        gamePane.getChildren().add(platform);
        platforms.add(platform);
        prevPlatform = platform;
    }


    public void handleMousePressed() {
        double pivotX = currentplatform.getLayoutX() + currentplatform.getWidth();
        double pivotY = currentplatform.getLayoutY();
        stick = new Line(pivotX, pivotY, pivotX, pivotY);
        stick.setStroke(Color.BLACK);
        stick.setStrokeWidth(4.0);
        gamePane.getChildren().add(stick);
        soundManager.playStickGrowSound();

        final long[] startTime = { System.nanoTime() };

        stickExtension = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsedTime = (now - startTime[0]) / 1e9;
                if (elapsedTime > 0.1) {
                    stickLength += 10;
                    stick.setEndY(stick.getStartY() - stickLength);
                    startTime[0] = System.nanoTime();
                }
            }
        };

        ischaracterfacingup = !ischaracterfacingup;

        stickExtension.start();
    }



    public void handleMouseReleased() {
        //stop the animation timer
        stickExtension.stop();
        rotateStick();
    }
    private void flipCharacter() {
        double characterScaleY = character.getScaleY();
        double originalCharacterHeight = character.getBoundsInParent().getHeight();
        double stickHeight = stick.getBoundsInParent().getWidth();

        // Flip the player vertically
        character.setScaleY(-characterScaleY);

        double newY;
        if (ischaracterfacingup) {
            // Adjust the player's position when facing up
            newY = stick.getEndY() - stickHeight + originalCharacterHeight;
        } else {
            // Adjust the player's position when facing down
            newY = stick.getEndY() - originalCharacterHeight;
        }

        character.setLayoutY(newY);

        // Toggle the flag for player direction
        ischaracterfacingup = !ischaracterfacingup;
    }
private void stik(){return;}
    private void rotateStick() {
        //rotate stick by 90 degrees by adding setX and setY using timeline
        double centerX = stick.getStartX();
        double centerY = stick.getStartY();

        Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(40), e -> {
            angle += 5;

            double angleInRadians = Math.toRadians(angle);
            double endX = centerX + stickLength * Math.cos(angleInRadians);
            double endY = centerY + stickLength * Math.sin(angleInRadians);
            stik();
            stick.setStartX(centerX);
            stick.setEndX(endX);
            stick.setStartY(centerY);

            stick.setEndY(endY);

        }));
        timeline.setCycleCount(18);
        timeline.play();
        //pause till stick animation completes
        timeline.setOnFinished(event -> doesStickReach());
    }

    private void doesStickReach() {

        double currPlatformEndX = currentplatform.getLayoutX() + currentplatform.getWidth();
        double nextPlatformStartX = platforms.get(platforms.indexOf(currentplatform) + 1).getLayoutX();
        double nextPlatformEndX = nextPlatformStartX + platforms.get(platforms.indexOf(currentplatform) + 1).getWidth();
        if (stickLength >= nextPlatformStartX - currPlatformEndX && stickLength <= nextPlatformEndX - currPlatformEndX) {
            moveToNext();
            soundManager.playStickFallSound();
        } else {
            playerFalled();
        }
    }

    void moveToNext() {
        isTranslating = true;
        Rectangle previousPlatform = currentplatform;
        int nextPlatformIndex = platforms.indexOf(currentplatform) + 1;
        currentplatform = platforms.get(nextPlatformIndex);
        //Animate the character to move to the next platform
        double characterX = character.getLayoutX();
        double fixedCharDist = 55.0;
        double characterEndX = currentplatform.getLayoutX() + currentplatform.getWidth() - fixedCharDist;
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), character);
        tt.setToX(characterEndX - characterX);
        tt.play();
        tt.setOnFinished(event -> {
            isTranslating = false;
            if (score.getText().equals("00")){
                score.setText("01");
            } //check if score is less than or equal to 09
            else if (Integer.parseInt(score.getText()) <= 9) {
                int currScore = Integer.parseInt(score.getText());
                currScore++;
                score.setText(String.format("%02d", currScore)); // 2 digits
            } else{
                int currScore = Integer.parseInt(score.getText());
                currScore++;
                score.setText(currScore + "");
            }
            gamePane.getChildren().remove(stick);
            stickLength = 0.0;
            angle = 270.0;
            double shiftAmount = currentplatform.getLayoutX() - previousPlatform.getLayoutX();
            character.setLayoutX(character.getLayoutX() - shiftAmount);
            for (Rectangle platform : platforms) {
                platform.setLayoutX(platform.getLayoutX() - shiftAmount);
            }
        });
    }
    private void adjustStickLengthToZero() {
        gamePane.getChildren().remove(stick);
        stickLength = 0.0;
        angle = 270.0;
    }
    private void playerFalled() {
        stopGeneratingPlatforms();
        //move player till end of stick and then fall
        double characterX = character.getLayoutX();
        TranslateTransition translateTransition
                = new TranslateTransition(Duration.millis(500), character);
        translateTransition.setToX(stick.getEndX() - characterX - 30);
        translateTransition.play();
        translateTransition.setOnFinished(event -> {
            adjustStickLengthToZero();

            try {
                playerFalls();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
    @FXML
    Label score1 = new Label();
    @FXML
    Label highScoreLabel = new Label();
    public void gameOverScreen() throws IOException {
        String scoreLabel = score.getText();
        gameOver.toFront();
        String currentHighscore;
        String highscore = getHighscore();
        if (Integer.parseInt(scoreLabel)>Integer.parseInt(highscore)){
            Path fileName = Path.of("C:\\Users\\Aman\\Downloads\\2022121_2022233\\2022121_2022233 (2)\\2022121_2022233\\stick_hero_game\\Ap_project\\StickHeroApplication\\src\\main\\java\\com\\example\\stickheroapplication\\HighScore.txt");
            Files.write(fileName,scoreLabel.getBytes());
            currentHighscore = scoreLabel;
        } else {
            currentHighscore = highscore;
        }
        highScoreLabel.setText(currentHighscore);
        score1.setText(scoreLabel);
        gameOver.setOpacity(1);
    }

    @FXML
    public void restartButtonClicked(ActionEvent event) throws IOException {
        DisplayController sceneController = new DisplayController();
        sceneController.goToMainGame(event);
    }

    private void playerFalls() throws IOException {
        double characterY = character.getLayoutY();
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), character);
        translateTransition.setToY(characterY + 249);
        translateTransition.play();
        translateTransition.setOnFinished(event -> soundManager.playPlayerFallSound());
        gameOverScreen();
    }
    public String getHighscore() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("C:\\Users\\Aman\\Downloads\\2022121_2022233\\2022121_2022233 (2)\\2022121_2022233\\stick_hero_game\\Ap_project\\StickHeroApplication\\src\\main\\java\\com\\example\\stickheroapplication\\HighScore.txt"));
        String highscore = sc.nextLine();
        sc.close();
        return highscore;
    }




}

