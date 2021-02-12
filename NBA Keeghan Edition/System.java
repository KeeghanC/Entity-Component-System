import java.awt.*;
import java.io.Console;
import java.util.Random;
import java.util.Vector;
import java.util.ResourceBundle.Control;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.*;
import java.io.*;
import java.io.File;
// import javafx.scene.media.Media;
// import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.text.AttributeSet.ColorAttribute;

import jdk.jfr.Percentage;



public abstract class System {

}
// There isn't a component called RenderableComponent, I just assume everything is rendered except if it has a specific characteristic (like a GoalComponent) which might not be the most ideal in the long-run
class RenderSystem extends System {
	public void render(Graphics2D g, Vector<Entity> entities) {
		// Clear
		g.clearRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

		// //Court -- Not finished
		// BufferedImage court;
		// try {
		// 	court = ImageIO.read(new File("court.png"));
		// 	g.drawImage(court, 0, 1000, 0, 1000, null);
		// } catch (Exception e){
		// 	java.lang.System.out.println("Error reading court.png");
		// }
		g.setColor(Color.black);
		// FPS
		g.drawString("FPS: " + String.valueOf((double)Math.round(Game.fps * 100d) / 100d), 0, 0+10);
		// Score
		g.drawString("SCORE: " + String.valueOf(Game.score), Game.WINDOW_WIDTH/2-50, 10);
		
		// This should really check if the entity has a component called Renderable Component, however I have assumed that certian objects will be renderable
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.elementAt(i);
			// If the entity is a circle, this is how we will render it
			if(entity.hasComponent(CircleComponent.class)) {
				CircleComponent circle = (CircleComponent) entity.getComponent(CircleComponent.class);
				if (circle.image != null){
					double rad = circle.mRadius;

					// Get Current Transform
					AffineTransform current = g.getTransform();

					g.translate(circle.mXPos, circle.mYPos);

					g.rotate(circle.mTheta);

					g.drawImage(circle.image, -(int)rad, -(int)rad, (int)rad*2, (int)rad*2, null);

					g.setTransform(current);
				}
				else {
					g.setColor(Color.BLACK);
					g.drawOval((int)(circle.mXPos - circle.mRadius), (int)(circle.mYPos - circle.mRadius), (int)(circle.mRadius*2), (int)(circle.mRadius*2));
				}
			}

			//If the entity is a rectangle, this is how we will render it
			if (entity.hasComponent(RectangleComponent.class) && (!entity.hasComponent(GoalComponent.class))){
				// Get the rectangle component
				RectangleComponent rectangleComponent = (RectangleComponent) entity.getComponent(RectangleComponent.class);
				
				//Set the color of the graphics
				g.setColor(rectangleComponent.mColor);

				//Draw the rectangle
				g.fillRect((int)rectangleComponent.mTopLeftXPos, (int) rectangleComponent.mTopLeftYPos, (int) rectangleComponent.mWidth, (int) rectangleComponent.mHeight);
			}

			if (entity.hasComponent(ArcComponent.class)){
				ArcComponent a = (ArcComponent) entity.getComponent(ArcComponent.class);
				g.setColor(a.color);
				g.fill(new Arc2D.Double(a.arcTopLeftX, a.arcTopLeftY, a.angle1, a.angle2, a.angle3, a.angle4, a.arcType));
			}
			// Power
		if (Game.mSpacebarPressed){
			g.setColor(Color.black);
			double now = java.lang.System.nanoTime();
			double holdTime = (now - Game.spaceBarHoldTimeStart) / 1000000000.0; 
			if (holdTime > 3) holdTime = 3;
			double percentage = holdTime/3 * 100;
			g.drawString("Power: " + String.valueOf((Math.round(percentage*100)/100)) + "%", 20, Game.WINDOW_HEIGHT-10 - Game.BASKETBALL_RADIUS*2);
			double startX = 0;
			double startY = Game.WINDOW_HEIGHT - (Game.WINDOW_HEIGHT * (percentage/100)); 
			int width = 10;
			double height = (Game.WINDOW_HEIGHT - startY);
			g.setColor(Color.RED);
			g.fillRect((int) startX, (int) startY, width, (int) height);
		}
		}
	}
}

