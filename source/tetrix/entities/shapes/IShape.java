package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;

public class IShape extends Shape {

	public IShape() {
		super(RIGHT, RIGHT, RIGHT);
		this.shapeType = ShapeType.I;
		
		// Starting coordinates for Block-A.
		setBlockAResetColumn(3);
		setBlockAResetRow(20);
		
		// Translate Shape to spawn position.
		reset();
	}
}
