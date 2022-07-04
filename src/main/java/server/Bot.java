package server;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Bot implements Runnable{

    final private Random rand;
    final private Clock clock;
    final private String name;
    final private Server server;
    private boolean dead;
    private String authToken;
    private long time;
    final private long timeThreshold = 1000;

    private int playerCount, round, hearts, ninjas, lastCard, playerID;
    private GameStatus status;
    private ArrayList<Integer> playersCount, playerHand;

    public Bot(String name){
        this.rand = new Random();
        this.clock = Clock.systemDefaultZone();
        this.name = name;
        this.server = Server.getInstance();
    }

    public void sendState(String state){
        this.time = clock.millis();
        String[] S = state.split("/");
        if (S.length == 2){
            this.status = GameStatus.valueOf(S[1]);
            synchronized (this) {
                this.notifyAll();
            }
            return;
        }
        this.status = GameStatus.PENDING;
        this.playersCount = new ArrayList<>();
        this.playerHand = new ArrayList<>();
        try{
            this.playerCount = Integer.parseInt(S[1]);
            this.round = Integer.parseInt(S[2]);
            this.hearts = Integer.parseInt(S[3]);
            this.ninjas = Integer.parseInt(S[4]);
            this.lastCard = Integer.parseInt(S[5]);
            this.playerID = Integer.parseInt(S[6]);
            for (int i = 1; i <= this.playerCount; i++){
                int n = Integer.parseInt(S[7 + i]);
                this.playersCount.add(n);
            }
            if (this.playersCount.get(this.playerID - 1) != 0) {
                String[] T = S[8 + this.playerCount].split(",");
                for (int i = 0; i < T.length; i++) {
                    int n = Integer.parseInt(T[i]);
                    this.playerHand.add(n);
                }
                Collections.sort(this.playerHand);
            }
        } catch (NumberFormatException e){

        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public String getName() {
        return name;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void run() {
        this.status = GameStatus.WON_ROUND;
        while (!this.dead){
            if (this.status != GameStatus.PENDING) {
                try {
                    synchronized (this){
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (this.playerHand.isEmpty())
                continue;
            long waitTime = (this.playerHand.get(0) - this.lastCard) * timeThreshold;
            if (this.playersCount.stream().reduce(0, Integer::sum) == this.playerHand.size())
                waitTime = timeThreshold;
            if (this.clock.millis() - this.time >= waitTime){
                System.out.println(this.clock.millis() - this.time);
                server.playCardBot(this.playerHand.get(0));
                try {
                    synchronized (this){
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
