package com.example.stickheroapplication;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;



public class PlatformGenerator extends Rectangle {

    private final Random random = new Random();
    private final double screenWidth = 600.0;
    private static final double LAYOUT_Y = 300.0;
    private static final double PLATFORM_HEIGHT = 110.0;
    private static final double minX = 10.0;
    private static final double maxX = 610.0;
    private static final double MIN_WIDTH = 30.0;
    private static final double MAX_WIDTH = 110.0;


    public Rectangle firstPlatform() {
        Rectangle PILLAR = new Rectangle();
        PILLAR.setLayoutX(60.0);
        PILLAR.setWidth(100.0);
        PILLAR.setLayoutY(LAYOUT_Y);
        PILLAR.setFill(Color.BLACK);
        PILLAR.setHeight(PLATFORM_HEIGHT);

        return PILLAR;
    }

    public Rectangle generatePlatform() {
        Rectangle PILLAR = new Rectangle();
        PILLAR.setLayoutY(LAYOUT_Y);
        PILLAR.setHeight(PLATFORM_HEIGHT);
        PILLAR.setWidth(random.nextDouble() * (MAX_WIDTH - MIN_WIDTH) + MIN_WIDTH);
        PILLAR.setFill(Color.BLACK);
        return PILLAR;
    }

}
