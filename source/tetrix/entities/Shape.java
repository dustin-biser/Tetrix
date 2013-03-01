package tetrix.entities;

import tetrix.entities.rotations.RotationState;
import static tetrix.entities.rotations.RotationState.*;

/**
 * Representation of a Tetrix Shape. A Shape has 4 Blocks, labeled A, B, C, and
 * D, respectively.  Each Block has a column-row position such that column positions
 * increase to the right and row positions increase upwards.
 * 
 * @author Dustin Biser
 * 
 */
public class Shape {
	private Block blockA;
	private Block blockB;
	private Block blockC;
	private Block blockD;

	private RotationState rotationState;
	private Direction[] constructionDirections;

	/**
	 * Constructs a Shape using Direction offsets.  Block-A is placed
	 * at the origin (col=0, row=0).  Block placement is chained so that Block-B is
	 * placed adjacent to Block-A, Block-C is placed adjacent to Block-B, and
	 * Block-D is placed adjacent to Block-C.  Each Direction parameter can be one of the
	 * following {LEFT, RIGHT, UP, DOWN}.
	 * 
	 * @param aToB_Offset - Direction adjacent to Block-A to place Block-B
	 * @param bToC_Offset - Direction adjacent to Block-B to place Block-C
	 * @param cToD_Offset - Direction adjacent to Block-C to place Block-D
	 */
	Shape(Direction aToB_Offset, Direction bToC_Offset, Direction cToD_Offset){
		Integer rowOffset = 0;
		Integer colOffset = 0;
		
		// Place BlockA at the origin.
		blockA = new Block(colOffset, rowOffset);
		
		Block[] blocks = {blockB, blockC, blockD};
		
		constructionDirections = new Direction[3];
		constructionDirections[0] = aToB_Offset;
		constructionDirections[1] = bToC_Offset;
		constructionDirections[2] = cToD_Offset;
		
		// Chain the creation and placement of Blocks B, C, and D.
		for(int i = 0; i < blocks.length; i++){
			updateColRowOffset( constructionDirections[i], colOffset, rowOffset);
			blocks[i] = new Block(colOffset, rowOffset);
		}
		
		rotationState = SPAWN_STATE;
	}
	
	private void updateColRowOffset(Direction dir, Integer colOffset, Integer rowOffset){
		switch (dir){
		case LEFT:
			colOffset--;
		case RIGHT:
			colOffset++;
		case UP:
			rowOffset++;
		case DOWN:
			rowOffset--;
		}
	}
	
	public void resetToOriginSpawnState() {
		Integer rowOffset = 0;
		Integer colOffset = 0;
		
		blockA.setColumn(0);
		blockA.setRow(0);
		
		Block[] blocks = {blockB, blockC, blockD};
		
		// Chain the placement of blocks B, C, and D.
		for(int i = 0; i < blocks.length; i++){
			updateColRowOffset( constructionDirections[i], colOffset, rowOffset);
			blocks[i].setColumn(colOffset);
			blocks[i].setRow(rowOffset);
		}
		
		rotationState = SPAWN_STATE;
	}

	public RotationState getRotationState() {
		return rotationState;
	}

	public void setRotationState(RotationState rotationState) {
		this.rotationState = rotationState;
	}

}
