package client.graphics;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GUIHandler extends JFrame implements Runnable{

    final private int WIDTH = 1280;
    final private int HEIGHT = 1024;
    final private int MARGIN_SIZE = 30;
    final private int ICON_SIZE = 100;
    final private int PLAYER_WIDTH = 170;
    final private int PLAYER_HEIGHT = 100;
    final private int CARD_SIZE = 64;

    private JLabel backgroundLabel, heartLabel, ninjaLabel;
    private JButton heartButton, ninjaButton;
    private JLayeredPane pane;
    private ImageLoader loader;

    private int playerCount, round, hearts, ninjas, lastCard, playerID;
    private ArrayList<Integer> playersCount, playerHand;
    private ArrayList<String> playerNames;
    private Client client;

    public GUIHandler(Client client){
        super();
        this.client = client;
        this.setTitle("The Mind!");
        this.loader = ImageLoader.getInstance();
        this.setSize(WIDTH, HEIGHT);
        this.pane = new JLayeredPane();
        this.pane.setSize(WIDTH, HEIGHT);
        this.setLayout(null);
        this.setVisible(true);
    }

    public void unloadData(String state){
        String[] S = state.split("/");
        this.playersCount = new ArrayList<>();
        this.playerHand = new ArrayList<>();
        this.playerNames = new ArrayList<>();
        try{
            this.playerCount = Integer.parseInt(S[1]);
            this.round = Integer.parseInt(S[2]);
            this.hearts = Integer.parseInt(S[3]);
            this.ninjas = Integer.parseInt(S[4]);
            this.lastCard = Integer.parseInt(S[5]);
            this.playerID = Integer.parseInt(S[6]);
            String[] names = S[7].split(",");
            this.playerNames.addAll(Arrays.asList(names));
            for (int i = 1; i <= this.playerCount; i++){
                int n = Integer.parseInt(S[7 + i]);
                this.playersCount.add(n);
            }
            if (this.playersCount.get(this.playerID - 1) != 0) {
                String[] T = S[8 + this.playerCount].split(",");
                for (int i = 0; i < this.round; i++) {
                    int n = Integer.parseInt(T[i]);
                    this.playerHand.add(n);
                }
                Collections.sort(this.playerHand);
            }
        } catch (NumberFormatException e){

        }
    }

    public void drawGameState(String state){
        if (!this.isVisible())
            return;
        unloadData(state);

        this.pane.removeAll();
        this.getContentPane().removeAll();

        addBackground();
        addHearts();
        addNinjas();
        addPlayers();
        addHand();

        this.add(this.pane);
        this.pane.revalidate();
        this.pane.repaint();

        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    public void addBackground(){
        this.backgroundLabel = new JLabel();
        this.backgroundLabel.setBounds(0, 0, WIDTH, HEIGHT);
        this.backgroundLabel.setIcon(this.loader.getBackground());
        this.pane.add(this.backgroundLabel, 1);
    }

    public void addHearts(){
        this.heartButton = new JButton();
        this.heartButton.setBounds(MARGIN_SIZE, HEIGHT / 2 - MARGIN_SIZE - ICON_SIZE, ICON_SIZE, ICON_SIZE);
        this.heartButton.setIcon(this.loader.getHeart());
        this.heartButton.setEnabled(false);
        this.add(this.heartButton);

        this.heartLabel = new JLabel();
        this.heartLabel.setBounds(2 * MARGIN_SIZE + ICON_SIZE, HEIGHT / 2 - MARGIN_SIZE - ICON_SIZE, ICON_SIZE, ICON_SIZE);
        this.heartLabel.setForeground(Color.GREEN);
        this.heartLabel.setText(String.valueOf(this.hearts));
        this.add(this.heartLabel);
    }

    public void addNinjas(){
        this.ninjaButton = new JButton();
        this.ninjaButton.setBounds(MARGIN_SIZE, HEIGHT / 2 + MARGIN_SIZE, ICON_SIZE, ICON_SIZE);
        this.ninjaButton.setIcon(this.loader.getNinja());
        this.ninjaButton.setEnabled(this.ninjas > 0);
        this.add(this.ninjaButton);

        this.ninjaLabel = new JLabel();
        this.ninjaLabel.setBounds(2 * MARGIN_SIZE + ICON_SIZE, HEIGHT / 2 + MARGIN_SIZE, ICON_SIZE, ICON_SIZE);
        this.ninjaLabel.setForeground(Color.GREEN);
        this.ninjaLabel.setText(String.valueOf(this.ninjas));
        this.add(this.ninjaLabel);
    }

    public void addPlayers(){
        for (int i = 0; i < this.playerCount; i++){
            JLabel label = new JLabel();
            label.setOpaque(false);
            label.setForeground((i + 1 == this.playerID ? Color.RED : Color.WHITE));
            label.setText(this.playerNames.get(i) + ": " + this.playersCount.get(i));
            label.setBounds(MARGIN_SIZE + ICON_SIZE + i * PLAYER_WIDTH, MARGIN_SIZE, PLAYER_WIDTH, PLAYER_HEIGHT);
            this.add(label);
        }
    }

    public void addHand(){
        for (int i = 0; i < this.playerHand.size(); i++){
            JButton button = new JButton();
            button.setBackground(Color.PINK);
            button.setForeground(Color.RED);
            button.setText(String.valueOf(this.playerHand.get(i)));
            button.setBounds(MARGIN_SIZE + ICON_SIZE + i * CARD_SIZE, HEIGHT - MARGIN_SIZE - CARD_SIZE, CARD_SIZE, CARD_SIZE);
            if (i == 0){
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.sendMessage("PLAY_CARD/" + playerHand.get(0));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }
            this.add(button);
        }
        JButton button = new JButton();
        button.setBackground(Color.PINK);
        button.setForeground(Color.RED);
        button.setText(String.valueOf(this.lastCard));
        button.setBounds((WIDTH - CARD_SIZE) / 2, (HEIGHT - CARD_SIZE) / 2, CARD_SIZE, CARD_SIZE);
        button.setEnabled(false);
        this.add(button);
    }

    @Override
    public void run() {

    }
}
