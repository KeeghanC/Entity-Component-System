import java.awt.*;

public abstract class Component {
}


class CircleComponent extends Component {
	public int xPos, yPos, Radius;
	public Color color;

	public CircleComponent(int xPos, int yPos, int Radius, Color color) {
		this.Radius = Radius;
		this.xPos = xPos;
		this.yPos = yPos;
		this.color = color;
	}
}

class MovementComponent extends Component {
	public int xVel, yVel;

	public MovementComponent(int xVel, int yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}
}

class ControllableComponent extends Component {
}

class WASDControllsComponent extends Component {
}

class ArrowControllsComponent extends Component {
}

class FoodComponent extends Component {
}

class HungryComponent extends Component {
}
