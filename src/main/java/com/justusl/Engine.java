package com.justusl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Engine extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    public static final int RESOLUTION_WIDTH = 640;
    public static final int RESOLUTION_HEIGHT = 480;
    
    private Thread thread;
    private boolean running;
    private BufferedImage image;

    private ArrayList<Texture> textures;
    private Camera camera;
    private Screen screen;

    public int[] pixels;

    // Basic sample map
    public static int[][] map = Map.debugMap;

    public Engine() {
        textures = new ArrayList<Texture>();
        textures.add(Texture.wood);
        textures.add(Texture.brick);
        textures.add(Texture.smoothstone);
        textures.add(Texture.stone);

        camera = new Camera(4.5, 4.5, 1, 0, 0, -.66);
        addKeyListener(camera);

        screen = new Screen(map, textures, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);

        thread = new Thread(this);
		image = new BufferedImage(RESOLUTION_WIDTH, RESOLUTION_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
		setResizable(false);
		setTitle("enginej");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.black);
		setLocationRelativeTo(null);
		setVisible(true);
		start();
    }

    public synchronized void start() {
        running = true;
        thread.start();
    } 

    public synchronized void stop() {
        running = false;
        try { thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        BufferStrategy bufferStrat = getBufferStrategy();
        if (bufferStrat == null) {
            createBufferStrategy(3);
            return;
        }
        bufferStrat.getDrawGraphics().drawImage(image, 0, 0, 
                getWidth(), getHeight(), null);
        bufferStrat.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nanosec = 1e9 / 60.;
        double delta = 0;
        requestFocus();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nanosec;
            lastTime = now;
            while (delta >= 1) {
                camera.update(map);
                screen.update(camera, pixels);
                delta--;
            }
            render();
        }
    }
}
     