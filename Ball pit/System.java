import java.awt.*;
import java.io.Console;
import java.util.Vector;
import java.util.ResourceBundle.Control;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.text.AttributeSet.ColorAttribute;

public abstract class System {

}

class RenderSystem extends System {
	public void render(Graphics2D g, Vector<Entity> entities) {
		int screenHeight = 1440;
		int screenWidth = 5000;
		g.clearRect(0, 0, 5000, 1080);
		g.drawString("FPS: " + String.valueOf((double)Math.round(Game.fps * 100d) / 100d), 0, 0+10);
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.elementAt(i);
			if(entity.hasComponent(CircleComponent.class)) {
				CircleComponent circle = (CircleComponent) entity.getComponent(CircleComponent.class);
				if (circle.image != null){
					double rad = circle.mRadius;
					double x = circle.mXPos - rad;
					double y = circle.mYPos - rad;
					double h = circle.mRadius*2;
					double w = h;
					g.drawImage(circle.image, (int)x, (int)y, (int)w, (int)h, null);
				}
				g.setColor(Color.BLACK);
				// g.setColor(circle.mColor);
				g.drawOval((int)(circle.mXPos - circle.mRadius), (int)(circle.mYPos - circle.mRadius), (int)(circle.mRadius*2), (int)(circle.mRadius*2));
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
				}
			}
		}
	}
}

class ControlSystem extends System {
	
	public void update(Boolean[] mKeysPressed, Vector<Entity> mEntities, Class playerToMove) {

	int SPEED = 200;
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
}


class CollisionSystem extends System {

	public void update(Vector<Entity> mEntities){
		//Let the balls bounce
		for (Entity ball : mEntities){
			if ((ball.hasComponent(CircleComponent.class)) && (ball.hasComponent(GravityComponent.class))){
				CircleComponent ballCircleComponent = (CircleComponent) ball.getComponent(CircleComponent.class);
				MovementComponent ballMovementComponent = (MovementComponent) ball.getComponent(MovementComponent.class);
				double x = ballCircleComponent.mXPos;
				double y = ballCircleComponent.mYPos;
				double rad = ballCircleComponent.mRadius;
				double vX = ballMovementComponent.xVel;
				double vY = ballMovementComponent.yVel;

				// Left wall
				if ((x <= rad) && (vX < 0)){
					ballMovementComponent.xVel = -ballMovementComponent.xVel * 0.65;
					ballCircleComponent.mXPos = rad;
				}
				// Right Wall
				if ((x >= Game.WINDOW_WIDTH - rad) && (vX > 0)){
					ballMovementComponent.xVel = -ballMovementComponent.xVel * 0.65;
					ballCircleComponent.mXPos = Game.WINDOW_WIDTH - rad;
				}
				// Ceiling
				if ((y <= rad) && (vY < 0)){
					ballMovementComponent.yVel = -ballMovementComponent.yVel * 0.65;
					ballCircleComponent.mYPos = rad;
				}
				// Floor
				if ((y + rad > Game.WINDOW_HEIGHT) && (vY > 0)){
					// java.lang.System.out.println("Collided with the floor");

					// If the velocity is so small (to avoid infinite bounching), set it to 0
					// double smallBounceThreshold = 1;
					// if (ballMovementComponent.yVel < smallBounceThreshold){
					// 	ballMovementComponent.yVel = 0;
					// 	ballCircleComponent.mYPos = Game.WINDOW_HEIGHT - rad;
					// 	continue;
					// }

					// Go back in time
					double distance = (rad + y) - Game.WINDOW_HEIGHT;
					double time = distance / vY;
					ballMovementComponent.yVel -= GravitySystem.GRAVITY * time;
					ballCircleComponent.mYPos = (Game.WINDOW_HEIGHT - rad);// - ballMovementComponent.yVel*time; (why move it back up? we want it to bounce from the ground not the air?)
					
					// Reverse speed
					ballMovementComponent.yVel = -ballMovementComponent.yVel * 0.65;
				}
			}

		}

		double dampening = 0.8;
		double k = 30;
		// // // Are the balls colliding with each other?
		for (int i = 0; i < mEntities.size(); i++){
			for (int j = i+1; j<mEntities.size(); j++){
				if (collisionCheck(mEntities.elementAt(i), mEntities.elementAt(j))){
					CircleComponent ball1CircleComponent = (CircleComponent) mEntities.elementAt(i).getComponent(CircleComponent.class);
					MovementComponent ball1MovementComponent = (MovementComponent) mEntities.elementAt(i).getComponent(MovementComponent.class);
					
					CircleComponent ball2CircleComponent = (CircleComponent) mEntities.elementAt(j).getComponent(CircleComponent.class);
					MovementComponent ball2MovementComponent = (MovementComponent) mEntities.elementAt(j).getComponent(MovementComponent.class);

		// 			double vx1 = ball1MovementComponent.xVel;
		// 			double vy1 = ball1MovementComponent.yVel;
		// 			double m1 = ball1CircleComponent.mMass;
					
		// 			double vx2 = ball2MovementComponent.xVel;
		// 			double vy2 = ball2MovementComponent.yVel;
		// 			double m2 = ball2CircleComponent.mMass;

		// 			ball1MovementComponent.xVel = (vx1 * (m1 - m2) + (2 * m2 * vx2)) / (m1 + m2);
		// 			ball1MovementComponent.yVel = (vy1 * (m1 - m2) + (2 * m2 * vy2)) / (m1 + m2);
		// 		}
		// 	}
		// }

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
		}
		// // for (Entity entityOne : mEntities){
		// 	for (Entity entityTwo : mEntities){
		// 		if (collisionCheck(entityOne, entityTwo) && (entityOne != entityTwo)){
		// 			// java.lang.System.out.println("A collision has occurred");
		// 		}
		// 	}
		// }
		return;
	}

	private boolean collisionCheck(Entity entityOne, Entity entityTwo) {
		boolean collided = false;

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
}

class GravitySystem extends System {
	public static double GRAVITY = 500;
	public void update (Vector<Entity> mEntities, double dt){
		for (Entity entity : mEntities){
			if ((entity.hasComponent(GravityComponent.class)) && (entity.hasComponent(CircleComponent.class))){
				MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
				CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
				
				// Apply gravity
				movementComponent.yVel += GRAVITY * dt; 
				java.lang.System.out.println(movementComponent.yVel);

				// Enable this if you only want gravity to occur to elements on screen
				// if (circleComponent.mYPos + circleComponent.mRadius < Game.WINDOW_HEIGHT){
					// movementComponent.yVel += GRAVITY * dt; 
					// java.lang.System.out.println(movementComponent.yVel);
				// }
			}
		}
	}
}