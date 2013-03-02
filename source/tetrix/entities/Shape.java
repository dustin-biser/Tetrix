package tetrix.entities;

import tetrix.entities.rotations.RotationState;
import static tetrix.entities.rotations.RotationState.*;

/**
 * Representation of a Tetrix Shape. A Shape has 4 Blocks, labeled A, B, C, and
 * D, respectively.  Each Block has a column-row position such that column
 * positions increase to the right and row positions increase upwards.
 * 
 * @author Dustin Biser
 * 
 */
public class Shape {
	// Blocks A,B,C,D, corresponding to indices 0,1,2,3 respectively.
	private Block[] blocks;

	private RotationState rotationState;
	private Direction[] constructionDirections;

	/**
	 * Constructs a Shape with RotationState = SPAWN_STATE using Direction offsets.
	 * Block-A is placed at the origin (col=0, row=0).  Block placement is chained
	 * so that Block-B is placed adjacent to Block-A, Block-C is placed adjacent to
	 * Block-B, and Block-D is placed adjacent to Block-C.  Each Direction
	 * parameter can be one of the following {LEFT, RIGHT, UP, DOWN}.
	 * 
	 * @param aToB_Offset - Direction adjacent to Block-A to place Block-B
	 * @param bToC_Offset - Direction adjacent to Block-B to place Block-C
	 * @param cToD_Offset - Direction adjacent to Block-C to place Block-D
	 */
	Shape(Direction aToB_Offset, Direction bToC_Offset, Direction cToD_Offset){
		int rowOffset = 0;
		int colOffset = 0;
		
		blocks = new Block[4];
		
		// Place Block-A at the origin.
		blocks[0] = new Block(colOffset, rowOffset);
		
		constructionDirections = new Direction[3];
		constructionDirections[0] = aToB_Offset;
		constructionDirections[1] = bToC_Offset;
		constructionDirections[2] = cToD_Offset;
		
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
	 * Resets Shape so that Block-A is translated to the origin (column=0, row=0),
	 * and Blocks B, C, and D are placed adjacent to Block-A in the same fashion as
	 * when the Shape was first created.  RotationState for this object is then set
	 * to SPAWN_STATE.
	 */
	public void reset() {
		int rowOffset = 0;
		int colOffset = 0;
		
		blocks[0].setColumn(0);
		blocks[0].setRow(0);
		
		Direction direction;
		
		// Chain the placement of blocks B, C, and D.
		for(int i = 1; i < blocks.length; i++){
			direction = constructionDirections[i-1];
			colOffset += directionToColOffset(direction);
			rowOffset += directionToRowOffset(direction);
			
			blocks[i].setColumn(colOffset);
			blocks[i].setRow(rowOffset);
		}
		
		rotationState = SPAWN_STATE;
	}

	/**
	 * Gets the RotationState of this Shape.
	 * @return RotationState
	 */
	public RotationState getRotationState() {
		return rotationState;
	}

	/**
	 * Sets the RotationState for this Shape.
	 * @param rotationState - RotationState
	 */
	public void setRotationState(RotationState rotationState) {
		this.rotationState = rotationState;
	}
	
	/**
	 * Returns an integer array with 8 elements representing the current column-row
	 * positions of each of the 4 Blocks that compose this Shape.  The items in the returned array
	 * are mapped to Block coordinates as follows:<br>
	 * <pre>
	 * int[0] = block-A-col
	 * int[1] = block-A-row
	 * int[2] = block-B-col
	 * int[3] = block-B-row
	 * int[4] = block-C-col
	 * int[5] = block-C-row
	 * int[6] = block-D-col
	 * int[7] = block-D-row
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
	
	/**
	 * Translate all Blocks by colOffset number of columns and rowOffset number of
	 * rows.  Assuming k is an integer > 0, then
	 * <li>colOffset = k, moves all Blocks to the Right by k columns.</li>
	 * <li>colOffset = -k, moves all Blocks to the Left by k columns.</li>
	 * <li>rowOffset = k, moves all Blocks Up by k rows.</li>
	 * <li>rowOffset = -k, moves all Blocks Down by k rows.</li>
	 * @param colOffset - number of columns to shift Shape.
	 * @param rowOffset - number or rows to shift Shape.
	 */
	public void translate(int colOffset, int rowOffset){
		for(Block block : blocks){
			block.translate(colOffset, rowOffset);
		}
	}
}
