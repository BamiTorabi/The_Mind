package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.*;
import java.util.stream.Stream;

import static server.ServerStatus.*;

public class Server {

    private static final String[] validEmojis = {":)", ":D", ":(", ":|", ":/", ":P", ":O"};
    private static Server server = null;
    private Game game;
    private List<ClientHandler> handlers;
    private ServerStatus status;
    final private int port = 9000;
    final private int maxPlayersPerLobby = 4;
    final private int threshold = 100;
    private String hostToken = "";
    private SecureRandom random;
    private Clock clock;
    private TreeMap<String, Integer> map;
    private int[] ninjaAnswers;
    private int answeredCount;

    private Server (){
        handlers = new ArrayList<>();
        game = Game.getInstance();
        random = new SecureRandom();
        clock = Clock.systemDefaultZone();
        map = new TreeMap<>();
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
            while (!serverSocket.isClosed()){
                System.err.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                addNewClientHandler(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void authenticate(ClientHandler handler) throws IOException {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        handler.setAuthToken(bytes.toString());
    }

    public void addNewClientHandler(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(socket);
        if (status != OPEN){
            handler.sendMessage("ERROR/FULL");
            handler.kill();
            return;
        }
        authenticate(handler);
        System.err.println("Authenticated client with token " + handler.getAuthToken());
        handler.askName();
        handlers.add(handler);
        if (hostToken.equals("")){
            handler.askNumberOfPlayers();
        }
        new Thread(handler).start();
        updateStatus();
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

    public void updateStatus(){
        if (handlers.size() >= game.getPlayerCount()){
            status = FULL;
        }
        else{
            status = OPEN;
        }
    }

    public void startGame() {
        int pt = 1;
        for (ClientHandler handler : handlers){
            map.put(handler.getAuthToken(), pt++);
        }
        game.init();
        try {
            for (ClientHandler handler : handlers) {
                handler.sendMessage("START_GAME");
            }
        } catch (IOException ignored){
            System.err.println("fuck");
        }
        try{
            playCard(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playCard(int n) throws IOException {
        if (n != 0)
            game.playCard(n);
        for (ClientHandler handler : handlers){
            String message = getState(handler.getAuthToken());
            System.err.println(message);
            handler.sendMessage(message);
        }
        switch(game.getStatus()){
            case PENDING:
                break;
            case WON_ROUND:
                game.nextRound();
                for (ClientHandler handler : handlers) {
                    String message = getState(handler.getAuthToken());
                    handler.sendMessage(message);
                }
                break;
            case FINISHED:
            case LOST:
                for (ClientHandler handler : handlers) {
                    handler.sendMessage("END_GAME");
                }
                break;
        }
    }

    public void askNinja() throws IOException {
        ninjaAnswers = new int[game.getPlayerCount()];
        for (int i = 0; i < game.getPlayerCount(); i++)
            ninjaAnswers[i] = -1;
        answeredCount = 0;
        for (ClientHandler handler : handlers){
            handler.sendMessage("NINJA_A");
        }
    }

    public void processNinjaAnswer(String token, boolean answer) throws IOException {
        int id = map.get(token) - 1;
        ninjaAnswers[id] = (answer ? 1 : 0);
        answeredCount++;
        if (answeredCount == game.getPlayerCount()){
            if (Arrays.stream(ninjaAnswers).sum() == answeredCount) {
                String ninjaCards = game.playNinja();
                for (ClientHandler handler : handlers) {
                    handler.sendMessage("PLAY_NINJA/" + ninjaCards);
                    String message = getState(handler.getAuthToken());
                    handler.sendMessage(message);
                }
            }
        }
    }

    public String getState(String token){
        String state = game.getState(map.get(token));
        if (state.equals("*")){
            return "STATE/" + game.getStatus().toString();
        }
        return "STATE/" + state;
    }

    public ArrayList<String> getNames(){
        String[] names = new String[game.getPlayerCount()];
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            for (ClientHandler handler : handlers)
                if (handler.getAuthToken().equals(entry.getKey()))
                    names[entry.getValue() - 1] = handler.getName();
        }
        return new ArrayList<>(Arrays.asList(names));
    }

    public void removeHandler(String token) throws IOException {
        for (ClientHandler handler : handlers)
            if (handler.getAuthToken().equals(token)) {
                handlers.remove(handler);
                updateStatus();
                if (token.equals(hostToken)) {
                    game.setPlayerCount(0);
                    hostToken = "";
                    if (handlers.isEmpty())
                        return;
                    handlers.get(0).askNumberOfPlayers();
                }
                return;
            }
    }


    public int getMaxPlayersPerLobby() {
        return maxPlayersPerLobby;
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
