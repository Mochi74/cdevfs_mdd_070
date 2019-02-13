/*
 * Ce fichier illustre l'ouvrage "Apprendre les Design Patterns en programmant un jeu vidéo"
 * Philippe-Henri Gosselin, Edition ENI
 */

package pacman.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Gestion du clavier avec AWT
 *
 * @author Philippe-Henri Gosselin
 */
public class Window extends Frame {

    private int canvasWidth = 800;
    private int canvasHeight = 600;
    private Canvas canvas;
    private boolean running = true;

    private BufferedImage texture;
    private int tileWidth = 24;
    private int tileHeight = 24;
    private int textureWidth;
    private int textureHeight;

    static final int levelWidth = 9;
    static final int levelHeight = 6;
    static final int[][] level = new int[][] {
            { 15,11,11,11,11,11,11,11,16 },
            { 12,5,3,3,3,3,3,3,12 },
            { 12,3,15,11,11,11,16,3,12 },
            { 14,3,13,11,11,11,14,3,13 },
            { 3,3,3,3,3,3,3,3,3 },
            { 11,11,11,11,11,11,11,11,11 }
    };

    private static class Keyboard implements KeyListener {

        private boolean[] keys;

        public Keyboard() {
            keys = new boolean[0x10000];
        }

        public boolean isKeyPressed(int keyCode) {
            if (keyCode >= keys.length)
                return false;
            return keys[keyCode];
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() < keys.length) {
                keys[e.getKeyCode()] = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() < keys.length) {
                keys[e.getKeyCode()] = false;
            }
        }

    }

    private static class Mouse implements MouseMotionListener {

        public Integer getMouseX() {
            return mouseX;
        }

        public void setMouseX(Integer mouseX) {
            this.mouseX = mouseX;
        }

        public Integer getMouseY() {
            return mouseY;
        }

        public void setMouseY(Integer mouseY) {
            this.mouseY = mouseY;
        }

        private Integer mouseX;
        private Integer mouseY;

        public Mouse() {

        }


        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX=e.getX();
            mouseY=e.getY();
        }
    }

    private Keyboard keyboard;
    private Mouse mouse;
    private int pacmanX;
    private int pacmanY;

    public void init() {
        setTitle("Affichage et contrôles avec AWT");
        setSize(200,200);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                running = false;
            }
        });
    }

    public void createCanvas() {
        canvasWidth = levelWidth * tileWidth;
        canvasHeight = levelHeight * tileHeight;

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(canvasWidth,canvasHeight));
        canvas.setMinimumSize(new Dimension(canvasWidth,canvasHeight));
        canvas.setMaximumSize(new Dimension(canvasWidth,canvasHeight));
        add(canvas);

        keyboard = new Keyboard();
        mouse= new Mouse();
        canvas.addKeyListener(keyboard);
        canvas.addMouseMotionListener(mouse);

        pack();
    }

    public void loadTexture() throws IOException {
        texture = ImageIO.read(this.getClass().getClassLoader().getResource("grid_tiles.png"));
        textureWidth = texture.getWidth() / tileWidth;
        textureHeight = texture.getHeight() / tileHeight;
    }

    public void handleInputs() {
        canvas.requestFocus();

        if (keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
            pacmanX ++;
        }
        if (keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
            pacmanX --;
        }
        if (keyboard.isKeyPressed(KeyEvent.VK_DOWN)) {
            pacmanY ++;
        }
        if (keyboard.isKeyPressed(KeyEvent.VK_UP)) {
            pacmanY --;
        }
        System.out.println("mouse:"+ mouse.getMouseX()+":"+mouse.getMouseY());
    }

    private long lastUpdate;
    public void update() {
        long now = System.nanoTime();
        if ( (now - lastUpdate) < 1000000000/4)
            return;
        lastUpdate = now;
        for (int j=0;j<levelHeight;j++) {
            for (int i=0;i<levelWidth;i++) {
                int id = level[j][i];
                if (id == 5) level[j][i] = 4;
                else if (id == 4) level[j][i] = 5;
            }
        }
    }

    public void render() {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(2);
            return;
        }
        Graphics g = null;
        try {
            g = bs.getDrawGraphics();

            g.setColor(Color.black);
            g.fillRect(0,0,canvasWidth,canvasHeight);

            /* draw level */
            for (int j=0;j<levelHeight;j++) {
                for (int i=0;i<levelWidth;i++) {
                    int tileIndex = level[j][i];
                    if (tileIndex < 0)
                        tileIndex = 0;
                    int tileX = (tileIndex-1) % textureWidth;
                    int tileY = (tileIndex-1) / textureHeight;
                    if (tileY >= textureHeight) {
                        tileX = 0;
                        tileY = 0;
                    }
                    g.drawImage(texture,
                            i * tileWidth, j * tileHeight, i * tileWidth + tileWidth, j * tileHeight + tileHeight,
                            tileX * tileWidth, tileY * tileHeight, tileX * tileWidth + tileWidth, tileY * tileHeight + tileHeight,
                            null
                    );
                }
            }

            /* draw Pacman */
            int tileX = 0;
            int tileY = 2;
            g.drawImage(texture,
                    pacmanX, pacmanY, pacmanX + tileWidth, pacmanY + tileHeight,
                    tileX * tileWidth, tileY * tileHeight, tileX * tileWidth + tileWidth, tileY * tileHeight + tileHeight,
                    null
            );

            /* draw Yellow square on mouse position */
            tileX = 5;
            tileY = 0;
            if (mouse.getMouseX()!=null && mouse.getMouseX()!=null) {
                int squareX = mouse.getMouseX();
                int squareY = mouse.getMouseY();
                g.drawImage(texture,
                        squareX, squareY, squareX + tileWidth, squareY + tileHeight,
                        tileX * tileWidth, tileY * tileHeight, tileX * tileWidth + tileWidth, tileY * tileHeight + tileHeight,
                        null
                );
            }

            bs.show();
        }
        finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    public void run()
    {
        int fps = 60;
        long nanoPerFrame = (long) (1000000000.0 / fps);
        long lastTime = 0;

        while (running) {
            long nowTime = System.nanoTime();
            if ((nowTime-lastTime) < nanoPerFrame) {
                continue;
            }
            lastTime = nowTime;

            handleInputs();
            update();
            render();

            long elapsed = System.nanoTime() - lastTime;
            long miliSleep = (nanoPerFrame - elapsed) / 1000000;
            if (miliSleep > 0) {
                try {
                    Thread.sleep (miliSleep);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        dispose();
    }

    public static void main(String args[]) throws IOException {
        Window window = new Window();
        window.init();
        window.loadTexture();
        window.createCanvas();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.run();
    }
}
