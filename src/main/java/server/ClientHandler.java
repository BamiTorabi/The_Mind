package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    private Socket socket;
    private String authToken;
    private Scanner input;
    private PrintWriter output;
    private String name;

    private Server server;

    public ClientHandler (Socket socket){
        this.socket = socket;
        server = Server.getInstance();
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

    public void askName() throws IOException {
        sendMessage("NAME");
        String name = getInput().split("/")[2];
        setName(name);
    }

    public void askNumberOfPlayers() throws IOException {
        sendMessage("HOST");
        String number = getInput().split("/")[2];
        while (true){
            try{
                int n = Integer.parseInt(number);
                if (2 <= n && n <= server.getMaxPlayersPerLobby()){
                    server.getGame().setPlayerCount(n);
                    server.setHostToken(this.authToken);
                    break;
                }
                else{
                    sendMessage("ERROR/HOST");
                }
            } catch (NumberFormatException e){
                sendMessage("ERROR/HOST");
            }
            number = getInput().split("/")[2];
        }
    }

    @Override
    public void run() {
        try{
            sendMessage("AUTH_TOKEN/" + authToken);
            sendMessage("HANDLE_USER");
            while (!socket.isClosed()){
                String[] S = getInput().split("/");
                //System.out.println(Arrays.asList(S));
                switch (S[1]){
                    case "MESSAGE":
                        if (S.length < 3)
                            continue;
                        if (S[2].equalsIgnoreCase("start")){
                            if (server.getHostToken().equals(S[0])){
                                server.startGame();
                            }
                            else{
                                sendMessage("ERROR/NOT_HOST");
                            }
                        }
                        break;
                    case "PLAY_CARD":
                        try{
                            int n = Integer.parseInt(S[2]);
                            server.playCard(n);
                        } catch (NumberFormatException e){}
                        break;
                    case "NINJA_Q":
                        server.askNinja();
                        break;
                    case "NINJA_A":
                        server.processNinjaAnswer(S[0], S[2].equals("true"));
                }
            }
        } catch (IOException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }

    public void kill() throws IOException {
        socket.close();
        Server.getInstance().removeHandler(authToken);
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
