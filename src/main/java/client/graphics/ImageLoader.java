package client.graphics;

import javax.swing.*;
import java.awt.*;

public class ImageLoader {


    private static ImageLoader loader;
    private final String path = "src/main/resources/";
    private static ImageIcon background, heart, ninja;

    private ImageLoader(){
        background = new ImageIcon(path + "Picture3.jpg");

        heart = new ImageIcon(path + "Picture1.png");
        Image heartImage = heart.getImage();
        heartImage = heartImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        heart = new ImageIcon(heartImage);

        ninja = new ImageIcon(path + "ninja.jpg");
        Image ninjaImage = ninja.getImage();
        ninjaImage = ninjaImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ninja = new ImageIcon(ninjaImage);
    }

    public static ImageLoader getInstance(){
        if (loader == null)
            loader = new ImageLoader();
        return loader;
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
