package tetrix.entities.rotations;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tetrix.entities.shapes.JShape;
import tetrix.entities.shapes.Shape;

public class TranslationResponse_Test {
	private TranslationResponse translationResponse;
	private Shape shape;
	private int[] expectedPositions;
	
	@Before
	public void setUp() throws Exception {
		shape = new JShape();
		expectedPositions = shape.getBlockPositions();
	}

	@Test
	public void test_zero_translation() {
		translationResponse = new TranslationResponse().translateA(0,0)
												       .translateB(0,0)
													   .translateC(0,0)
													   .translateD(0,0);
		translationResponse.translate(shape);
		
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
	
	@Test
	public void test_translate_all_right_1_col() {
		translationResponse = new TranslationResponse().translateA(1,0)
													   .translateB(1,0)
													   .translateC(1,0)
													   .translateD(1,0);
		translationResponse.translate(shape);
		
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i] += 1;
		}
		
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
	
	@Test
	public void test_translate_all_up_1_row() {
		new TranslationResponse().translateA(0,1)
								 .translateB(0,1)
								 .translateC(0,1)
								 .translateD(0,1)
								 .translate(shape);
		
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i + 1] += 1;
		}
		
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
	
	@Test
	public void test_translate_all_unique(){
		new TranslationResponse().translateA(-1, 1)
								 .translateB(2, -3)
								 .translateC(0, 14)
								 .translateD(22,-23)
								 .translate(shape);
		
		expectedPositions[0] += -1;
		expectedPositions[1] += 1;
		expectedPositions[2] += 2;
		expectedPositions[3] += -3;
		expectedPositions[4] += 0;
		expectedPositions[5] += 14;
		expectedPositions[6] += 22;
		expectedPositions[7] += -23;
		
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
	
	@Test
	public void test_inverse_with_all_unique(){
		translationResponse = new TranslationResponse().translateA(1, 1)
													   .translateB(2, 2)
													   .translateC(3, 3)
													   .translateD(4, 4);
		
		TranslationResponse inverse = translationResponse.getInverse();
		
		int[] startingPositions = shape.getBlockPositions();
		
		translationResponse.translate(shape);
		inverse.translate(shape); //Translate Shape back to starting position.
		
		assertArrayEquals(expectedPositions, startingPositions);
	}
	
	@Test
	public void test_inverse_of_zero_translations(){
		translationResponse = new TranslationResponse();
		
		TranslationResponse inverse = translationResponse.getInverse();
		
		int[] startingPositions = shape.getBlockPositions();
		
		translationResponse.translate(shape);
		inverse.translate(shape); //Translate Shape back to starting position.
		
		assertArrayEquals(expectedPositions, startingPositions);
	}
	
	@Test
	public void test_inverse_unique_with_negatives(){
		translationResponse = new TranslationResponse().translateA(-1, 1)
													   .translateB(2, -2)
													   .translateC(3, 3)
													   .translateD(-4, -4);
		
		TranslationResponse inverse = translationResponse.getInverse();
		inverse.translate(shape);
		
		expectedPositions[0] -= -1;
		expectedPositions[1] -=  1;
		expectedPositions[2] -=  2;
		expectedPositions[3] -= -2;
		expectedPositions[4] -=  3;
		expectedPositions[5] -=  3;
		expectedPositions[6] -= -4;
		expectedPositions[7] -= -4;
		
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}

}
