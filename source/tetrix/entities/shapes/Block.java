package tetrix.entities.shapes;


/**
 * Representation of a block, which contains column, and row coordinates.
 * @author Dustin Biser
 *
 */
public class Block {
	
	private int row;
	private int column;
	
	public Block(int column, int row){
		this.column = column;
		this.row = row;
	}
	
	/**
	 * Increments Block's column and row positions by colOffset and rowOffset,
	 * respectively, which can be positive, negative, or zero.
	 * @param colOffset - number of columns to shift Block by.
	 * @param rowOffset - number of rows to shift Block by.
	 */
	public void translate(int colOffset, int rowOffset){
		column = column + colOffset;
		row = row + rowOffset;
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
	
	/**
	 * 
	 * @param row - row position for Block.
	 */
	public void setRow(int row){
		this.row = row;
	}

	/**
	 * 
	 * @param column - column position for Block.
	 */
	public void setColumn(int column){
		this.column = column;
	}
}
