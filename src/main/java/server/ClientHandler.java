package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private String[] validEmojis = {":)", ":D", ":(", ":|", ":/", ":P", ":O"};
    private Socket socket;
    private String authToken;
    private Scanner input;
    private PrintWriter output;

    ClientHandler (Socket socket){
        this.socket = socket;
    }

    public void sendMessage(String message) throws IOException {
        output = new PrintWriter(socket.getOutputStream());
        output.println(message);
        output.flush();
    }

    @Override
    public void run() {
        try{
            input = new Scanner(socket.getInputStream());
            Server server = Server.getInstance();
            while (!socket.isClosed()){
                String S = input.nextLine();
                if (Arrays.asList(validEmojis).contains(S)){

                }
                else{
                    sendMessage("Invalid text.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void kill() throws IOException {
        socket.close();
    }
}
