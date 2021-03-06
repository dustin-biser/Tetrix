package tetrix.entities.shapes;

import tetrix.entities.Direction;
import tetrix.entities.rotations.RotationState;
import static tetrix.entities.rotations.RotationState.*;

/**
 * Representation of a Tetrix Shape. A Shape has 4 {@link Block} instances,
 * labeled A, B, C, and D, respectively.  Each Block has a column-row position
 * such that column positions increase to the right and row positions increase
 * upwards.  When a Shape object is first created, it's {@link RotationState}
 * defaults to <code>RotationState.SPAWN_STATE</code>.
 * 
 * @author Dustin Biser
 * 
 */
public abstract class Shape {
	// Blocks A,B,C,D, corresponding to indices 0,1,2,3 respectively.
	private Block[] blocks;
	
	private RotationState rotationState;
	
	// Shapes can be constructed using either Directions or coordinate offsets.
	private Direction[] constructionDirections;
	private int[] constructionCoordinates;
	
	// Column-Row coordinates for Block-A when reset() is called.
	private int resetAColumn;
	private int resetARow;
	
	protected ShapeType shapeType;

	/**
	 * Constructs a Shape with RotationState = SPAWN_STATE using Direction offsets.
	 * Block-A is placed at the origin (col=0, row=0).  Block placement is chained
	 * so that Block-B is placed adjacent to Block-A, Block-C is placed adjacent to
	 * Block-B, and Block-D is placed adjacent to Block-C.  Each Direction
	 * parameter can be one of the following {LEFT, RIGHT, UP, DOWN}.
	 * 
	 * @param aToB - Direction adjacent to Block-A to place Block-B
	 * @param bToC - Direction adjacent to Block-B to place Block-C
	 * @param cToD - Direction adjacent to Block-C to place Block-D
	 */
	Shape(Direction aToB, Direction bToC, Direction cToD) {
		
		int rowOffset = 0;
		int colOffset = 0;
		
		blocks = new Block[4];
		
		// Place Block-A at the origin.
		blocks[0] = new Block(colOffset, rowOffset);
		
		constructionDirections = new Direction[3];
		constructionDirections[0] = aToB;
		constructionDirections[1] = bToC;
		constructionDirections[2] = cToD;
		
		Direction direction;
		
		// Chain the creation and placement of Blocks B, C, and D.
		for(int i = 0; i < 3; i++){
			direction = constructionDirections[i];
			colOffset += directionToColOffset(direction);
			rowOffset += directionToRowOffset(direction);
			
			blocks[i+1] = new Block(colOffset, rowOffset);
		}
		
		rotationState = SPAWN_STATE;
	}
	
	/**
	 * Constructs a Shape using coordinate offsets from Block-A.  Shape starts with
	 * Block-A at the origin (col=0, row=0), with Blocks B,C,D placed at col/row
	 * offsets from Block-A.  Shape's RotationState is set to SPAWNED_STATE by default.
	 * 
	 * <p>
	 * Column and Row offsets use the following convention.  Given an integer k > 0,
	 * <li>ColOffset = k, is an offset to the right by k columns.</li>
	 * <li>ColOffset = -k, is an offset to the left by k columns.</li>
	 * <li>RowOffset = k, is an offset upwards by k rows.</li>
	 * <li>RowOffset = -k, is an offset downwards by k rows.</li>
	 * 
	 * @param bColOffset - column offset from Block-A to place Block-B.
	 * @param bRowOffSet - row offset from Block-A to place Block-B. 
	 * @param cColOffset - column offset from Block-A to place Block-C.
	 * @param cRowOffSet - row offset from Block-A to place Block-C. 
	 * @param dColOffset - column offset from Block-A to place Block-D.
	 * @param dRowOffSet - row offset from Block-A to place Block-D. 
	 */
	Shape(int bColOffset, int bRowOffset,
		  int cColOffset, int cRowOffset,
		  int dColOffset, int dRowOffset){
		
		constructionCoordinates = new int [6];
		
		//-- Save coordinate offsets for later use.
		constructionCoordinates[0] = bColOffset;
		constructionCoordinates[1] = bRowOffset;
		constructionCoordinates[2] = cColOffset;
		constructionCoordinates[3] = cRowOffset;
		constructionCoordinates[4] = dColOffset;
		constructionCoordinates[5] = dRowOffset;
		
		blocks = new Block[4];
		
		// Block A
		blocks[0] = new Block(0,0);
		
		// Block B
		blocks[1] = new Block(bColOffset, bRowOffset);
		
		// Block C
		blocks[2] = new Block(cColOffset, cRowOffset);
		
		// Block D
		blocks[3] = new Block(dColOffset, dRowOffset);
		
		rotationState = SPAWN_STATE;
	}
	
	private int directionToColOffset(Direction direction){
		switch (direction){
		case LEFT:
			return -1;
		case RIGHT:
			return 1;
		default:
			return 0;
		}
	}
	
	private int directionToRowOffset(Direction direction){
		switch (direction){
		case DOWN:
			return -1;
		case UP:
			return 1;
		default:
			return 0;
		}
	}
	
