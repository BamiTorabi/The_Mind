package client;

import java.util.Scanner;

public class UserHandler implements Runnable{

    private Scanner scanner;
    private Client client;

    public UserHandler(Client cl){
        this.scanner = new Scanner(System.in);
        this.client = cl;
    }

    public void tell(String message){
        System.out.print(message);
    }

    public String ask(String question){
        tell(question);
        String answer = scanner.nextLine();
        return answer;
    }

    @Override
    public void run() {
        while (true){
            String S = ask("");
            client.reactToUser(S);
        }
    }
}
