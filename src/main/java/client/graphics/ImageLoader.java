package client.graphics;

import javax.swing.*;
import java.awt.*;

public class ImageLoader {

    private static ImageIcon background, heart, ninja;

    public ImageLoader(){
        background = new ImageIcon("Picture3.jpg");

        heart = new ImageIcon("heart.jpg");
        Image heartImage = heart.getImage();
        heartImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        heart = new ImageIcon(heartImage);

        ninja = new ImageIcon("ninja.jpg");
        Image ninjaImage = ninja.getImage();
        ninjaImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ninja = new ImageIcon(ninjaImage);
    }

    public ImageIcon getBackground() {
        return background;
    }

    public ImageIcon getHeart() {
        return heart;
    }

    public ImageIcon getNinja() {
        return ninja;
    }
}
