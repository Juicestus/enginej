package com.justusl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Texture {
    private int[] pixels;
    private String path;
    private final int size;

    public Texture(String path, int size) {
        this.path = path;
        this.size = size;
        pixels = new int[size * size];
        load();
    }

    private void load() {
        try {
            BufferedImage image = ImageIO.read(this.getClass().getResource(path));
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), 
                    pixels, 0, image.getWidth());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getPixels() { return pixels; }
    public int getSize() { return size; }


    public static Texture brick = new Texture("/Bricks.png", 64);
    public static Texture wood = new Texture("/Planks.png", 64);
    public static Texture smoothstone = new Texture("/SmoothStone.png", 64);
    public static Texture stone = new Texture("/Stone.png", 64);
}
