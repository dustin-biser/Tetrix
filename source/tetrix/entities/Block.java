package tetrix.entities;


/**
 * Represents the position of a block, given by row and column coordinates.
 * @author Dustin Biser
 *
 */
public class Block {
	
	private int row;
	private int column;
	
	public Block(int row, int column){
		this.row = row;
		this.column = column;
	}
	
	/**
	 * Increments Block's row and column positions by rowOffset and colOffset,
	 * respectively.
	 * @param rowOffset - number of rows to shift Block by.
	 * @param colOffset - number of columns to shift Block by.
	 */
	public void translate(int rowOffset, int colOffset){
		row = row + rowOffset;
		column = column + colOffset;
	}
	
	/**
	 * 
	 * @return Block's row position.
	 */
	public int getRow() {
		return row;
	}

	
	/**
	 * 
	 * @return Block's column position.
	 */
	public int getColumn() {
		return column;
	}

}
