package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static server.ServerStatus.*;

public class Server {

    private static final String[] validEmojis = {":)", ":D", ":(", ":|", ":/", ":P", ":O"};
    private static Server server = null;
    private List<ClientHandler> handlers;
    private ServerStatus status;
    final private int port = 8080;
    final private int playersPerLobby = 4;
    private int playerCount = 0;
    private String host = "";

    private Server (){
        handlers = new ArrayList<>();
        status = OPEN;
    }

    public static Server getInstance(){
        if (server == null)
            server = new Server();
        return server;
    }

    public void init(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                System.err.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                addNewClientHandler(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewClientHandler(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(socket);
        if (status != OPEN){
            handler.sendMessage("ERROR/FULL");
            handler.kill();
            return;
        }
        handler.authenticate();
        System.err.println("Authenticated client with token " + handler.getAuthToken());
        handler.askName();
        handlers.add(handler);
        if (host.equals("")){
            handler.askNumberOfPlayers();
        }
        new Thread(handler).start();
        checkStatus();
    }

    public void sendToAll(String token, String message){
        String name = "";
        for (ClientHandler handler : handlers)
            if (handler.getAuthToken().equals(token))
                name = handler.getName();
        for (ClientHandler handler : handlers){
            try {
                handler.sendMessage("> " + name + ": " + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void checkStatus(){
        if (handlers.size() >= playerCount){
            status = FULL;
        }
        else{
            status = OPEN;
        }
    }

    public void removeHandler(String token){
        for (ClientHandler handler : handlers)
            if (handler.getAuthToken().equals(token)) {
                handlers.remove(handler);
                checkStatus();
                if (handlers.isEmpty()) {
                    playerCount = 0;
                    host = "";
                }
                return;
            }
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getPort() {
        return port;
    }

    public int getPlayersPerLobby() {
        return playersPerLobby;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
