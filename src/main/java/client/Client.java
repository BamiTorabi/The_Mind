package client;


import client.graphics.GUIHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    private Socket socket;
    private String authToken;
    private Scanner input;
    private PrintWriter output;
    private UserHandler user;
    private GUIHandler guiHandler;
    private Thread userThread;

    public void init() throws IOException {
        this.socket = new Socket("localhost", 9000);
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream());
        this.user = new UserHandler(this);
        this.userThread = new Thread(this.user);
        String response = "";
        while (!socket.isClosed()){
            String message = getMessage();
            String[] S = message.split("/");
            switch (S[0]){
                case "ERROR":
                    switch (S[1]){
                        case "FULL":
                            this.user.tell("Sorry, the server is full!\n");
                            kill();
                            break;
                        case "HOST":
                            response = this.user.ask("Invalid input. Please try again.\n");
                            sendMessage("PLAYER_CNT/" + response);
                            break;
                        case "NOT_HOST":
                            this.user.tell("Sorry, you can't do that; only the host can do that.\n");
                            break;
                    }
                    break;
                case "STATE":
                    this.guiHandler.drawGameState(message);
                    break;
                case "NAME":
                    response = this.user.ask("Enter your name: ");
                    sendMessage("NAME/" + response);
                    break;
                case "HOST":
                    response = this.user.ask("You are the host!\n" +
                            "How many players in the server?\n" +
                            "(Enter a number between 2 and 4)\n");
                    sendMessage("PLAYER_CNT/" + response);
                    break;
                case "HANDLE_USER":
                    this.userThread.start();
                    break;
                case "START_GAME":
                    this.userThread.interrupt();
                    this.guiHandler = new GUIHandler(this);
                    this.userThread = new Thread(this.guiHandler);
                    this.userThread.start();
                    break;
                case "AUTH_TOKEN":
                    this.authToken = S[1];
                    break;
                case "END_GAME":
                    this.userThread.interrupt();
                    kill();
                    break;
                case "NINJA_A":
                    boolean ninja = this.guiHandler.askNinja();
                    this.sendMessage("NINJA_A/" + ninja);
                    break;
                case "PLAY_NINJA":
                    this.guiHandler.showNinjaResult(message);
            }
        }
        user.kill();
    }

    public void sendMessage(String message) throws IOException {
        output = new PrintWriter(socket.getOutputStream());
        output.println(this.authToken + "/" + message);
        output.flush();
    }

    public String getMessage() throws IOException {
        input = new Scanner(socket.getInputStream());
        return input.nextLine();
    }

    public void reactToUser(String message){
        try {
            sendMessage("MESSAGE/" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try{
            init();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void kill() throws IOException {
        socket.close();
    }
}
