package tetrix.entities.rotations;

import tetrix.entities.shapes.Shape;

public class TranslationResponse {
	private int[] blockTranslations;
	
	TranslationResponse(int[] blockTranslations){
		this.blockTranslations = blockTranslations;
	}
	
	void translate(Shape s){
	}
}
