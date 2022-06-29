package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<ClientHandler> handlers;
    final private int port = 8080;

    public Server (){
        handlers = new ArrayList<>();
        try{
            ServerSocket server = new ServerSocket(port);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }
}