	/**
	 * Resets Shape so that Block-A is translated to position (resetColumn,
	 * resetRow) (which, if not manually set defaults to 0,0) and Blocks B, C, and D
	 * are placed adjacent to Block-A in the same fashion as when the Shape was
	 * first created.  RotationState for this object is then set to SPAWN_STATE.
	 */
	public void reset() {
		int colOffset = resetAColumn;
		int rowOffset = resetARow;
		
		// Reset Block-A's position.
		blocks[0].setColumn(resetAColumn);
		blocks[0].setRow(resetARow);
		
		// Use construction directions if available.
		if (constructionDirections != null) {
			Direction direction;
			
			// Chain the placement of blocks B, C, and D.
			for(int i = 1; i < blocks.length; i++){
				direction = constructionDirections[i-1];
				colOffset += directionToColOffset(direction);
				rowOffset += directionToRowOffset(direction);
				
				blocks[i].setColumn(colOffset);
				blocks[i].setRow(rowOffset);
			}
		}
		else { // Use construction coordinates.
			int column, row;
			
			// Reset Block-B's position.
			column = constructionCoordinates[0] + resetAColumn;
			row = constructionCoordinates[1] + resetARow;
			blocks[1].setColumn(column);
			blocks[1].setRow(row);
			
			// Reset Block-C's position.
			column = constructionCoordinates[2] + resetAColumn;
			row = constructionCoordinates[3] + resetARow;
			blocks[2].setColumn(column);
			blocks[2].setRow(row);
			
			// Reset Block-D's position.
			column = constructionCoordinates[4] + resetAColumn;
			row = constructionCoordinates[5] + resetARow;
			blocks[3].setColumn(column);
			blocks[3].setRow(row);
		}
		
		rotationState = SPAWN_STATE;
	}
	
	/**
	 * Sets the reset column for this Shape.  This is the column that
	 * Block-A will be located when this Shape's reset() method is called.
	 * @param resetColumn
	 */
	protected void setBlockAResetColumn(int resetColumn){
		this.resetAColumn = resetColumn;
	}
	
	/**
	 * Sets the reset row for this Shape.  This is the row that
	 * Block-A will be located when this Shape's reset() method is called.
	 * @param resetRow
	 */
	protected void setBlockAResetRow(int resetRow){
		this.resetARow = resetRow;
	}

	/**
	 * Gets the RotationState of this Shape.
	 * @return RotationState
	 */
	public RotationState getRotationState() {
		return rotationState;
	}
	
	/**
	 * Sets the RotationState for the Shape. 
	 * @param rotationState
	 */
	public void setRotationState(RotationState rotationState){
		this.rotationState = rotationState;
	}
	
	/**
	 * Gets the ShapeType of this Shape. 
	 * @return ShapeType
	 */
	public ShapeType getShapeType(){
		return shapeType;
	}
	
	/**
	 * Returns a new integer array with 8 elements representing the current
	 * column-row positions of each of the 4 Blocks that compose this Shape.  The
	 * items in the returned array are mapped to Block coordinates as follows:<br>
	 * <pre>
	 * int[0] = block-A column
	 * int[1] = block-A row
	 * int[2] = block-B column
	 * int[3] = block-B row
	 * int[4] = block-C column
	 * int[5] = block-C row
	 * int[6] = block-D column
	 * int[7] = block-D row
	 * </pre>
	 * @return integer array containing column-row positions for each Block.
	 */
	public int[] getBlockPositions(){
		int[] result = new int[8];
		
		for(int i = 0; i < 4; i++){
			result[2*i] = blocks[i].getColumn();
			result[2*i + 1] = blocks[i].getRow();
		}
		
		return result;
	}
	
	public void moveLeft(){
		for(Block block : blocks){
			block.translate(-1, 0);
		}
	}
	
	public void moveRight(){
		for(Block block : blocks){
			block.translate(1, 0);
		}
	}
	
	public void moveDown(){
		for(Block block : blocks){
			block.translate(0, -1);
		}
	}
	
	/**
	 * Translate all Blocks by colOffset number of columns and rowOffset number of
	 * rows.  Assuming k is an integer > 0, then
	 * <li>colOffset = k, moves all Blocks to the Right by k columns.</li>
	 * <li>colOffset = -k, moves all Blocks to the Left by k columns.</li>
	 * <li>rowOffset = k, moves all Blocks Up by k rows.</li>
	 * <li>rowOffset = -k, moves all Blocks Down by k rows.</li>
	 * @param colOffset - number of columns to translate Shape by.
	 * @param rowOffset - number of rows to translate Shape by.
	 */
	public void translate(int colOffset, int rowOffset){
		for(Block block : blocks){
			block.translate(colOffset, rowOffset);
		}
	}
	
	public void translateA(int colOffset, int rowOffset){
		blocks[0].translate(colOffset, rowOffset);
	}
	
	public void translateB(int colOffset, int rowOffset){
		blocks[1].translate(colOffset, rowOffset);
	}
	
	public void translateC(int colOffset, int rowOffset){
		blocks[2].translate(colOffset, rowOffset);
	}
	
	public void translateD(int colOffset, int rowOffset){
		blocks[3].translate(colOffset, rowOffset);
	}
}