class MovementSystem extends System {
	public void update (Vector<Entity> mEntities, double dt){
		for(Entity entity : mEntities) {
			if (entity.hasComponent(MovementComponent.class)){
				if (entity.hasComponent(CircleComponent.class)){
					CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
					MovementComponent movementComponent = (MovementComponent)entity.getComponent(MovementComponent.class);	

					circle.mXPos = circle.mXPos + movementComponent.xVel * dt;
					circle.mYPos = circle.mYPos + movementComponent.yVel * dt;

					circle.mTheta += (movementComponent.xVel / circle.mRadius) * dt;
				}
			}
		}
	}
}

class ControlSystem extends System {
	
	public void update(Boolean[] mKeysPressed, Vector<Entity> mEntities, Class playerToMove) {
		int SPEED = 500;
		for (Entity entity : mEntities){
			if (entity.hasComponent(ControllableComponent.class)){
				if (entity.hasComponent(MovementComponent.class)){
					if (entity.hasComponent(CircleComponent.class)){
						if (entity.hasComponent(playerToMove)){
							MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
							movementComponent.yVel = 0;
							movementComponent.xVel = 0;
							if (mKeysPressed[Game.UP] == true){
								movementComponent.yVel -= SPEED;
							}
							if (mKeysPressed[Game.DOWN] == true){
								movementComponent.yVel += SPEED;
							}
							if (mKeysPressed[Game.LEFT] == true){
								movementComponent.xVel -= SPEED;
							}
							if (mKeysPressed[Game.RIGHT] == true){
								movementComponent.xVel += SPEED;					
							}
						}
					}
				}
			}
		}
	}

	public void shootBasketBall(Vector<Entity> mEntities, double timeSpacebarHeld){

		double ratio = 0.6; 
		double yStrength = 1000;
		double xStrength = yStrength * ratio;
		int strengthCap = 3;
		if (timeSpacebarHeld > strengthCap) timeSpacebarHeld = strengthCap;
		double shotStrengthXVel = xStrength * timeSpacebarHeld;
		double shotStrengthYVel = -yStrength * timeSpacebarHeld; 

		boolean thereIsABallToShoot = false;
		for (Entity entity : mEntities){
			if ((entity.hasComponent(ShootComponent.class)) && ( entity.hasComponent(MovementComponent.class))){
				MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);

				movementComponent.xVel = shotStrengthXVel;
				movementComponent.yVel = shotStrengthYVel;

				thereIsABallToShoot = true;
			}
		}
		if (!thereIsABallToShoot){
			java.lang.System.out.println("There is no ball to shoot");
		}
	}


}


class CollisionSystem extends System {

	public void update(Vector<Entity> mEntities){
		double bounceDampening = 0.65;
		//Let the balls bounce
		for (Entity entity : mEntities){
			if ((entity.hasComponent(CircleComponent.class)) && (entity.hasComponent(GravityComponent.class))){
				CircleComponent ballCircleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
				MovementComponent ballMovementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);

				double x = ballCircleComponent.mXPos;
				double y = ballCircleComponent.mYPos;
				double rad = ballCircleComponent.mRadius;
				double vX = ballMovementComponent.xVel;
				double vY = ballMovementComponent.yVel;

				// Left wall
				if ((x <= rad) && (vX < 0)){
					ballMovementComponent.xVel = -ballMovementComponent.xVel * bounceDampening;
					ballCircleComponent.mXPos = rad;
				}
				// Right Wall
				if ((x >= Game.WINDOW_WIDTH - rad) && (vX > 0)){
					ballMovementComponent.xVel = -ballMovementComponent.xVel * bounceDampening;
					ballCircleComponent.mXPos = Game.WINDOW_WIDTH - rad;
				}
				// Ceiling
				if ((y <= rad) && (vY < 0)){
					ballMovementComponent.yVel = -ballMovementComponent.yVel * bounceDampening;
					ballCircleComponent.mYPos = rad;
				}
				// Floor
				if ((y + rad > Game.WINDOW_HEIGHT) && (vY > 0)){
					// Go back in time
					double distance = (rad + y) - Game.WINDOW_HEIGHT;
					double time = distance / vY;
					ballMovementComponent.yVel -= GravitySystem.GRAVITY * time;
					ballCircleComponent.mYPos = (Game.WINDOW_HEIGHT - rad);
					
					// Reverse speed
					ballMovementComponent.yVel = -ballMovementComponent.yVel * bounceDampening;
					double rollDampening = 0.999;
					ballMovementComponent.xVel *= rollDampening;
					
					if (entity.hasComponent(BounceComponent.class)){
						BounceComponent bounceComponent = (BounceComponent) entity.getComponent(BounceComponent.class);
						bounceComponent.bounceCount++;
						if (bounceComponent.bounceCount > 5){
							ballCircleComponent.mXPos = Game.BASKETBALL_RADIUS + 20;
							ballCircleComponent.mYPos = Game.WINDOW_HEIGHT - Game.BASKETBALL_RADIUS;

							ballMovementComponent.xVel = 0;
							ballMovementComponent.yVel = 0;
							
							bounceComponent.bounceCount = 0;

							Game.canShoot = true;
						}
						
					}
					if (entity.hasComponent(ShootComponent.class)){
						ShootComponent sh = (ShootComponent) entity.getComponent(ShootComponent.class);
						sh.canScore = true;
					}
				}
			}

		}

