import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.awt.event.KeyListener;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
/*
 TODO:
Fix movement 
Slow down big bubbles 
Add artificial bots
*/


public class Game extends JPanel {
    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;

    public static boolean gameIsFinished = false;
    public static String winnerString = "";
    public static int UP = 0;
    public static int DOWN = 1;
    public static int LEFT = 2;
    public static int RIGHT = 3;

    public static int NUM_OF_SMALL_BALLS = 500;

    // public static int WINNING_SCORE = 400;

    // Player One Controlls
    int PlayerOneUp = KeyEvent.VK_UP;
    int PlayerOneDown = KeyEvent.VK_DOWN;
    int PlayerOneLeft = KeyEvent.VK_LEFT;
    int PlayerOneRight = KeyEvent.VK_RIGHT;

    // Player Two Controls
    int PlayerTwoUp = KeyEvent.VK_W;
    int PlayerTwoDown = KeyEvent.VK_S;
    int PlayerTwoLeft = KeyEvent.VK_A;
    int PlayerTwoRight = KeyEvent.VK_D;

    Vector<Entity> mEntities = new Vector<Entity>();
    RenderSystem mRenderSystem;
    MovementSystem mMovementSystem;
    ControlSystem mControlSystem;
    CollisionSystem mCollisionSystem;
    GravitySystem mGravitySystem;
    Boolean[] mArrowKeysInput = new Boolean[]{false,false,false,false};
    Boolean[] mWASDInput = new Boolean[]{false,false,false,false};
    public static double fps;
    
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
                Boolean WASDmovementKeyReleased = false;
                Boolean ArrowsKeysReleased = false;
                if (e.getKeyCode() == PlayerOneUp){
                    mArrowKeysInput[UP] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) { //} || (e.getKeyCode() == KeyEvent.VK_A)) {
                    mArrowKeysInput[LEFT] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {//|| (e.getKeyCode() == KeyEvent.VK_S)){
                    mArrowKeysInput[DOWN] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {//} || (e.getKeyCode() == KeyEvent.VK_D)){
                    mArrowKeysInput[RIGHT] = false;
                    ArrowsKeysReleased = true;
                }
                if (ArrowsKeysReleased) mControlSystem.update(mArrowKeysInput, mEntities, ArrowControllsComponent.class);
                if (e.getKeyCode() == PlayerTwoUp){
                    mWASDInput[UP] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoDown){
                    mWASDInput[DOWN] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoLeft){
                    mWASDInput[LEFT] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoRight){
                    mWASDInput[RIGHT] = false;
                    WASDmovementKeyReleased = true;
                }
                if (WASDmovementKeyReleased) mControlSystem.update(mWASDInput, mEntities, WASDControllsComponent.class);
            }
            @Override
            public void keyTyped(KeyEvent e){
                // System.out.println("Key typed");
            }
            @Override
            public void keyPressed(KeyEvent e) {
                Boolean WASDPressed = false;
                Boolean ArrowsPressed = false;
                if (e.getKeyCode() == PlayerOneUp){
                    mArrowKeysInput[UP] = true;
                    ArrowsPressed = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) { //} || (e.getKeyCode() == KeyEvent.VK_A)) {
                    mArrowKeysInput[LEFT] = true;
                    ArrowsPressed = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {//|| (e.getKeyCode() == KeyEvent.VK_S)){
                    mArrowKeysInput[DOWN] = true;
                    ArrowsPressed = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {//} || (e.getKeyCode() == KeyEvent.VK_D)){
                    mArrowKeysInput[RIGHT] = true;
                    ArrowsPressed = true;
                }
                if (ArrowsPressed) mControlSystem.update(mArrowKeysInput, mEntities, ArrowControllsComponent.class);
                if (e.getKeyCode() == PlayerTwoUp){
                    mWASDInput[UP] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoDown){
                    mWASDInput[DOWN] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoLeft){
                    mWASDInput[LEFT] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoRight){
                    mWASDInput[RIGHT] = true;
                    WASDPressed = true;
                }
                if (WASDPressed) mControlSystem.update(mWASDInput, mEntities, WASDControllsComponent.class);
                
        }});
        setDoubleBuffered(true);

        // Resize the window (insets are just the boards that the Operating System puts on the board)
        Insets insets = frame.getInsets();
        frame.setSize(WINDOW_WIDTH + insets.left + insets.right,
            WINDOW_HEIGHT + insets.top + insets.bottom);
    }

    // Main function that takes care of some Object Oriented stuff
    public static void main(String args[]) {
        new Game();
    }

    public Game() {
        mMovementSystem = new MovementSystem();
        mRenderSystem = new RenderSystem();
        mControlSystem = new ControlSystem();
        mCollisionSystem = new CollisionSystem();
        mGravitySystem = new GravitySystem();
        mEntities = new Vector<Entity>();

        setupWindow();


        Entity controllableCircle = new Entity();
        controllableCircle.addComponent(new CircleComponent(100, 100, 50, Color.GREEN, 20));
        controllableCircle.addComponent(new MovementComponent(0, 0));
        controllableCircle.addComponent(new ControllableComponent());
        controllableCircle.addComponent(new ArrowControllsComponent());
        controllableCircle.addComponent(new WASDControllsComponent());
        mEntities.add(controllableCircle);
        
        
        for (int i=0; i<NUM_OF_SMALL_BALLS; i++){
            Entity smallBall = new Entity();

            int randomX = (int) (WINDOW_WIDTH * Math.random());
            int randomY = (int) (WINDOW_HEIGHT * Math.random());
            int radius = 25;
            Color color = Color.CYAN;
            CircleComponent c = new CircleComponent(randomX, randomY, radius, color, 10);
            try{
            // c.image = ImageIO.read(new File("orange.jpeg"));
            } catch (Exception e) {

            }

            smallBall.addComponent(c);
            smallBall.addComponent(new MovementComponent(0, 0));
            smallBall.addComponent(new GravityComponent());
            mEntities.add(smallBall);
        }
        
        // Last Update
        double last = java.lang.System.nanoTime();
        
        while(!gameIsFinished) {
            double now = java.lang.System.nanoTime();
            
            if (((long)((1000.0/60.0) - (now - last)/(1000000.0))) > 0){
                try {
                    Thread.sleep((long)((1000.0/60.0) - (now - last)/(1000000.0)));
                } catch(InterruptedException ex) {
    
                }
            }
            now = java.lang.System.nanoTime();

            double dt = (now - last) / 1000000000.0;
            fps = 1/dt;
            // java.lang.System.out.println(dt);
            last = now;
            
            mCollisionSystem.update(mEntities);
            mGravitySystem.update(mEntities, dt); 
            mMovementSystem.update(mEntities, dt);
           

            repaint();
            // Toolkit.getDefaultToolkit().sync();
        }
        repaint();
    }

    
    // This gets called any time the Operating System
    // tells the program to paint itself
    public void paint(Graphics g) {
        Toolkit.getDefaultToolkit().sync();
        mRenderSystem.render((Graphics2D)g, mEntities);
    }
}