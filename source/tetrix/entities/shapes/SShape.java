package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;
import tetrix.entities.shapes.ShapeType;

/**
 * Representation of an S shaped tetromino, with ShapeType S.
 * @author Dustin Biser
 *
 */
public class SShape extends Shape {
	public SShape(){
		super(RIGHT, UP, RIGHT);
		this.shapeType = ShapeType.S;
		
		// Starting coordinates for Block-A.
		setResetColumn(3);
		setResetRow(20);
		
		reset();
	}
}
