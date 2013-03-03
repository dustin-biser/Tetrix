package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tetrix.entities.shapes.JShape;
import tetrix.entities.shapes.Shape;
import tetrix.entities.shapes.ShapeType;

import static tetrix.entities.rotations.RotationState.*;
import static tetrix.entities.Direction.*;

public class JShape_Test {
	private Shape shape;
	
	@Before
	public void setUp() throws Exception {
		shape = new JShape();
	}

	@Test
	public void test_initial_RotationState() {
		assertEquals(shape.getRotationState(), SPAWN_STATE);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test getBlockPositions()
	/////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void test_getBlockPositions(){
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test translate()
	/////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void test_translate_0_0(){
		shape.translate(0,0);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_translate_1_1(){
		shape.translate(1,1);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {1,1, 1,0, 2,0, 3,0};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_translate_neg1_neg2(){
		shape.translate(-1,-2);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {-1,-2, -1,-3, 0,-3, 1,-3};
		
		assertArrayEquals(expected, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test reset()
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_reset_no_translations(){
		shape.reset();
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset1(){
		shape.translate(1,1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset2(){
		shape.translate(5,-4);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset3(){
		shape.translate(-2,10);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset4(){
		shape.translate(0, 1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	// Test moveLeft, moveRight, moveDown
	/////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	public void test_moveLeft(){
		shape.moveLeft();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {-1,0, -1,-1, 0,-1, 1,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_moveLeft_2(){
		shape.moveLeft();
		shape.moveLeft();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {-2,0, -2,-1, -1,-1, 0,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_moveRight(){
		shape.moveRight();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {1,0, 1,-1, 2,-1, 3,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_moveRight_2(){
		shape.moveRight();
		shape.moveRight();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {2,0, 2,-1, 3,-1, 4,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_moveDown(){
		shape.moveDown();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,-1, 0,-2, 1,-2, 2,-2};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_moveDown_2(){
		shape.moveDown();
		shape.moveDown();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,-2, 0,-3, 1,-3, 2,-3};
		
		assertArrayEquals(expected, positions);
	}
}
