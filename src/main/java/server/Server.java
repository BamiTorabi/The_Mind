package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    final private int port = 8080;

    public Server (){
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
