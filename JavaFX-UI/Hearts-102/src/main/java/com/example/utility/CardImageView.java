package com.example.utility;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardImageView extends ImageView {
    private Image faceUpImage;
    private Image faceDownImage;

    public CardImageView(Image faceUpImage, Image faceDownImage) {
        super(faceDownImage); // Start face down by default
        this.faceUpImage = faceUpImage;
        this.faceDownImage = faceDownImage;
    }

    public void setImage(boolean isFaceUp) {
        this.setImage(isFaceUp ? faceUpImage : faceDownImage);
    }
}
