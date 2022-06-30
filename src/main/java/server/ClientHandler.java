package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private static String[] validEmojis = {":)", ":D", ":(", ":|", ":/", ":P", ":O"};
    private Socket socket;
    private String authToken;
    private Scanner input;
    private PrintWriter output;
    private String name;
    private SecureRandom random;

    ClientHandler (Socket socket){
        this.socket = socket;
        random = new SecureRandom();
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

    public void authenticate(String name){
        this.name = name;
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        this.authToken = Arrays.toString(bytes);
    }

    @Override
    public void run() {
        try{
            while (!socket.isClosed()){
                String S = getInput();
                Server server = Server.getInstance();
                if (Arrays.asList(validEmojis).contains(S)){
                    server.sendToAll(authToken, S);
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

    public String getAuthToken() {
        return authToken;
    }

    public String getName() {
        return name;
    }
}
