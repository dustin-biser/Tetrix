package tetrix.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static tetrix.entities.rotations.RotationState.*;
import static tetrix.entities.Direction.*;

public class Shape_Test {
	private Shape shape;
	
	@Before
	public void setUp() throws Exception {
		shape = new Shape(RIGHT, UP, RIGHT);
	}

	@Test
	public void test_initial_RotationState() {
		assertEquals(shape.getRotationState(), SPAWN_STATE);
	}
	
	@Test
	public void test_set_RotationState() {
		shape.setRotationState(SECOND_ROTATION);
		assertEquals(shape.getRotationState(), SECOND_ROTATION);
	}
	
	@Test
	public void test_getBlockPositions_R_U_R(){
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 1,1, 2,1};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	@Test
	public void test_getBlockPositions_R_R_R(){
		shape = new Shape(RIGHT, RIGHT, RIGHT);
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 2,0, 3,0};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	@Test
	public void test_getBlockPositions_L_L_L(){
		shape = new Shape(LEFT, LEFT, LEFT);
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, -1,0, -2,0, -3,0};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	@Test
	public void test_getBlockPositions_L_D_L(){
		shape = new Shape(LEFT, DOWN, LEFT);
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, -1,0, -1,-1, -2,-1};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	@Test
	public void test_getBlockPositions_D_R_R(){
		shape = new Shape(DOWN, RIGHT, RIGHT);
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 0,-1, 1,-1, 2,-1};
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(expected[i], positions[i]);
		}
	}
	
	@Test
	public void test_translate_0_0(){
		shape = new Shape(RIGHT, DOWN, LEFT);
		shape.translate(0,0);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 1,-1, 0,-1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_translate_1_1(){
		shape = new Shape(RIGHT, DOWN, LEFT);
		shape.translate(1,1);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {1,1, 2,1, 2,0, 1,0};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_translate_neg1_neg2(){
		shape = new Shape(RIGHT, DOWN, LEFT);
		shape.translate(-1,-2);
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {-1,-2, 0,-2, 0,-3, -1,-3};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset_no_translations(){
		shape.reset();
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 1,1, 2,1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset1(){
		shape = new Shape(RIGHT, RIGHT, UP);
		shape.translate(1,1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 2,0, 2,1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset2(){
		shape = new Shape(RIGHT, RIGHT, UP);
		shape.translate(5,-4);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 2,0, 2,1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset3(){
		shape = new Shape(RIGHT, RIGHT, UP);
		shape.translate(-2,10);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 2,0, 2,1};
		
		assertArrayEquals(expected, positions);
	}
	
	@Test
	public void test_reset4(){
		shape = new Shape(RIGHT, RIGHT, UP);
		shape.translate(0, 1);
		shape.reset();
		
		int[] positions = shape.getBlockPositions();
		int[] expected = {0,0, 1,0, 2,0, 2,1};
		
		assertArrayEquals(expected, positions);
	}
}
