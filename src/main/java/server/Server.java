package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static Server server = null;
    private List<ClientHandler> handlers;
    final private int port = 8080;

    private Server (){
        handlers = new ArrayList<>();
        try{
            ServerSocket server = new ServerSocket(port);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static Server getInstance(){
        if (server == null)
            server = new Server();
        return server;
    }

    public int getPort() {
        return port;
    }
}
