package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;

public class IShape extends Shape {

	public IShape() {
		super(RIGHT, RIGHT, RIGHT);
		this.shapeType = ShapeType.I;
		
		// Starting coordinates for Block-A.
		setResetColumn(3);
		setResetRow(20);
		
		reset();
	}
}
