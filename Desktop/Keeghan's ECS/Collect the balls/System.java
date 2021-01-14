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
				// java.lang.System.out.println("Drawing a circle");
				// Get circle
				CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
				// Save current transform
				// AffineTransform current = g.getTransform();

				// Apply Transform (TODO replace with transform.getTransform())
				// g.translate(transform.mX, transform.mY);

				int diameter = circle.Radius * 2; 
				// Draw the Render component
				g.setColor(Color.RED);
				if (entity.hasComponent(EdibleComponent.class)) g.setColor(Color.green);
				g.fillOval(circle.xPos - circle.Radius, circle.yPos - circle.Radius, diameter, diameter);

			}
		}
	}
}

class MovementSystem extends System {
	public static int SPEED = 15;
	public static int SLOWING_FACTOR = SPEED;
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
	
	public void update(Boolean[] mKeysPressed, Vector<Entity> mEntities) {
		for (Entity entity : mEntities){
			if ((entity.hasComponent(ControllableComponent.class)) && (entity.hasComponent(MovementComponent.class))){
				if (mKeysPressed[Game.UP] == true){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					if (entity.hasComponent(CircleComponent.class)){
						CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
						if (circleComponent.yPos <= 0) return;
					}
					movementComponent.yVel -= MovementSystem.SPEED;
				}
				if (mKeysPressed[Game.DOWN] == true){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					if (entity.hasComponent(CircleComponent.class)){
						CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
						if (circleComponent.yPos >= Game.WINDOW_HEIGHT) return;
					}
					movementComponent.yVel += MovementSystem.SPEED;
				}
				if (mKeysPressed[Game.LEFT] == true){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					if (entity.hasComponent(CircleComponent.class)){
						CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
						if (circleComponent.xPos <= 0) return;
					}
					movementComponent.xVel -= MovementSystem.SPEED;
					
				}
				if (mKeysPressed[Game.RIGHT] == true){
					MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
					if (entity.hasComponent(CircleComponent.class)){
						CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
						if (circleComponent.xPos >= Game.WINDOW_WIDTH) return;
					}
					movementComponent.xVel += MovementSystem.SPEED;
					
				}
			}
		}
	}
}


class CollisionSystem extends System {
	public CollisionSystem(){};
	public float GROWING_FACTOR = 0.25f;
	// This function deals with all collisions based on different components
	public void update(Vector<Entity> mEntities){
		for (Entity entity : mEntities){
			for (Entity entity2 : mEntities){
				if ((entity2.hasComponent(EdibleComponent.class)) && (entity.hasComponent(HungryComponent.class))){
					boolean entitiesHaveCollided = collisionCheck(entity, entity2);
					if (entitiesHaveCollided){
						CircleComponent entityCircle = (CircleComponent)entity.getComponent(CircleComponent.class);
						CircleComponent entity2Circle = (CircleComponent)entity2.getComponent(CircleComponent.class);
						entityCircle.Radius += (int) (Math.ceil(entity2Circle.Radius * GROWING_FACTOR));
						mEntities.remove(entity2);
						return;

					}
				}
			}
		}
	}

	private boolean collisionCheck(Entity entity, Entity entity2) {
		boolean collided = false;
		CircleComponent HungryCircle = (CircleComponent)entity.getComponent(CircleComponent.class);
		CircleComponent FoodEntity = (CircleComponent)entity2.getComponent(CircleComponent.class);
		int ymax, xmax = 0;
		int ymin, xmin = 1;

		// Circle one's bounds
		//X
		int HungryCircleXBounds[] = new int[2];
		HungryCircleXBounds[xmax] = HungryCircle.xPos + HungryCircle.Radius;
		HungryCircleXBounds[xmin] = HungryCircle.xPos - HungryCircle.Radius;
		//Y
		int HungryCircleYBounds[] = new int[2];
		HungryCircleYBounds[xmax] = HungryCircle.yPos + HungryCircle.Radius;
		HungryCircleYBounds[xmin] = HungryCircle.yPos - HungryCircle.Radius;

		// Circle two's bounds
		//X
		int FoodEntityXBounds[] = new int[2];
		FoodEntityXBounds[xmax] = FoodEntity.xPos + FoodEntity.Radius;
		FoodEntityXBounds[xmin] = FoodEntity.xPos - FoodEntity.Radius;
		//Y
		int FoodEntityYBounds[] = new int[2];
		FoodEntityYBounds[xmax] = FoodEntity.yPos + FoodEntity.Radius;
		FoodEntityYBounds[xmin] = FoodEntity.yPos - FoodEntity.Radius;

		int dx = HungryCircle.xPos - FoodEntity.xPos;
		int dy = HungryCircle.yPos - FoodEntity.yPos;
		double distance = Math.sqrt(dx*dx + dy*dy);
		if (distance < HungryCircle.Radius + FoodEntity.Radius){
			java.lang.System.out.println("A collision has occured");
			collided = true;
			return collided;
		}
		// // Case 1
		// if ((HungryCircleXBounds[xmax] >= FoodEntityYBounds[xmin]) && (HungryCircleYBounds[xmin] >= FoodEntityYBounds[xmin])){
		// } 


		// Does X overlap?
		// if ((HungryCircleXBounds[max] >= FoodEntityXBounds[min]) && HungryCircleXBounds[min] <= FoodEntityXBounds[max]){
		// 	java.lang.System.out.println("X overlaps");
		// 	// Does Y overlap?
		// 	if (FoodEntityYBounds[min] >= HungryCircleYBounds[max]){
		// 		collided = true;
		// 		java.lang.System.out.println("A collision has occured");
		// 	}
		// }
		return collided;
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