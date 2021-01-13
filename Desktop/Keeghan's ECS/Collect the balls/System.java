import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import static java.lang.System.*;
public abstract class System {

}

class RenderSystem extends System {
	public void render(Graphics2D g, Vector<Entity> entities) {
		g.clearRect(0, 0, 1920, 1080);

		// For Each Entity
		for(Entity entity : entities) {
			// If a circle with a position
			if(entity.hasComponent(CircleComponent.class)) {
				java.lang.System.out.println("Drawing a circle");
				// Get circle
				CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);

				MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
				
				// Save current transform
				// AffineTransform current = g.getTransform();

				// Apply Transform (TODO replace with transform.getTransform())
				// g.translate(transform.mX, transform.mY);

				int diameter = circle.Radius * 2; 
				// Draw the Render component
				g.setColor(Color.RED);
				g.fillOval(circle.xPos - circle.Radius, circle.yPos - circle.Radius, diameter, diameter);

			}
		}
	}
}

class MovementSystem extends System {
	public static int SPEED = 26;
	public static int SLOWING_FACTOR = SPEED/2;
	enum Direction{
		left, right, up, down;
	}
	
	public void update (Vector<Entity> mEntities){
		for(Entity entity : mEntities) {
			if ((entity.hasComponent(MovementComponent.class)) && (entity.hasComponent(CircleComponent.class))){
				CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
				MovementComponent movementComponent = (MovementComponent)entity.getComponent(MovementComponent.class);
				circle.xPos += movementComponent.xVel;
				circle.yPos += movementComponent.yVel;
				
				//Slow the object down
				if (movementComponent.xVel > 0){ 
					movementComponent.xVel -= SLOWING_FACTOR;
				} else if (movementComponent.xVel < 0) {
					movementComponent.xVel += SLOWING_FACTOR;
				}
				if (movementComponent.yVel > 0){ 
					movementComponent.yVel -= SLOWING_FACTOR;
				} else if (movementComponent.yVel < 0) {
					movementComponent.yVel += SLOWING_FACTOR;
				}
			}
		}
	}
}

class ControlSystem extends System {
	
	public ControlSystem(){
		
	}	
	
	public void update(MovementSystem.Direction direction, Vector<Entity> mEntities) {
		for (Entity entity : mEntities){
			if ((entity.hasComponent(ControllableComponent.class)) && (entity.hasComponent(MovementComponent.class))){
				if (direction == MovementSystem.Direction.up){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					movementComponent.yVel -= MovementSystem.SPEED;
				}
				if (direction == MovementSystem.Direction.down){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					movementComponent.yVel += MovementSystem.SPEED;
				}
				if (direction == MovementSystem.Direction.left){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					movementComponent.xVel -= MovementSystem.SPEED;
					
				}
				if (direction == MovementSystem.Direction.right){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					movementComponent.xVel += MovementSystem.SPEED;
					
				}
			}
		}
	}
}

	// class PhysicsSystem extends System {
		// 	public void update(Vector<Entity> entities, double dt) {
			// 		// For Each Entity
			// 		for(Entity entity : entities) {
				// 			// That has a Transform and a Physics Component
// 			if(entity.hasComponent(TransformComponent.class) && entity.hasComponent(PhysicsComponent.class)) {
	// 				// Get Transform and Render Components
	// 				TransformComponent transform = (TransformComponent)entity.getComponent(TransformComponent.class);
	// 				PhysicsComponent   physics   = (PhysicsComponent)entity.getComponent(PhysicsComponent.class);
	
	// 				physics.mVY += 98 * dt;

	// 				transform.mX += physics.mVX * dt;
	// 				transform.mY += physics.mVY * dt;
	
	// 				if(transform.mY > 500 && physics.mVY > 0) {
		// 					physics.mVY = -physics.mVY;
		// 				}
		
// 				if(transform.mX < 0 && physics.mVX < 0) {
	// 					physics.mVX = -physics.mVX;
	// 				}
	
	// 				if(transform.mX > 500 && physics.mVX > 0) {
		// 					physics.mVX = -physics.mVX;
		// 				}
		// 			}
		// 		}
		// 	}
// }

// private void moveLeft(Vector<Entity> mEntities){
// 	for(Entity entity : mEntities) {
// 		// If a circle with a position
// 		if(entity.hasComponent(ControllableComponent.class)) {
// 			CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
// 			MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
// 			circle.xPos -= movementComponent.yVel;
// 			movementComponent.yVel = 0;
// 		}
// 	}
// }
// private void moveRight(Vector<Entity> mEntities){
// 	String direction = "Right";
// 	java.lang.System.out.println("You moved: " + direction);
// 	for(Entity entity : mEntities) {
// 		// If a circle with a position
// 		if(entity.hasComponent(CircleComponent.class)) {
// 			CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
// 			circle.xPos += SPEED;
// 		}
// 	}
// }
// private void moveDown(Vector<Entity> mEntities){
// 	String direction = "Down";
// 	java.lang.System.out.println("You moved: " + direction);
// 	for(Entity entity : mEntities) {
// 		// If a circle with a position
// 		if(entity.hasComponent(CircleComponent.class)) {
// 			CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
// 			circle.yPos += SPEED;
// 		}
// 	}
// }
// private void moveUp(Vector<Entity> mEntities){
// 	String direction = "Up";
// 	java.lang.System.out.println("You moved: " + direction);
// 	for(Entity entity : mEntities) {
// 		// If a circle with a position
// 		if(entity.hasComponent(CircleComponent.class)) {
// 			CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
// 			circle.yPos -= SPEED;
// 		}
// 	}
// }