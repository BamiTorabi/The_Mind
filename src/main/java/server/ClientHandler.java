package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Socket socket;
    private String authToken;
    private Scanner input;
    private PrintWriter output;

    ClientHandler (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            input = new Scanner(socket.getInputStream());
            while (!socket.isClosed()){

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
