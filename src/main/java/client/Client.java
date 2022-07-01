package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private String name;
    private int id;
    private String authToken;
    private Scanner input;
    private PrintWriter output;

    public void init() throws IOException {
        this.socket = new Socket("localhost", 8080);

    }
}
