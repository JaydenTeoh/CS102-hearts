package com.example.players;
import com.example.gameplay.*;
import java.util.*;

import com.example.exceptions.*;
import com.example.app.*;
import com.example.pokercards.*;

public interface Player {
    public String getName();

    public Hand getHand();

    public void setHand(Hand hand);

    //public abstract Card playCard(Round round, Trick trick);

    //public abstract List<Card> passCards();
}
