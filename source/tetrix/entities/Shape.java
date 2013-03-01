package tetrix.entities;

import tetrix.entities.rotations.RotationState;

/**
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
	
	private int[] spawnStateBlockOffsets;
}
