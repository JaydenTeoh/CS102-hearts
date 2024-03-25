package com.example.app;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardImageView extends ImageView {
    private Image faceUpImage;
    private Image faceDownImage;
    private boolean isFaceUp;

    public CardImageView(Image faceUpImage, Image faceDownImage) {
        super(faceDownImage); // Start face down by default
        this.faceUpImage = faceUpImage;
        this.faceDownImage = faceDownImage;
        this.isFaceUp = false;
    }

    public void setImage(boolean isFaceUp) {
        this.setImage(isFaceUp ? faceUpImage : faceDownImage);
        this.isFaceUp = isFaceUp;
    }
}
