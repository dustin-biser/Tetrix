package tetrix.entities;

import java.security.InvalidParameterException;

import tetrix.annotations.Immutable;

/**
 * Encapsulates the configuration of blocks that compose a given shape. A shape
 * is composed of 4 blocks total, where each block has an associated coordinate.
 * The set of all possible block coordinates compose what we call <i>Shape
 * Configuration Space</i>.  The following lists the possible block coordinates,
 * and their locations within this configuration space.<br>
 * <pre>
 * 0,0  0,1  0,2  0,3
 * 1,0  1,1  1,2  1,3
 * 2,0  2,1  2,2  2,3
 * 3,0  3,1  3,2  3,3
 * </pre>
 */

@Immutable
public class ShapeConfiguration {
	
	// Coordinates donating placement of blocks within Shape Configuration Space.
	private final int[] blockCoordinates;
	
	private final int NUM_COORDINATE_ENTRIES = 8;

	/**
	 * Each parameter represents either a row or column coordinate for the location
	 * of a block within <i>Shape Configuration Space</i>.  Each parameter must be
	 * in the range [0, 3].  
	 * @param block1_row
	 * @param block1_col
	 * @param block2_row
	 * @param block2_col
	 * @param block3_row
	 * @param block3_col
	 * @param block4_row
	 * @param block4_col
	 */
	public ShapeConfiguration(int block1_row, int block1_col,
			                  int block2_row, int block2_col,
			                  int block3_row, int block3_col,
			                  int block4_row, int block4_col){
		
		int[] inputParams = { block1_row,  block1_col,
				              block2_row,  block2_col,
				              block3_row,  block3_col,
				              block4_row,  block4_col };
		
		blockCoordinates = new int[NUM_COORDINATE_ENTRIES];
		int index = 0;
		
		// Populate blockCoordinates with input parameters.
		for(int coord : inputParams){
			// Check validity of input parameters.
			if ((coord < 0) || (coord > 3)) {
				throw new InvalidParameterException("Invalid Parameter. " +
						"Block coordinate values must be in the range [0, 3].)");
			}
			
			blockCoordinates[index++] = coord;
		}
		
	}
	
	public int[] getBlockCoordinates(){
		return blockCoordinates.clone();
	}
}
