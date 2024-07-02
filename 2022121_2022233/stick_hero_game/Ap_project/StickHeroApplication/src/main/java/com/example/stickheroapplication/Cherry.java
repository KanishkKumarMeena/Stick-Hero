package com.example.stickheroapplication;

// here flyweight pattern is used to genete the cherry image as creating a new cherry everytime will lead
// to memory wastage and also it will take time to generate the cherry image so we use flyweight pattern
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;

public class Cherry implements CherryPrototype {
    private Image image;
    private static final CherryPrototype cherryFlyweight = CherryImageFactory.getCherryFlyweight();

    public Cherry(String imagePath) {
        this.image = new Image(imagePath);
    }
    public ImageView createCherryView() {
        ImageView cherryView = new ImageView(cherryFlyweight.getImage());

        return cherryView;}

    @Override
    public Cherry clone() {
        return new Cherry("images/cherry.png");
    }

    public Image getImage() {
        return image;
    }

    public static double getHeight() {

        return 20.0;
    }
}