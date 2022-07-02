package client.graphics;

import client.Client;

public class GUITest {
    public static void main(String[] args) {
        GUIHandler handler = new GUIHandler(new Client());
        handler.drawGameState("STATE/2/1/2/1/0/1/Bami,Feri/37/1");
    }

}
