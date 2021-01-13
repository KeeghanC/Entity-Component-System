import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Abstract Component Base Class
public abstract class Component {

}
// Future components:
// 	Moveable Component
//  Controllable Component
//  Renderable Component
	// What how to render each object RenderAsCircleComponent or CircleComponent?
// 
// 

// Circle Component
class CircleComponent extends Component {
	// Data Members
	public int xPos, yPos, Radius;
	
	public CircleComponent(int xPos, int yPos, int Radius) {
		this.Radius = Radius;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	// Methods
}

// Movement Component
class MovementComponent extends Component {
	// Data Members
	public int xVel, yVel;

	public MovementComponent(int xVel, int yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

}

class ControllableComponent extends Component {
	ControllableComponent(){};
}