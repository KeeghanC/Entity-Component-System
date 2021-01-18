import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.awt.event.KeyListener;

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

    public static int NUM_OF_FOOD = 100;

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
    Boolean[] mPlayer1KeysCurrentlyPressed = new Boolean[]{false,false,false,false};
    Boolean[] mPlayer2KeysCurrentlyPressed = new Boolean[]{false,false,false,false};
    
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
                    mPlayer1KeysCurrentlyPressed[UP] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) { //} || (e.getKeyCode() == KeyEvent.VK_A)) {
                    mPlayer1KeysCurrentlyPressed[LEFT] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {//|| (e.getKeyCode() == KeyEvent.VK_S)){
                    mPlayer1KeysCurrentlyPressed[DOWN] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {//} || (e.getKeyCode() == KeyEvent.VK_D)){
                    mPlayer1KeysCurrentlyPressed[RIGHT] = false;
                    ArrowsKeysReleased = true;
                }
                if (ArrowsKeysReleased) mControlSystem.update(mPlayer1KeysCurrentlyPressed, mEntities, ArrowControllsComponent.class);
                if (e.getKeyCode() == PlayerTwoUp){
                    mPlayer2KeysCurrentlyPressed[UP] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoDown){
                    mPlayer2KeysCurrentlyPressed[DOWN] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoLeft){
                    mPlayer2KeysCurrentlyPressed[LEFT] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerTwoRight){
                    mPlayer2KeysCurrentlyPressed[RIGHT] = false;
                    WASDmovementKeyReleased = true;
                }
                if (WASDmovementKeyReleased) mControlSystem.update(mPlayer2KeysCurrentlyPressed, mEntities, WASDControllsComponent.class);
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
                    mPlayer1KeysCurrentlyPressed[UP] = true;
                    ArrowsPressed = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) { //} || (e.getKeyCode() == KeyEvent.VK_A)) {
                    mPlayer1KeysCurrentlyPressed[LEFT] = true;
                    ArrowsPressed = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {//|| (e.getKeyCode() == KeyEvent.VK_S)){
                    mPlayer1KeysCurrentlyPressed[DOWN] = true;
                    ArrowsPressed = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {//} || (e.getKeyCode() == KeyEvent.VK_D)){
                    mPlayer1KeysCurrentlyPressed[RIGHT] = true;
                    ArrowsPressed = true;
                }
                if (ArrowsPressed) mControlSystem.update(mPlayer1KeysCurrentlyPressed, mEntities, ArrowControllsComponent.class);
                if (e.getKeyCode() == PlayerTwoUp){
                    mPlayer2KeysCurrentlyPressed[UP] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoDown){
                    mPlayer2KeysCurrentlyPressed[DOWN] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoLeft){
                    mPlayer2KeysCurrentlyPressed[LEFT] = true;
                    WASDPressed = true;
                }
                if (e.getKeyCode() == PlayerTwoRight){
                    mPlayer2KeysCurrentlyPressed[RIGHT] = true;
                    WASDPressed = true;
                }
                if (WASDPressed) mControlSystem.update(mPlayer2KeysCurrentlyPressed, mEntities, WASDControllsComponent.class);
                
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
        mEntities = new Vector<Entity>();

        setupWindow();


        Entity Player1Blob = new Entity();
        Player1Blob.addComponent(new CircleComponent(100, 100, 50, Color.GREEN));
        Player1Blob.addComponent(new MovementComponent(0, 0));
        Player1Blob.addComponent(new ControllableComponent());
        Player1Blob.addComponent(new HungryComponent());
        Player1Blob.addComponent(new ArrowControllsComponent());
        mEntities.add(Player1Blob);
        
        Entity Player2Blob = new Entity();
        Player2Blob.addComponent(new CircleComponent(WINDOW_WIDTH-100, WINDOW_HEIGHT - 100, 50, Color.RED));
        Player2Blob.addComponent(new MovementComponent(0, 0));
        Player2Blob.addComponent(new ControllableComponent());
        Player2Blob.addComponent(new HungryComponent());
        Player2Blob.addComponent(new WASDControllsComponent());
        mEntities.add(Player2Blob);
        
        for (int i=0; i<NUM_OF_FOOD; i++){
            Entity pieceOfFood = new Entity();

            int randomX = (int) (WINDOW_WIDTH * Math.random());
            int randomY = (int) (WINDOW_HEIGHT * Math.random());
            int radius = 5;
            Color color = Color.CYAN;

            pieceOfFood.addComponent(new CircleComponent(randomX, randomY, radius, color));
            pieceOfFood.addComponent(new FoodComponent());
            mEntities.add(pieceOfFood);
        }

        // Last Update
        double last = java.lang.System.nanoTime();

        while(!gameIsFinished) {
            double now = java.lang.System.nanoTime();
            
            try {
                Thread.sleep((long)((1000.0/60.0) - (now - last)/(1000000.0)));
            } catch(InterruptedException ex) {

            }
            
            now = java.lang.System.nanoTime();

            double dt = (now - last) / 1000000000.0;
            
            // java.lang.System.out.println(dt);
            last = now;

            mMovementSystem.update(mEntities, dt);
            
            mCollisionSystem.update(mEntities);
           

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