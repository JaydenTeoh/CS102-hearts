package com.example.players;
import com.example.gameplay.*;

public interface Player {
    public String getName();

    public Hand getHand();

    public void setHand(Hand hand);
}
