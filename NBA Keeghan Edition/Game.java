import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Vector;
import java.awt.event.KeyListener;
import java.awt.geom.Arc2D;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
/*
 TODO:

*/


public class Game extends JPanel {
    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;
    
    public static int difficulty = 1;

    public static boolean gameIsFinished = false;
    public static String scoreString = "";
    public static int score = 0;
    public static int UP = 0;
    public static int DOWN = 1;
    public static int LEFT = 2;
    public static int RIGHT = 3;

    public static int BASKETBALL_RADIUS = 35;
    public static int WINNING_SCORE = 400;

    // Shoot mechanic
    public static Boolean canShoot = true;
    int ShootKey = KeyEvent.VK_SPACE;
    public static double spaceBarHoldTimeStart = 0;
    public static double spaceBarHoldTimeEnd = 0;
    
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

    // Colors
    Color poleColor = new Color(163, 169, 171);
    Color backBoardColor = new Color(166, 55, 21);
    Color hoopColor = new Color(150,50,18);

    Vector<Entity> mEntities = new Vector<Entity>();
    
    RenderSystem mRenderSystem;
    MovementSystem mMovementSystem;
    ControlSystem mControlSystem;
    CollisionSystem mCollisionSystem;
    GravitySystem mGravitySystem;
    SoundSystem mSoundSystem;

    Boolean[] mArrowKeysInput = new Boolean[]{false,false,false,false};
    Boolean[] mWASDInput = new Boolean[]{false,false,false,false};
    public static Boolean mSpacebarPressed = false;
    public static double fps;
    
