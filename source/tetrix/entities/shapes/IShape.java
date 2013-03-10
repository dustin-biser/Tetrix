package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;

public class IShape extends Shape {

	IShape() {
		super(RIGHT, RIGHT, RIGHT);
		this.shapeType = ShapeType.I;
	}
}
