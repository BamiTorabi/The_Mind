package client;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    private Socket socket;
    private String name;
    private int id;
    private String authToken;
    private Scanner input;
    private PrintWriter output;
    private UserHandler user;

    public void init() throws IOException {
        this.socket = new Socket("localhost", 8080);
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream());
        this.user = new UserHandler(this);
        String response = "";
        while (true){
            String[] S = getMessage().split("/");
            switch (S[0]){
                case "ERROR":
                    switch (S[1]){
                        case "FULL":
                            user.tell("Sorry, the server is full!");
                            kill();
                            break;
                        case "HOST":
                            response = user.ask("Invalid input. Please try again.\n");
                            sendMessage("PLAYER_CNT/" + response);
                            break;
                        case "NOT_HOST":
                            user.tell("Sorry, you can't start the game; only the host can do that.");
                            break;
                    }
                    break;
                case "STATE":
                    System.out.println(S);
                    break;
                case "NAME":
                    response = user.ask("Enter your name: ");
                    sendMessage("NAME/" + response);
                    break;
                case "HOST":
                    response = user.ask("You are the host!\n" +
                            "How many players in the server?\n" +
                            "(Enter a number between 2 and 4)\n");
                    sendMessage("PLAYER_CNT/" + response);
                    break;
                case "HANDLE_USER":
                    new Thread(user).start();
                    break;
            }
        }
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
        } catch (IOException e){

        }
    }

    public void kill() throws IOException {
        socket.close();
    }
}
