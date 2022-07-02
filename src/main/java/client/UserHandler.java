package client;

import java.util.Scanner;

public class UserHandler implements Runnable{

    private Scanner scanner;
    private Client client;
    private Boolean dead = false;

    public UserHandler(Client cl){
        this.scanner = new Scanner(System.in);
        this.client = cl;
    }

    public void tell(String message){
        if (dead)
            return;
        System.out.print(message);
    }

    public String ask(String question){
        if (dead)
            return "";
        tell(question);
        return scanner.nextLine();
    }

    @Override
    synchronized public void run() {
        while (!dead) {
            String S = ask("");
            client.reactToUser(S);
        }
    }

    synchronized public void kill(){
        dead = true;
    }
}
