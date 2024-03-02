import java.util.ArrayList;

public class Round {
    private boolean heartsBroken;
    private Player playerStartingFirst;
    private int numTricksPlayed;
    // private Trick currentTrick;

    public Round(Player playerStartingFirst){
        this.playerStartingFirst = playerStartingFirst;
    }

    public Player getPlayerStartingFirst() {
        return playerStartingFirst;
    }

    public int getNumTricksPlayed() {
        return numTricksPlayed;
    }

    public boolean isHeartsBroken() {
        return heartsBroken;
    }

    // public startNewTrick(Player player){
    //     this.currentTrick = new Trick(new ArrayList<Card>(), )
    // }
}
