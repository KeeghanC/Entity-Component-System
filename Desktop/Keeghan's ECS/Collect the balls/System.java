import java.awt.*;
import java.util.Vector;

public abstract class System {

}

class RenderSystem extends System {
	public void render(Graphics2D g, Vector<Entity> entities) {
		g.clearRect(0, 0, 1920, 1080);

		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.elementAt(i);
			if(entity.hasComponent(CircleComponent.class)) {
				CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
				g.setColor(circle.color);
				g.fillOval(circle.xPos - circle.Radius, circle.yPos - circle.Radius, circle.Radius*2, circle.Radius*2);
				g.setColor(Color.BLACK);
				if (entity.hasComponent(HungryComponent.class)) g.drawString(String.valueOf(circle.Radius), circle.xPos-5, circle.yPos+5);
			}
		}
	}
}

class MovementSystem extends System {
	public static int SPEED = 15;
	public static int SLOWING_FACTOR = SPEED;

	public void update (Vector<Entity> mEntities){
		for(Entity entity : mEntities) {
			if (entity.hasComponent(MovementComponent.class)){
				if (entity.hasComponent(CircleComponent.class)){
					CircleComponent circle = (CircleComponent)entity.getComponent(CircleComponent.class);
					MovementComponent movementComponent = (MovementComponent)entity.getComponent(MovementComponent.class);
					
					//Move the object
					circle.xPos += movementComponent.xVel;
					circle.yPos += movementComponent.yVel;
					
					//Slow the object down if it's currently moving
					// Slow X
					if (movementComponent.xVel > 0){ 
						movementComponent.xVel -= SLOWING_FACTOR;
					} else if (movementComponent.xVel < 0) {
						movementComponent.xVel += SLOWING_FACTOR;
					}

					// Slow Y
					if (movementComponent.yVel > 0){ 
						movementComponent.yVel -= SLOWING_FACTOR;
					} else if (movementComponent.yVel < 0) {
						movementComponent.yVel += SLOWING_FACTOR;
					}
				}
			}
		}
	}
}

class ControlSystem extends System {
	
	public void update(Boolean[] mKeysPressed, Vector<Entity> mEntities, Class playerToMove) {
		for (Entity entity : mEntities){
			if (entity.hasComponent(ControllableComponent.class)){
				if (entity.hasComponent(MovementComponent.class)){
					if (entity.hasComponent(CircleComponent.class)){
						if (entity.hasComponent(playerToMove)){
							MovementComponent movementComponent = (MovementComponent) entity.getComponent(MovementComponent.class);
							CircleComponent circleComponent = (CircleComponent) entity.getComponent(CircleComponent.class);
							if (mKeysPressed[Game.UP] == true){
								if (circleComponent.yPos <= 0) continue;
								movementComponent.yVel -= MovementSystem.SPEED;
							}
							if (mKeysPressed[Game.DOWN] == true){
								if (circleComponent.yPos >= Game.WINDOW_HEIGHT) continue;
								movementComponent.yVel += MovementSystem.SPEED;
							}
							if (mKeysPressed[Game.LEFT] == true){
								if (circleComponent.xPos <= 0) continue;
								movementComponent.xVel -= MovementSystem.SPEED;
								
							}
							if (mKeysPressed[Game.RIGHT] == true){
								if (circleComponent.xPos >= Game.WINDOW_WIDTH) continue;
								movementComponent.xVel += MovementSystem.SPEED;					
							}
						}
					}
				}
			}
		}
	}
}


class CollisionSystem extends System {
	public float GROWING_FACTOR = 0.25f;


	public void print(String a) { java.lang.System.out.println(a);}
	public void update(Vector<Entity> mEntities){
		for (Entity potentialHungryCircle : mEntities){
			for (Entity potentialFood : mEntities){
				if ((potentialFood.hasComponent(FoodComponent.class)) && (potentialHungryCircle.hasComponent(HungryComponent.class))){
					boolean entitiesHaveCollided = collisionCheck(potentialHungryCircle, potentialFood);
					if (entitiesHaveCollided){
						CircleComponent HungryCircle = (CircleComponent)potentialHungryCircle.getComponent(CircleComponent.class);
						CircleComponent FoodCircle = (CircleComponent)potentialFood.getComponent(CircleComponent.class);
						HungryCircle.Radius += (int) (Math.ceil(FoodCircle.Radius * GROWING_FACTOR));
						
						if (HungryCircle.Radius < ((int) (Game.WINDOW_HEIGHT/4))) {
							int randomX = (int) (Game.WINDOW_WIDTH * Math.random());
							int randomY = (int) (Game.WINDOW_HEIGHT * Math.random());
							FoodCircle.xPos =  randomX;
							FoodCircle.yPos = randomY;
						} else mEntities.remove(potentialFood);
						return ;
					}
				}
				if (potentialHungryCircle.hasComponent(HungryComponent.class)) {
					if (potentialFood.hasComponent(HungryComponent.class)){
						if (potentialHungryCircle != potentialFood){
							boolean entitiesHaveCollided = collisionCheck(potentialHungryCircle, potentialFood);
							if (entitiesHaveCollided){
								CircleComponent entityCircle = (CircleComponent)potentialHungryCircle.getComponent(CircleComponent.class);
								CircleComponent entity2Circle = (CircleComponent)potentialFood.getComponent(CircleComponent.class);
								if (entityCircle.Radius > entity2Circle.Radius) {
									entityCircle.Radius += (int) (Math.ceil(entity2Circle.Radius));
									mEntities.remove(potentialFood);
									return;
								}
								if (entityCircle.Radius <= entity2Circle.Radius) {
									entity2Circle.Radius += (int) (Math.ceil(entityCircle.Radius));
									mEntities.remove(potentialHungryCircle);
									return;
								}
								// if (entityCircle.Radius == entity2Circle.Radius) java.lang.System.out.println("Collision occurred but objects are of the same size");
								return;
							}
						}
					}
				}
			}
		}
	}

	private boolean collisionCheck(Entity entity, Entity entity2) {
		boolean collided = false;

		CircleComponent circle1 = (CircleComponent)entity.getComponent(CircleComponent.class);
		CircleComponent circle2 = (CircleComponent)entity2.getComponent(CircleComponent.class);
		
		int dx = circle1.xPos - circle2.xPos;
		int dy = circle1.yPos - circle2.yPos;
		
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		if (distance < circle1.Radius + circle2.Radius){
			collided = true;
		}
		return collided;
	}
}