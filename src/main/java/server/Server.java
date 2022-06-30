package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static server.ServerStatus.*;

public class Server {

    private static Server server = null;
    private List<ClientHandler> handlers;
    private ServerStatus status;
    final private int port = 8080;
    final private static int MAX_PLAYERS_PER_LOBBY = 4;
    private int playerCount = 0;

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
            handler.sendMessage("Sorry, the server is full!");
            handler.kill();
            return;
        }
        handler.sendMessage("Enter your name: ");
        String name = handler.getInput();
        handler.authenticate(name);
        System.err.println("Authenticated client with token " + handler.getAuthToken());
        handlers.add(handler);
        if (playerCount == 0){
            handler.sendMessage("You are the host! How many players in this game?\n(Enter a number between 2 to " + MAX_PLAYERS_PER_LOBBY + ")");
            String number = handler.getInput();
            while (true){
                try{
                    int n = Integer.parseInt(number);
                    if (2 <= n && n <= MAX_PLAYERS_PER_LOBBY){
                        handler.sendMessage("Lobby with " + n + " players created.");
                        playerCount = n;
                        break;
                    }
                    else{
                        handler.sendMessage("Invalid input. Please try again.");
                    }
                } catch (NumberFormatException e){
                    handler.sendMessage("Invalid input. Please try again.");
                }
                number = handler.getInput();
            }
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
                if (handlers.isEmpty())
                    playerCount = 0;
                return;
            }
    }

    public int getPort() {
        return port;
    }
}
