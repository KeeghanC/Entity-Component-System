import java.awt.*;

public abstract class Component {
}


class CircleComponent extends Component {
	public double mXPos, mYPos, mRadius, mMass;
	public Color mColor;
	public Image image = null;

	public CircleComponent(float xPos, float yPos, float Radius, Color color, float mass) {
		this.mRadius = Radius;
		this.mXPos = xPos;
		this.mYPos = yPos;
		this.mColor = color;
		this.mMass = mass;
	}
}

class MovementComponent extends Component {
	public double xVel, yVel;

	public MovementComponent(double xVel, double yVel) {
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

class GravityComponent extends Component {
}