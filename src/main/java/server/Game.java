package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {

    private static Game game = null;
    private int playerCount = 0;
    final private int cardCount = 100;
    private Random rand;
    private ArrayList<Integer> cards;
    private int maxRounds;
    private int round;
    private int hearts;
    private int ninjas;
    private boolean started;
    private ArrayList<ArrayList<Integer>> playerHands;

    private Game(){
        rand = new Random();
        cards = new ArrayList<>();
        for (int i = 0; i < cardCount; i++){
            cards.add(i + 1);
        }
        this.started = true;
    }

    public static Game getInstance(){
        if (game == null)
            game = new Game();
        return game;
    }

    public void init(){
        this.maxRounds = 16 - 2 * playerCount;
        playerHands = new ArrayList<>();
        for (int i = 0; i < playerCount; i++)
            playerHands.add(new ArrayList<>());
        this.hearts = playerCount;
        this.ninjas = 1;
        this.round = 1;
    }

    public void prepareRound(){
        Collections.shuffle(cards, rand);
        for (int i = 0; i < round; i++){
            for (int j = round * i; j < round * (i + 1); i++)
                (playerHands.get(i)).add(cards.get(j));
        }
    }

    public void nextRound(){
        round++;
        if (round % 3 == 0 && round < 10)
            this.hearts++;
        if (round % 3 == 2 && round < 10)
            this.ninjas++;
        prepareRound();
    }

    public void playCard(int card){
        int mn = cardCount;
        for (int i = 0; i < playerCount; i++) {
            mn = Math.min(mn, Collections.min(playerHands.get(i)));
            playerHands.get(i).remove(Integer.valueOf(card));
        }
        if (card != mn){
            this.hearts--;
            for (int i = 0; i < playerCount; i++){
                playerHands.set(i,
                        (ArrayList<Integer>) playerHands.get(i)
                        .stream()
                        .filter(x -> (x < card))
                        .toList()
                );
            }
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