		double dampening = 0.8;
		double k = 30;
		// // // Are the balls colliding with each other?
		for (int i = 0; i < mEntities.size(); i++){
			for (int j = i+1; j<mEntities.size(); j++){

				//Case of two circles intersecting
				if ((mEntities.elementAt(i).hasComponent(CircleComponent.class)) && (mEntities.elementAt(j).hasComponent(CircleComponent.class)) ){
					if (collisionCheck(mEntities.elementAt(i), mEntities.elementAt(j))){
						CircleComponent ball1CircleComponent = (CircleComponent) mEntities.elementAt(i).getComponent(CircleComponent.class);
						MovementComponent ball1MovementComponent = (MovementComponent) mEntities.elementAt(i).getComponent(MovementComponent.class);
						
						CircleComponent ball2CircleComponent = (CircleComponent) mEntities.elementAt(j).getComponent(CircleComponent.class);
						MovementComponent ball2MovementComponent = (MovementComponent) mEntities.elementAt(j).getComponent(MovementComponent.class);

						double dx = ball2CircleComponent.mXPos - ball1CircleComponent.mXPos;
						double dy = ball2CircleComponent.mYPos - ball1CircleComponent.mYPos;
						double d = Math.sqrt(dx*dx + dy*dy);
						
						//Get a unit vector
						dx = dx/d;
						dy = dy/d;
		
						// Calculate how much they overlap -- using dan's formula, won't work with big ball
						double displacement = ((ball1CircleComponent.mRadius)*2) - d;

						if (!mEntities.elementAt(i).hasComponent(ControllableComponent.class)){
							// 'Push' the balls away from each other
							ball1MovementComponent.xVel -= k * dx * displacement;
							ball1MovementComponent.yVel -= k * dy * displacement;
							
							// Apply some dampening to stop them getting too energetic
							ball1MovementComponent.xVel *= dampening;
							ball1MovementComponent.yVel *= dampening;
						}
						if (!mEntities.elementAt(j).hasComponent(ControllableComponent.class)){
							ball2MovementComponent.xVel -= k * -dx * displacement;
							ball2MovementComponent.yVel -= k * -dy * displacement;

							ball2MovementComponent.xVel *= dampening;
							ball2MovementComponent.yVel *= dampening;
						}
					}
				}

				Entity e1 = mEntities.elementAt(i);
				Entity e2 = mEntities.elementAt(j);
				Entity rect;
				Boolean e1IsCircle = e1.hasComponent(CircleComponent.class);
				Boolean e1IsRectangle = e1.hasComponent(RectangleComponent.class);
				Boolean e2IsCircle = e2.hasComponent(CircleComponent.class);
				Boolean e2IsRectangle = e2.hasComponent(RectangleComponent.class);

				// If we have a circle and a rectangle
				if (((e1IsCircle) && (e2IsRectangle)) || ((e1IsRectangle) && (e2IsCircle))){
					// Let's check to see if they collide
					Boolean collided = collisionCheck(e1, e2);
					
						

					ShootComponent shootComponent;
					MovementComponent movementComponent;

					RectangleComponent rectangleComponent;
					CircleComponent circleComponent;
					CollidableComponent collidableComponent = new CollidableComponent();
					Boolean hasGoalComponent = false;
					Boolean hasCollidible = false;
					if (e1IsCircle){
						shootComponent = (ShootComponent) e1.getComponent(ShootComponent.class);
						movementComponent = (MovementComponent) e1.getComponent(MovementComponent.class);
						circleComponent = (CircleComponent) e1.getComponent(CircleComponent.class);

						hasGoalComponent = e2.hasComponent(GoalComponent.class);
						rectangleComponent = (RectangleComponent) e2.getComponent(RectangleComponent.class);
						rect = e2;
					} else {
						shootComponent = (ShootComponent) e2.getComponent(ShootComponent.class);
						movementComponent = (MovementComponent) e2.getComponent(MovementComponent.class);
						circleComponent = (CircleComponent) e2.getComponent(CircleComponent.class);
						
						hasGoalComponent = e1.hasComponent(GoalComponent.class);
						rectangleComponent = (RectangleComponent) e1.getComponent(RectangleComponent.class);
						rect = e1;
					}
					if (e2.hasComponent(CollidableComponent.class)){
						collidableComponent = (CollidableComponent) e2.getComponent(CollidableComponent.class);
						hasCollidible = true;
					}
					else if (e1.hasComponent(CollidableComponent.class)){
						collidableComponent = (CollidableComponent) e1.getComponent(CollidableComponent.class);
						hasCollidible = true;
					}
					if (rect.hasComponent(GoalComponent.class)){
						GoalComponent goalComponent = (GoalComponent) rect.getComponent(GoalComponent.class);
						if (collided){
							if (goalComponent.playSound){
								String randomGoalSound = "goal";
								String extension = ".wav";
								Random rand = new Random();
								int oneToFive = rand.nextInt(4);
								randomGoalSound += String.valueOf(oneToFive) + extension;
								SoundSystem.playSound(randomGoalSound);
								goalComponent.playSound = false;
							}
						}
						else goalComponent.playSound = true;
					}
					if (hasCollidible){
						if ((collided) && (collidableComponent.playSound == true)) {
							SoundSystem.playSound("bounce.wav");
							// java.lang.System.out.println("Collided");
							collidableComponent.playSound = false;
						}
						if (!collided){
							collidableComponent.playSound = true;
						}
					}
					if ((collided) && (shootComponent.canScore)) {//} && ((e1.hasComponent(GoalComponent.class)) || (e2.hasComponent(GoalComponent.class)))){
						// java.lang.System.out.println("Scored");
						if ((hasGoalComponent) && (movementComponent.yVel > 0)){
							Game.score++;
							shootComponent.canScore = false;
						}
					}
				}
			}
		}
		return;
	}


	private boolean collisionCheck(Entity entityOne, Entity entityTwo) {
		boolean collided = false;
		if ((entityOne.hasComponent(CircleComponent.class)) && (entityTwo.hasComponent(CircleComponent.class))){
			if (entityOne == entityTwo) return false;
			CircleComponent circle1 = (CircleComponent)entityOne.getComponent(CircleComponent.class);
			CircleComponent circle2 = (CircleComponent)entityTwo.getComponent(CircleComponent.class);
			
			double dx = circle1.mXPos - circle2.mXPos;
			double dy = circle1.mYPos - circle2.mYPos;
			
			double distance = Math.sqrt(dx*dx + dy*dy);
			
			if (distance < circle1.mRadius + circle2.mRadius){
				collided = true;
			}
			return collided;
		}

		/*
			Rectangle and circle collision check / Circle and rectangle collision check
		*/
		Entity circle = new Entity();
		Entity rectangle = new Entity();
		CircleComponent circleComponent; 
		MovementComponent circleMovementComponent;

		RectangleComponent rectangleComponent;

		Boolean e1IsCircle = entityOne.hasComponent(CircleComponent.class);
		Boolean e1IsRectangle = entityOne.hasComponent(RectangleComponent.class);
		Boolean e2IsCircle = entityTwo.hasComponent(CircleComponent.class);
		Boolean e2IsRectangle = entityTwo.hasComponent(RectangleComponent.class);
		
		if ((((e1IsCircle) && (e2IsRectangle))) || ((e1IsRectangle) && (e2IsCircle))){
			if (entityOne == entityTwo) return false;
			if (e1IsCircle){
				circle = entityOne;
				circleComponent = (CircleComponent) entityOne.getComponent(CircleComponent.class);
				circleMovementComponent = (MovementComponent) entityOne.getComponent(MovementComponent.class);
				
				rectangleComponent = (RectangleComponent) entityTwo.getComponent(RectangleComponent.class);
				rectangle = entityTwo;
				// if (!entityTwo.hasComponent(CollidableComponent.class)) return false;
			}
			else if (e2IsCircle) {
				circle = entityTwo;
				circleMovementComponent = (MovementComponent) entityTwo.getComponent(MovementComponent.class);
				circleComponent = (CircleComponent) entityTwo.getComponent(CircleComponent.class);

				rectangleComponent = (RectangleComponent) entityOne.getComponent(RectangleComponent.class);
				rectangle = entityOne;
				// if (!entityOne.hasComponent(CollidableComponent.class)) return false;
			} else return false;
			double rw = rectangleComponent.mWidth;
			double rh = rectangleComponent.mHeight;

			double ry = rectangleComponent.mTopLeftYPos;
			double rx = rectangleComponent.mTopLeftXPos;

			double cx = circleComponent.mXPos;
			double cy = circleComponent.mYPos;
			double cr = circleComponent.mRadius;

			double RectanglesClosestXToCircle = cx;
			double RectanglesClosestYToCircle = cy;


			int top = 0;
			int bottom = 1;
			int left = 2;
			int right = 3;

			Boolean[] ballPosition = new Boolean[]{false,false,false,false};
			// Left
			if (cx < rx){
				RectanglesClosestXToCircle = rx;
				// java.lang.System.out.println("Left");
				ballPosition[left] = true;
			}
			// Right
			else if (cx > rx + rw){
				RectanglesClosestXToCircle = rx+rw;
				// java.lang.System.out.println("Right");
				ballPosition[right] = true;
			}

			// Up
			if (cy < ry) {
				RectanglesClosestYToCircle = ry;
				// java.lang.System.out.println("Top");
				ballPosition[top] = true;
			}
			// Down
			else if (cy > ry + rh) {
				RectanglesClosestYToCircle = ry + rh;
				// java.lang.System.out.println("Bottom");
				ballPosition[bottom] = true;
			}

			double dx = cx - RectanglesClosestXToCircle;
			double dy = cy - RectanglesClosestYToCircle;

			double distance = Math.sqrt((dx*dx) + (dy*dy));

			double ux = dx/distance;
			double uy = dy/distance;

			// java.lang.System.out.println(distance);

			if (distance <= cr){
				if (e1IsRectangle){
					if (!entityOne.hasComponent(CollidableComponent.class)) return true;
				}
				if (e2IsRectangle){
					if (!entityTwo.hasComponent(CollidableComponent.class)) return true;
				}
				double overlap = cr-distance;
				/*
						  0_|______1________|_2
						    |				|
						  3 |				| 4
						   _|_______________|_
						  5 |       6       | 7
				*/
				//java.lang.System.out.println("");
				// Corner collisions aren't too similar to real life, sometimes this is noticeable
				// 0
				double rad = circleComponent.mRadius;
				double x = circleComponent.mXPos;
				double y = circleComponent.mYPos;
				double vX = circleMovementComponent.xVel;
				double vY = circleMovementComponent.yVel;
				double totalMomentum = Math.abs(Math.abs(circleMovementComponent.xVel) + Math.abs(circleMovementComponent.yVel));
				double dampening = 0.40;
				if ((ballPosition[top]) && (ballPosition[left]) && (!ballPosition[right]) && (!ballPosition[bottom])){
					circleMovementComponent.xVel -= -1 * (ux*0.5 * (totalMomentum/2)) * overlap * dampening; // ux * 0.5 to try adjust to the inaccurate corner collision
					circleMovementComponent.yVel -= -1 * (uy*0.5 * (totalMomentum/2)) * overlap * dampening; // uy * 0.5 to try adjust to the inaccurate corner collision
				}
				// 1
				else if ((ballPosition[top]) && (!ballPosition[left]) && (!ballPosition[right]) && (!ballPosition[bottom])){
					// java.lang.System.out.println("1");

					// Go back in time
					if (vY > 0) {
						double displacement = (rad + y) - rectangleComponent.mTopLeftYPos;
						double time = displacement / vY;
						circleMovementComponent.yVel -= (float) (GravitySystem.GRAVITY * time);
					

					circleComponent.mYPos = (int)(rectangleComponent.mTopLeftYPos) - rad;
					circleMovementComponent.yVel *= -1 * dampening;

					double rollDampening = 0.999;
					circleMovementComponent.xVel *= rollDampening;
					
					if ((circle.hasComponent(BounceComponent.class)) && (rectangle.hasComponent(FloorComponent.class)) ){
						BounceComponent bounceComponent = (BounceComponent) circle.getComponent(BounceComponent.class);
						bounceComponent.bounceCount++;
						if (bounceComponent.bounceCount > 5){
							circleComponent.mXPos = Game.BASKETBALL_RADIUS + 20;
							circleComponent.mYPos = rectangleComponent.mTopLeftYPos - Game.BASKETBALL_RADIUS;

							circleMovementComponent.xVel = 0;
							circleMovementComponent.yVel = 0;
							
							bounceComponent.bounceCount = 0;

							Game.canShoot = true;
						}
						
					}
					if (circle.hasComponent(ShootComponent.class)){
						ShootComponent sh = (ShootComponent) circle.getComponent(ShootComponent.class);
						sh.canScore = true;
					
					}
				}
				}
				// 2
				else if ((ballPosition[top]) && (!ballPosition[left]) && (ballPosition[right]) && (!ballPosition[bottom])){
					// java.lang.System.out.println("2");
					circleMovementComponent.xVel -= -1 * (ux * (totalMomentum/2)) * overlap * dampening;
					circleMovementComponent.yVel -= -1 * (uy * (totalMomentum/2)) * overlap * dampening;
				}
				// 3
				else if (!(ballPosition[top]) && (ballPosition[left]) && (!ballPosition[right]) && (!ballPosition[bottom])){
					// java.lang.System.out.println("3");
					circleComponent.mXPos = rectangleComponent.mTopLeftXPos - rad;
					circleMovementComponent.xVel *= -1 * dampening;
				}
				// 4
				else if (!(ballPosition[top]) && (!ballPosition[left]) && (ballPosition[right]) && (!ballPosition[bottom])){
					// java.lang.System.out.println("4");
					circleComponent.mXPos = rectangleComponent.mTopLeftXPos + rectangleComponent.mWidth + rad;
					circleMovementComponent.xVel *= -1 * dampening;
				}
				// 5
				else if (!(ballPosition[top]) && (ballPosition[left]) && (!ballPosition[right]) && (ballPosition[bottom])){
					// java.lang.System.out.println("5");
					circleMovementComponent.xVel -= -1 * (ux * (totalMomentum/2)) * overlap * dampening;
					circleMovementComponent.yVel -= -1 * (uy * (totalMomentum/2)) * overlap * dampening;
				}
				// 6
				else if (!(ballPosition[top]) && (!ballPosition[left]) && (!ballPosition[right]) && (ballPosition[bottom])){
					// java.lang.System.out.println("6");
					circleComponent.mYPos = rectangleComponent.mTopLeftYPos + rectangleComponent.mHeight + rad;
					circleMovementComponent.yVel *= -1 * dampening;
				}
				// 7
				else if (!(ballPosition[top]) && (!ballPosition[left]) && (ballPosition[right]) && (ballPosition[bottom])){
					// java.lang.System.out.println("7");
					circleMovementComponent.xVel -= -1 * (ux * (totalMomentum/2)) * overlap * dampening;
					circleMovementComponent.yVel -= -1 * (uy * (totalMomentum/2)) * overlap * dampening;
				}
				else return false;
				return true;
			}
		}
		return collided;
	}
}
class GravitySystem extends System {
	public static double GRAVITY = 500;
	public void update (Vector<Entity> mEntities, double dt){
		for (Entity entity : mEntities){
			if ((entity.hasComponent(GravityComponent.class)) && (entity.hasComponent(CircleComponent.class))){
				MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
				movementComponent.yVel += GRAVITY * dt; 
			}
		}
	}
}

// Sound system breaks after a while, needs to be fixed with error,
// 		line with format PCM_SIGNED 48000.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian not supported.
class SoundSystem extends System {
	public static void playSound(final String url) {
		new Thread(new Runnable() {
		// The wrapper thread is unnecessary, unless it blocks on the
		// Clip finishing; see comments.
		  public void run() {
			try {
			  Clip clip = AudioSystem.getClip();
			  AudioInputStream inputStream = AudioSystem.getAudioInputStream(
				SoundSystem.class.getResourceAsStream("" + url));
			  clip.open(inputStream);
			  clip.start(); 
				Thread.sleep(1000000000);;
			} catch (Exception e) {
			  java.lang.System.err.println(e.getMessage());
			}
		  }
		}).start();
	  }
}