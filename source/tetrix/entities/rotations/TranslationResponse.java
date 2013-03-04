package tetrix.entities.rotations;

import tetrix.entities.shapes.Shape;


/**
 * Representation of a set of {@link Block} translations that can be used in response to
 * a {@link Shape} rotation request.  A <code>TranslationResponse</code> is composed of
 * four pairs of column-row offsets, one pair for each of the four {@link Block}s that
 * make up a {@link Shape}.  Each column-row offset pair describes how to translate one
 * of the blocks in order to simulate a rotation.
 * 
 * @author Dustin Biser
 *
 */
public class TranslationResponse {
	private int aColOffset, aRowOffset;
	private int bColOffset, bRowOffset;
	private int cColOffset, cRowOffset;
	private int dColOffset, dRowOffset;
	
	/**
	 * Constructs a TranslationResponse.<br>
	 * Assuming k is an integer > 0, then
	 * <li>colOffset = k, represents a translation to the Right by k columns.</li>
	 * <li>colOffset = -k, represents a translation to the Left by k columns.</li>
	 * <li>rowOffset = k,  represents a translation Up by k rows.</li>
	 * <li>rowOffset = -k,  represents a translation Down by k rows.</li>
	 * <br>
	 * @param aColOffset - Number of columns to translate Block-A by.
	 * @param aRowOffset - Number of rows to translate Block-A by.
	 * @param bColOffset - Number of columns to translate Block-B by.
	 * @param bRowOffset - Number of rows to translate Block-B by.
	 * @param cColOffset - Number of columns to translate Block-C by.
	 * @param cRowOffset - Number of rows to translate Block-C by.
	 * @param dColOffset - Number of columns to translate Block-D by.
	 * @param dRowOffset - Number of rows to translate Block-D by.
	 */
	public TranslationResponse(int aColOffset, int aRowOffset,
					           int bColOffset, int bRowOffset,
					           int cColOffset, int cRowOffset,
					           int dColOffset, int dRowOffset) {
		
		this.aColOffset = aColOffset;
		this.aRowOffset = aRowOffset;
		
		this.bColOffset = bColOffset;
		this.bRowOffset = bRowOffset;
		
		this.cColOffset = cColOffset;
		this.cRowOffset = cRowOffset;
		
		this.dColOffset = dColOffset;
		this.dRowOffset = dRowOffset;
	}
	
	
	/**
	 * Translates each {@link Block} of {@link Shape} s by using the translations
	 * that define this {@link TranslationResponse}.
	 * @param s - Shape to be translated.
	 */
	public void translate(Shape s){
		s.translateA(aColOffset, aRowOffset);
		s.translateB(bColOffset, bRowOffset);
		s.translateC(cColOffset, cRowOffset);
		s.translateD(dColOffset, dRowOffset);
	}
}
