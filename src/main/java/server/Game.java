package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {

    private static Game game = null;
    final private int cardCount = 100;
    private Random rand;
    private ArrayList<Integer> cards;
    private ArrayList<String> names;
    private int playerCount = 0, maxRounds, round, hearts, ninjas, lastCard;
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
        System.err.println(playerCount);
        this.maxRounds = 16 - 2 * playerCount;
        this.playerHands = new ArrayList<>();
        this.names = Server.getInstance().getNames();
        for (int i = 0; i < playerCount; i++)
            this.playerHands.add(new ArrayList<>());
        this.hearts = playerCount;
        this.ninjas = 1;
        this.round = 1;
        this.lastCard = 0;
        this.prepareRound();
    }

    public void prepareRound(){
        Collections.shuffle(cards, rand);
        for (int i = 0; i < playerCount; i++){
            for (int j = round * i; j < round * (i + 1); j++)
                (playerHands.get(i)).add(cards.get(j));
        }
        this.lastCard = 0;
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
        this.lastCard = card;
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

    public String getState(int player){
        String S = playerCount + "/" +
                round + "/" +
                hearts + "/" +
                ninjas + "/" +
                lastCard + "/" +
                player + "/";
        for (int i = 0; i < playerCount; i++){
            S += this.names.get(i);
            if (i != playerCount - 1)
                S += ",";
        }
        S += "/";
        for (int i = 0; i < playerCount; i++){
            if (i == player - 1){
                for (int j = 0; j < playerHands.get(i).size(); j++){
                    S += playerHands.get(i).get(j);
                    if (j != playerHands.get(i).size() - 1)
                        S += ",";
                }
            }
            else{
                S += playerHands.get(i).size();
            }
            if (i != playerCount - 1)
                S += "/";
        }
        return S;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
