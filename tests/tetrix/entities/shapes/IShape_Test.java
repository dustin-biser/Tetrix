package tetrix.entities.shapes;

import static org.junit.Assert.*;
import static tetrix.entities.rotations.RotationState.LEFT_OF_SPAWN;
import static tetrix.entities.rotations.RotationState.RIGHT_OF_SPAWN;
import static tetrix.entities.rotations.RotationState.SECOND_ROTATION;
import static tetrix.entities.rotations.RotationState.SPAWN_STATE;

import org.junit.Before;
import org.junit.Test;

public class IShape_Test {
	private Shape shape;
	private int[] expectedPositions;
	
	@Before
	public void setUp() throws Exception {
		shape = new IShape();
		expectedPositions = shape.getBlockPositions();
	}

	@Test
	public void test_initial_RotationState() {
		assertEquals(shape.getRotationState(), SPAWN_STATE);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test getShapeType()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_getShapeType(){
		assertEquals(ShapeType.I, shape.getShapeType());
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test getRotationState()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_getRotationState(){
		assertEquals(SPAWN_STATE, shape.getRotationState());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test setRotationState()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_setRotationState_1(){
		shape.setRotationState(RIGHT_OF_SPAWN);
		
		assertEquals(RIGHT_OF_SPAWN, shape.getRotationState());
	}
	
	@Test
	public void test_setRotationState_2(){
		shape.setRotationState(SECOND_ROTATION);
		
		assertEquals(SECOND_ROTATION, shape.getRotationState());
	}
	
	@Test
	public void test_setRotationState_3(){
		shape.setRotationState(LEFT_OF_SPAWN);
		
		assertEquals(LEFT_OF_SPAWN, shape.getRotationState());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test getBlockPositions()
	/////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void test_getBlockPositions(){
		int[] expectedPositions = {3,20, 4,20, 5,20, 6,20};
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translate()
	/////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void test_translate_0_0(){
		shape.translate(0,0);
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translate_1_1(){
		shape.translate(1,1);
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length; i++){
			expectedPositions[i] += 1;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translate_neg1_neg2(){
		shape.translate(-1,-2);
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i]     += -1;
			expectedPositions[2*i + 1] += -2;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translate_neg2_2(){
		shape.translate(-2, 2);
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i]     += -2;
			expectedPositions[2*i + 1] += 2;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translateA()
	/////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void test_translateA_0_0(){
		shape.translateA(0,0);
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translateA_1_neg1(){
		shape.translateA(1,-1);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[0] += 1;
		expectedPositions[1] += -1;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translateB()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_translateB_1_0(){
		shape.translateB(1,0);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[2] += 1;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translateB_neg1_neg1(){
		shape.translateB(-1,-1);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[2] += -1;
		expectedPositions[3] += -1;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translateC()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_translateC_neg2_neg4(){
		shape.translateC(-2,-4);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[4] += -2;
		expectedPositions[5] += -4;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translateC_2_4(){
		shape.translateC(2,4);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[4] += 2;
		expectedPositions[5] += 4;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translateD()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_translateD_0_neg5(){
		shape.translateD(0,-5);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[6] += 0;
		expectedPositions[7] += -5;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_translateD_2_2(){
		shape.translateD(2,2);
		
		int[] positions = shape.getBlockPositions();
		expectedPositions[6] += 2;
		expectedPositions[7] += 2;
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test reset()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_reset_no_translations(){
		shape.reset();
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_reset1(){
		shape.translate(1,1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_reset2(){
		shape.translate(5,-4);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_reset3(){
		shape.translate(-2,10);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_reset4(){
		shape.translate(0, 1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_reset5(){
		shape.moveLeft();
		shape.moveDown();
		shape.moveLeft();
		shape.moveRight();
		shape.translate(6, 5);
		
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test moveLeft, moveRight, moveDown
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_moveLeft(){
		shape.moveLeft();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i] += -1;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveLeft_2(){
		shape.moveLeft();
		shape.moveLeft();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i] += -2;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveRight(){
		shape.moveRight();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i] += 1;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveRight_2(){
		shape.moveRight();
		shape.moveRight();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i] += 2;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveDown(){
		shape.moveDown();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i + 1] += -1;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveDown_2(){
		shape.moveDown();
		shape.moveDown();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i + 1] += -2;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveLeft_moveRight(){
		shape.moveLeft();
		shape.moveRight();
		
		int[] positions = shape.getBlockPositions();
		
		assertArrayEquals(expectedPositions, positions);
	}
	
	@Test
	public void test_moveRight_moveDown_moveLeft(){
		shape.moveRight();
		shape.moveDown();
		shape.moveLeft();
		
		int[] positions = shape.getBlockPositions();
		for(int i = 0; i < expectedPositions.length / 2; i++){
			expectedPositions[2*i + 1] += -1;
		}
		
		assertArrayEquals(expectedPositions, positions);
	}
}
