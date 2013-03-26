package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;
import tetrix.entities.shapes.ShapeType;

/**
 * Representation of an L shaped tetromino, with ShapeType J.
 * @author Dustin Biser
 *
 */
public class LShape extends Shape {
	public LShape(){
		super(RIGHT, RIGHT, UP);
		this.shapeType = ShapeType.L;
	}
}
