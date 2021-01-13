import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Control;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import java.lang.*;
import static java.lang.System.out;

public class Game extends JPanel {
    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;
        // Function to create the window and display it
    public void setupWindow() {
        JFrame frame = new JFrame();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocation(200,200);
        frame.setTitle("Collect all the balls");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.addKeyListener(new KeyListener(){
            @Override
            public void keyReleased(KeyEvent e){
                // System.out.println("Key released");
            }
            @Override
            public void keyTyped(KeyEvent e){
                // System.out.println("Key typed");
            }
            @Override
            public void keyPressed(KeyEvent e) {
                MovementSystem.Direction direction;
                //Move main piece up
                if ((e.getKeyCode() == KeyEvent.VK_UP)|| (e.getKeyCode() == KeyEvent.VK_W) ){
                    direction = MovementSystem.Direction.up;
                    mMovementSystem.update(direction, mEntities);
                }
                //Move main piece left
                if ((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) {
                    direction = MovementSystem.Direction.left;
                    mMovementSystem.update(direction, mEntities);
                }
                //Move main piece down
                if ((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)){
                    direction = MovementSystem.Direction.down;
                    mMovementSystem.update(direction, mEntities);
                }
                //Move main piece right
                if ((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)){
                    direction = MovementSystem.Direction.right;
                    mMovementSystem.update(direction, mEntities);
                }
                
        }});
        setDoubleBuffered(true);

        // Resize the window (insets are just the boards that the Operating System puts on the board)
        Insets insets = frame.getInsets();
        frame.setSize(WINDOW_WIDTH + insets.left + insets.right,
            WINDOW_HEIGHT + insets.top + insets.bottom);
    }

    // Main function that takes care of some Object Oriented stuff
    public static void main(String args[]) {
        Game game = new Game();
    }

    Vector<Entity> mEntities = new Vector<Entity>();
    RenderSystem mRenderSystem;
    MovementSystem mMovementSystem;
    int id = 0;

    public Game() {
        mMovementSystem = new MovementSystem();
        mRenderSystem = new RenderSystem();
        setupWindow();
        // Create a new vector of entities
        mEntities = new Vector<Entity>();

        Entity mainCircleEntity = new Entity();
        mainCircleEntity.addComponent(new CircleComponent(WINDOW_WIDTH/2, WINDOW_HEIGHT/2, 50));
        mainCircleEntity.addComponent(new MovementComponent(0, 0));
        mainCircleEntity.addComponent(new ControllableComponent());
        mEntities.add(mainCircleEntity);

        Entity secondCircleEntity = new Entity();
        secondCircleEntity.addComponent(new CircleComponent(200, 200, 25));
        secondCircleEntity.addComponent(new MovementComponent(0, 0));
        mEntities.add(secondCircleEntity);
    
        // Last Update
        double last = java.lang.System.nanoTime();

        while(true) {
            double now = java.lang.System.nanoTime();
            
            try {
                Thread.sleep((long)((1000.0/60.0) - (now - last)/(1000000.0)));
            } catch(InterruptedException ex) {

            }
            
            now = java.lang.System.nanoTime();

            double dt = (now - last) / 1000000000.0;
            // System.out.println(dt);
            last = now;

            // mPhysicsSystem.update(mEntities, dt);

            repaint();
            // Toolkit.getDefaultToolkit().sync();
        }
    }

    
    // This gets called any time the Operating System
    // tells the program to paint itself
    public void paint(Graphics g) {
        Toolkit.getDefaultToolkit().sync();
        mRenderSystem.render((Graphics2D)g, mEntities);
    }
    // @Override
    // public void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     mRenderSystem.render((Graphics2D)g, mEntities);
    // }

}