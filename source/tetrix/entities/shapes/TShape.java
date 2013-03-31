package tetrix.entities.shapes;

import tetrix.entities.shapes.ShapeType;

/**
 * Representation of a T shaped tetromino, with ShapeType T.
 * @author Dustin Biser
 *
 */

/* Block Placement for Spawn State
 * |-|B|-|
 * |C|A|D|
 * |-|-|-|
 */
public class TShape extends Shape {
	public TShape(){
		super(0,1, -1,0, 1,0);
		this.shapeType = ShapeType.T;
	}
}
