package game;

import graphics.Screen;

import java.awt.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    public static int width = 300;
    public static int height = width / 16 * 9; // Aspect Ratio
    public static int scale = 3;

    private Thread thread;
    private JFrame frame;
    private boolean running = false;

    private Screen screen;

    // Create image with an accessible buffer
    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    // Convert image object to an accessible array of integers (pixels)
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    public Game() {
        //Constructor
        Dimension size = new Dimension (width * scale, height * scale);
        setPreferredSize(size);

        screen = new Screen(width, height);

        frame = new JFrame();
    }

    public synchronized void start() {
        running = true;
        // Create thread in THIS instance
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while ( running ) {
            update();
            render();
        }
    }

    public void update() {

    }

    public void render(){
        // init buffer strategy object for the canvas
        BufferStrategy bs = getBufferStrategy();
        // If the buffer strategy hasn't been created yet
        if( bs == null ) {
            // create a triple buffer
            createBufferStrategy(3);
            return;
        }

        screen.clear();

        screen.render();

        for ( int i = 0; i < pixels.length; i++ ) {
            pixels[i] = screen.pixels[i];
        }

        // Create context for drawing on the buffer
        Graphics graphics = bs.getDrawGraphics();
        // Draw all graphics between here and dispose()
        graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bs.show();
    }

    public static void main (String[] args) {
        Game game = new Game();
        game.frame.setResizable(false);
        game.frame.setTitle("The Seventh Age");
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);

        game.start();
    }
}