    // Function to create the window and display it
    public void setupWindow() {
        JFrame frame = new JFrame();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocation(200,200);
        frame.setTitle("NBA: Keeghan Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.addKeyListener(new KeyListener(){
            @Override
            public void keyReleased(KeyEvent e){
                Boolean WASDmovementKeyReleased = false;
                Boolean ArrowsKeysReleased = false;
                if (e.getKeyCode() == ShootKey){
                    // java.lang.System.out.println("Space");
                    if(canShoot){
                        spaceBarHoldTimeEnd = java.lang.System.nanoTime();
                        double holdTime = (spaceBarHoldTimeEnd - spaceBarHoldTimeStart) / 1000000000.0;                  
                        mSpacebarPressed = false;
                        mControlSystem.shootBasketBall(mEntities, holdTime);
                        canShoot = false;
                    }
                }
                if (e.getKeyCode() == PlayerTwoRight){
                    mWASDInput[RIGHT] = false;
                    WASDmovementKeyReleased = true;
                }
                if (e.getKeyCode() == PlayerOneUp){
                    mArrowKeysInput[UP] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) {
                    mArrowKeysInput[LEFT] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {
                    mArrowKeysInput[DOWN] = false;
                    ArrowsKeysReleased = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {
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
                if (e.getKeyCode() == ShootKey){
                    if ((!mSpacebarPressed) && (canShoot)){
                        spaceBarHoldTimeStart = java.lang.System.nanoTime();
                        mSpacebarPressed = true;
                    }
                }
                if (e.getKeyCode() == PlayerOneUp){
                    mArrowKeysInput[UP] = true;
                    ArrowsPressed = true;
                }
                //Move main piece left
                if (e.getKeyCode() == PlayerOneLeft) { 
                    mArrowKeysInput[LEFT] = true;
                    ArrowsPressed = true;
                }
                //Move main piece down
                if (e.getKeyCode() == PlayerOneDown) {
                    mArrowKeysInput[DOWN] = true;
                    ArrowsPressed = true;
                }
                //Move main piece right
                if (e.getKeyCode() == PlayerOneRight) {
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
        frame.setSize(WINDOW_WIDTH + insets.left + insets.right, WINDOW_HEIGHT + insets.top + insets.bottom);
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
        mSoundSystem = new SoundSystem();
        mEntities = new Vector<Entity>();

        setupWindow();
        // SoundSystem.playSound("ahem.wav");

        // Floor
        double floorWidth = Game.WINDOW_WIDTH;
        double floorHeight = 100;
        double floorX  = 0;
        double floorY   = Game.WINDOW_HEIGHT - floorHeight;
        
        Entity floor = new Entity();
        floor.addComponent(new RectangleComponent(poleColor, floorX, floorY, floorHeight, floorWidth));
        floor.addComponent(new CollidableComponent());
        floor.addComponent(new FloorComponent());
        mEntities.add(floor);


        for (int i = 0; i<1;i++){
        // Basketball
            Entity basketBall = new Entity();
            Random rand = new Random();
            double x = Game.BASKETBALL_RADIUS + 20;//(double) rand.nextInt(Game.WINDOW_WIDTH);
            double y = Game.WINDOW_HEIGHT/2 - Game.BASKETBALL_RADIUS - floorHeight;
            CircleComponent basketBallCircleComponent = new CircleComponent(x, y, BASKETBALL_RADIUS, Color.GREEN, 20);
            try {
                basketBallCircleComponent.image = ImageIO.read(new File("basketball.png"));
            } catch (Exception e){
                java.lang.System.out.println("Error reading basketball2.gif");
            }
            basketBall.addComponent(basketBallCircleComponent);
            basketBall.addComponent(new MovementComponent(0, 0));
            basketBall.addComponent(new ControllableComponent());
            basketBall.addComponent(new WASDControllsComponent());
            basketBall.addComponent(new GravityComponent());
            basketBall.addComponent(new ShootComponent());
            basketBall.addComponent(new BounceComponent());
            mEntities.add(basketBall);
        }
        // Ignore -------------------------------------------------------------------------
        Entity testBoard = new Entity();
        double height  = 60;
        double width = 200;
        double x =  1000;//WINDOW_WIDTH/2 - width/2; //860
        double y =  WINDOW_HEIGHT/2 - height/2;
        testBoard.addComponent(new RectangleComponent(Color.green, x, y, height, width));
        testBoard.addComponent(new CollidableComponent());
        // mEntities.add(testBoard);
        // -------------------------------------------------------------------------------

        // Main support pole
        double poleTopLeftX = Game.WINDOW_WIDTH - Game.WINDOW_WIDTH/4;
        double poleTopLeftY = Game.WINDOW_HEIGHT/2;
        double poleWidth    = 20;
        double poleHeight   = Game.WINDOW_HEIGHT - poleTopLeftY - floorHeight;
        
        Entity mainSupportPole = new Entity();
        mainSupportPole.addComponent(new RectangleComponent(poleColor, poleTopLeftX, poleTopLeftY, poleHeight, poleWidth));
        mainSupportPole.addComponent(new CollidableComponent());
        mEntities.add(mainSupportPole);

        // Arc
        double arcTopLeftX = poleTopLeftX - 20;
        double arcTopLeftY = poleTopLeftY - 20;
        double angle1      = 40;
        double angle2      = 40;
        double angle3      = 0;
        double angle4      = 90;
        int    arcType     = Arc2D.PIE;  

        Entity arc = new Entity();
        arc.addComponent(new ArcComponent(arcTopLeftX, arcTopLeftY, angle1, angle2, angle3, angle4, arcType, poleColor));
        mEntities.add(arc);
        
        // Backboard
        double backboardWidth    = 20;
        double backboardHeight   = 200;
        double backboardTopLeftX = arcTopLeftX - backboardWidth + 20;
        double backboardTopLeftY = arcTopLeftY - backboardHeight*0.75;

        Entity backBoard = new Entity();
        backBoard.addComponent(new RectangleComponent(backBoardColor, backboardTopLeftX, backboardTopLeftY, backboardHeight, backboardWidth));
        backBoard.addComponent(new CollidableComponent());
        mEntities.add(backBoard);

        // Backboard to net lip
        double hoopBackWidth    = 15;
        double hoopBackHeight   = 10;
        double hoopBackTopLeftX = backboardTopLeftX - hoopBackWidth;
        double hoopBackTopLeftY = arcTopLeftY;

        Entity hoopBack = new Entity();
        hoopBack.addComponent(new RectangleComponent(Color.red, hoopBackTopLeftX, hoopBackTopLeftY, hoopBackHeight, hoopBackWidth));
        hoopBack.addComponent(new CollidableComponent());
        mEntities.add(hoopBack);

        // Hoop 
        double hoopWidth    = Math.round(Game.BASKETBALL_RADIUS*2 * 1.8927444795); // Ball to hoop ratio in real life is 1:1.8927444795
        double hoopHeight   = 14;
        double hoopTopLeftX = hoopBackTopLeftX - hoopWidth;
        double hoopTopLeftY = hoopBackTopLeftY - ((hoopHeight-hoopBackHeight)/2);

        Entity hoop = new Entity();
        hoop.addComponent(new RectangleComponent(hoopColor, hoopTopLeftX, hoopTopLeftY, hoopHeight, hoopWidth));
        mEntities.add(hoop);

        // Front lip
        double hoopLipWidth    = 6;
        double hoopLipHeight   = hoopHeight;
        double hoopLipTopLeftX = hoopTopLeftX - hoopLipWidth;
        double hoopLipTopLeftY = hoopTopLeftY;

        Entity hoopLip = new Entity();
        hoopLip.addComponent(new RectangleComponent(Color.red, hoopLipTopLeftX, hoopLipTopLeftY, hoopLipHeight, hoopLipWidth));
        hoopLip.addComponent(new CollidableComponent());
        mEntities.add(hoopLip);

        // Scoring rectangle
        double scoreRectangleWidth    = hoopWidth;
        double scoreRectangleHeight   = hoopHeight/2;
        double scoreRectangleTopLeftX = hoopTopLeftX;
        double scoreRectangleTopLeftY = hoopBackTopLeftY + hoopBackWidth/2 + 5;

        Entity scoreRectangle = new Entity();
        scoreRectangle.addComponent(new RectangleComponent(Color.cyan, scoreRectangleTopLeftX, scoreRectangleTopLeftY, scoreRectangleHeight, scoreRectangleWidth));
        scoreRectangle.addComponent(new GoalComponent());
        mEntities.add(scoreRectangle);
        // ------------------------------------------------------------------------------------------------------

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