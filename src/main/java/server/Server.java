package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static server.ServerStatus.*;

public class Server {

    private static final String[] validEmojis = {":)", ":D", ":(", ":|", ":/", ":P", ":O"};
    private static Server server = null;
    private Game game;
    private List<ClientHandler> handlers;
    private ServerStatus status;
    final private int port = 8080;
    final private int playersPerLobby = 4;
    private String hostToken = "";

    private Server (){
        handlers = new ArrayList<>();
        game = Game.getInstance();
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
        if (hostToken.equals("")){
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
        if (handlers.size() >= game.getPlayerCount()){
            status = FULL;
        }
        else{
            status = OPEN;
        }
    }

    public void removeHandler(String token) throws IOException {
        for (ClientHandler handler : handlers)
            if (handler.getAuthToken().equals(token)) {
                handlers.remove(handler);
                checkStatus();
                if (token.equals(hostToken)) {
                    game.setPlayerCount(0);
                    hostToken = "";
                    if (handlers.isEmpty())
                        return;
                    System.err.println(handlers.get(0).getName());
                    handlers.get(0).askNumberOfPlayers();

                }
                return;
            }
    }


    public int getPlayersPerLobby() {
        return playersPerLobby;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }

    public String getHostToken() {
        return hostToken;
    }

    public Game getGame() {
        return game;
    }
}
