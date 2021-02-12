import java.awt.*;
import java.awt.image.*;
public abstract class Component {
}


class CircleComponent extends Component {
	public double mXPos, mYPos, mRadius, mMass;
	public Color mColor;
	public BufferedImage image = null;
	double mTheta = 1;

	public CircleComponent(double xPos, double yPos, double Radius, Color color, double mass) {
		this.mRadius = Radius;
		this.mXPos = xPos;
		this.mYPos = yPos;
		this.mColor = color;
		this.mMass = mass;
	}
}

class RectangleComponent extends Component {

	public RectangleComponent(Color mColor, double mTopLeftXPos, double mTopLeftYPos, double mHeight, double mWidth) {
		this.mColor = mColor;
		this.mTopLeftXPos = mTopLeftXPos;
		this.mTopLeftYPos = mTopLeftYPos;
		this.mHeight = mHeight;
		this.mWidth = mWidth;
	}

	public RectangleComponent(double mTopLeftXPos, double mTopLeftYPos, double mHeight, double mWidth) {
		this.mTopLeftXPos = mTopLeftXPos;
		this.mTopLeftYPos = mTopLeftYPos;
		this.mHeight = mHeight;
		this.mWidth = mWidth;
	}
	public Color mColor = Color.BLACK;
	public double mTopLeftXPos;
	public double mTopLeftYPos;
	public double mHeight;
	public double mWidth;

}
class MovementComponent extends Component {
	public double xVel = 0;
	public double yVel = 0;

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

class ShootComponent extends Component {
	public Boolean canScore = true;
}

class CollidableComponent extends Component {
	Boolean playSound = true;
}

class GoalComponent extends Component {
	Boolean playSound = true;
}

class BounceComponent extends Component{
	public int bounceCount = 0;
}

class FloorComponent extends Component {
}
class ArcComponent extends Component {

	public ArcComponent(double arcTopLeftX, double arcTopLeftY, double angle1, double anngle2, double angle3, double andgle4, int arcType, Color color) {
		this.arcTopLeftX = arcTopLeftX;
		this.arcTopLeftY = arcTopLeftY;
		this.angle1 = angle1;
		this.angle2 = anngle2;
		this.angle3 = angle3;
		this.angle4 = andgle4;
		this.arcType = arcType;
		this.color = color;
	}
	double arcTopLeftX;
	double arcTopLeftY;
	double angle1;
	double angle2;
	double angle3;
	double angle4;
	int arcType;
	Color color;
}