package com.example.stickheroapplication;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CherryGenerator {

    private static List<Cherry> cherries=null;
    private static Random random=null;

    public CherryGenerator() {
        this.cherries = new ArrayList<>();
        this.random = new Random();
    }

    public static List<Cherry> generateCherries(Rectangle pillar1, Rectangle pillar2, int numberOfCherries) {
        cherries.clear();

        for (int i = 0; i < numberOfCherries; i++) {
            double minX = Math.min(pillar1.getLayoutX(), pillar2.getLayoutX());
            double maxX = Math.max(pillar1.getLayoutX() + pillar1.getWidth(), pillar2.getLayoutX() + pillar2.getWidth());

            double cherryX = random.nextDouble() * (maxX - minX) + minX;
            double cherryY = random.nextDouble() * (pillar1.getLayoutY() - Cherry.getHeight());

            Cherry cherry = new Cherry("images/cherry.png");
            cherries.add(cherry);

            ImageView cherryView = new ImageView(cherry.getImage());
            cherryView.setLayoutX(cherryX);
            cherryView.setLayoutY(cherryY);

            // Add cherry to the scene or a container
            // For example, you can add it to a Pane
            Pane gameScreen = new Pane();
            gameScreen.getChildren().add(cherryView);
        }

        return cherries;
    }
}
