package tetrix.entities;

import tetrix.entities.rotations.RotationState;

/**
 * Representation of a Tetrix Shape.  A Shape has 4 Blocks, labeled A, B, C, and
 * D, respectively.
 * @author Dustin Biser 
 *
 */
public class Shape {
	private Block blockA;
	private Block blockB;
	private Block blockC;
	private Block blockD;
	
	private RotationState rotationState;
	
	private int[] spawnStateBlockOffsets;
	
	/**
	 * Constructs a Shape object using column/row offset parameters.  Each
	 * parameter is given in reference to Block-A's column-row position, which can
	 * be thought of as residing at the origin (column=0, row=0).  Subsequent
	 * Blocks B, C, and D are then placed with respect to Block-A using the
	 * following convention:<br>
	 * <br>
	 * Assume k is an integer > 0.
	 * <li><code>columnOffset = k</code>, implies the block will be placed to the
	 * right of Block-A by k columns.</li>
	 * <li><code>columnOffset = -k</code>, implies the block will be placed to the
	 * left of Block-A by k columns.</li>
	 * <li><code>rowOffSet = k</code>, implies the block will be placed above
	 * Block-A by k rows. </li>
	 * <li><code>rowOffSet = -k</code>, implies the block will be placed below
	 * Block-A by k rows. </li>
	 *   
	 * @param bColOffset - column offset from Block-A to Block-B.
	 * @param bRowOffset - row offset from Block-A to Block-B.
	 * @param cColOffset - column offset from Block-A to Block-C.
	 * @param cRowOffset - row offset from Block-A to Block-C.
	 * @param dColOffset - column offset from Block-A to Block-D.
	 * @param dRowOffset - row offset from Block-A to Block-D.
	 */
	Shape(int bColOffset, int bRowOffset,
		  int cColOffset, int cRowOffset,
		  int dColOffset, int dRowOffset){
		
		blockA = new Block(0, 0);
		blockB = new Block(bColOffset, bRowOffset);
		blockC = new Block(cColOffset, cRowOffset);
		blockD = new Block(dColOffset, dRowOffset);
		
		spawnStateBlockOffsets = new int[8];
		spawnStateBlockOffsets[0] = 0;
		spawnStateBlockOffsets[1] = 0;
		spawnStateBlockOffsets[2] = bColOffset;
		spawnStateBlockOffsets[3] = bRowOffset;
		spawnStateBlockOffsets[4] = cColOffset;
		spawnStateBlockOffsets[5] = cRowOffset;
		spawnStateBlockOffsets[6] = dColOffset;
		spawnStateBlockOffsets[7] = dRowOffset;
		
		rotationState = RotationState.SpawnState;
	}
	
	public void resetToSpawnState(){
		blockA.setColumn(spawnStateBlockOffsets[0]);
		blockA.setRow(spawnStateBlockOffsets[1]);
		
		blockB.setColumn(spawnStateBlockOffsets[2]);
		blockB.setRow(spawnStateBlockOffsets[3]);
		
		blockC.setColumn(spawnStateBlockOffsets[4]);
		blockC.setRow(spawnStateBlockOffsets[5]);
		
		blockD.setColumn(spawnStateBlockOffsets[6]);
		blockD.setRow(spawnStateBlockOffsets[7]);
	}
	
	public RotationState getRotationState(){
		return rotationState;
	}
	
	public void setRotationState ( RotationState rotationState ){
		this.rotationState = rotationState;
	}
	
}
