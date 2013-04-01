package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;
import tetrix.entities.shapes.ShapeType;

/**
 * Representation of a Z shaped tetromino, with ShapeType Z.
 * @author Dustin Biser
 *
 */
public class ZShape extends Shape {
	public ZShape(){
		super(RIGHT, DOWN, RIGHT);
		this.shapeType = ShapeType.Z;
		
		// Starting coordinates for Block-A.
		setResetColumn(3);
		setResetRow(21);
		
		reset();
	}
}
