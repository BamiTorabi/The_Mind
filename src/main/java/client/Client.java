package client;

import server.Server;

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

    public void init() throws IOException {
        this.socket = new Socket("localhost", 8080);
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream());
        Scanner sc = new Scanner(System.in);
        String response = "";
        while (true){
            String[] S = getInput().split("/");
            switch (S[0]){
                case "ERROR":
                    switch (S[1]){
                        case "FULL":
                            System.out.println("Sorry, the server is full!");
                            kill();
                            break;
                        case "HOST":
                            System.out.println("Invalid input. Please try again.");
                            response = sc.nextLine();
                            sendMessage("HOST/" + response);
                            break;

                    }

                    break;
                case "STATE":
                    System.out.println(S);
                    break;
                case "NAME":
                    System.out.print("Enter your name: ");
                    response = sc.nextLine();
                    sendMessage("NAME/" + response);
                    break;
                case "ID":
                    try {
                        this.id = Integer.parseInt(S[1]);
                    } catch (NumberFormatException e) {}
                    break;
                case "HOST":
                    System.out.println("You are the host!\nHow many players in the server?\n(Enter a number between 2 and 4)");
                    response = sc.nextLine();
                    sendMessage("PLAYER_CNT/" + response);
                    break;
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        output = new PrintWriter(socket.getOutputStream());
        output.println(message);
        output.flush();
    }

    public String getInput() throws IOException {
        input = new Scanner(socket.getInputStream());
        return input.nextLine();
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
        Server.getInstance().removeHandler(authToken);
    }
}
