package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;
import tetrix.entities.shapes.ShapeType;

/**
 * Representation of an O shaped tetromino, with ShapeType O.
 * @author Dustin Biser
 *
 */
public class OShape extends Shape {
	public OShape(){
		super(RIGHT,UP,LEFT);
		this.shapeType = ShapeType.O;
		
		// Starting coordinates for Block-A.
		setResetColumn(4);
		setResetRow(20);
		
		reset();
	}
}
