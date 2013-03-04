package tetrix.entities.shapes;

import static tetrix.entities.Direction.*;
import static tetrix.entities.shapes.ShapeType.*;


/**
 * Representation of a J shaped tetromino, with ShapeType J.
 * @author Dustin Biser
 *
 */
public class JShape extends Shape {
	public JShape(){
		super(DOWN, RIGHT, RIGHT);
		shapeType = J;
	}
}