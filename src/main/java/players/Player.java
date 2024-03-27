package players;
import gameplay.*;
import java.util.*;

import exceptions.*;
import app.*;
import pokercards.*;

public interface Player {
    public String getName();

    public Hand getHand();

    public void setHand(Hand hand);

    //public abstract Card playCard(Round round, Trick trick);

    //public abstract List<Card> passCards();
}
