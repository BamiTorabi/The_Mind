package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static server.ServerStatus.OPEN;

public class Server {

    private static Server server = null;
    private List<ClientHandler> handlers;
    private ServerStatus status;
    final private int port = 8080;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }
}
