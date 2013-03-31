package tetrix.entities.rotations;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import tetrix.entities.Direction;
import tetrix.entities.shapes.IShape;
import tetrix.entities.shapes.OShape;
import tetrix.entities.shapes.Shape;

public class SuperRotationSystem_Test {
	private Shape shapeI;
	private Shape shapeO;
	private RotationSystem rotationSystem = new SuperRotationSystem();
	
	@Before
	public void setUp() throws Exception {
		shapeI = new IShape();
		shapeO = new OShape();
	}

	@Test(expected=InvalidParameterException.class)
	public void test_rotate_invalid_param_up() {
		rotationSystem.rotate(shapeI, Direction.UP);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void test_rotate_invalid_param_down() {
		rotationSystem.rotate(shapeI, Direction.DOWN);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Test RotationState
	/////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void test_IShape_RotationState_rotate_RIGHT_OtoR(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertEquals(RotationState.RIGHT_OF_SPAWN, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_RIGHT_OtoRtoTwo(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertEquals(RotationState.SECOND_ROTATION, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_RIGHT_OtoRtoTwotoL(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertEquals(RotationState.LEFT_OF_SPAWN, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_RIGHT_FullCircle(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertEquals(RotationState.SPAWN_STATE, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_LEFT_OtoL(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertEquals(RotationState.LEFT_OF_SPAWN, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_LEFT_OtoLtoTwo(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertEquals(RotationState.SECOND_ROTATION, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_LEFT_OtoLtoTwotoR(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertEquals(RotationState.RIGHT_OF_SPAWN, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_LEFT_FullCircle(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertEquals(RotationState.SPAWN_STATE, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_LEFT_RIGHT(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertEquals(RotationState.SPAWN_STATE, shapeI.getRotationState());
	}
	
	@Test
	public void test_IShape_RotationState_rotate_RIGHT_LEFT(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertEquals(RotationState.SPAWN_STATE, shapeI.getRotationState());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Test IShape Modified Block Positions
	/////////////////////////////////////////////////////////////////////////////////////
	
	// IShapes that will be manually rotated to specific RotationStates.
	private static IShape shapeI_atO = new IShape();
	private static IShape shapeI_atR = new IShape();
	private static IShape shapeI_atTwo = new IShape();
	private static IShape shapeI_atL = new IShape();
	
	static {
		shapeI_atR.translateA(2, 1);
		shapeI_atR.translateB(1, 0);
		shapeI_atR.translateC(0, -1);
		shapeI_atR.translateD(-1, -2);
	}
	
	static {
		shapeI_atTwo.translateA(3, -1);
		shapeI_atTwo.translateB(1, -1);
		shapeI_atTwo.translateC(-1, -1);
		shapeI_atTwo.translateD(-3, -1);
	}
	
	static {
		shapeI_atL.translateA(1, -2);
		shapeI_atL.translateB(0, -1);
		shapeI_atL.translateC(-1, 0);
		shapeI_atL.translateD(-2, 1);
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_OtoR(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atR.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_OtoRtoTwo(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atTwo.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_OtoRtoTwotoL(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atL.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_FullCircle(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atO.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_OtoL(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atL.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_OtoLtoTwo(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atTwo.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_OtoLtoTwotoR(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atR.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_FullCircle(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atO.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_RIGHT(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atO.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_LEFT(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atO.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_LEFT_LEFT_RIGHT(){
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		
		assertArrayEquals(shapeI_atL.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	@Test
	public void test_IShape_BlockPositions_rotate_RIGHT_RIGHT_LEFT(){
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.RIGHT);
		rotationSystem.rotate(shapeI, Direction.LEFT);
		
		assertArrayEquals(shapeI_atR.getBlockPositions(), shapeI.getBlockPositions());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Test OShape Rotation
	/////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void test_OShape_BlockPositions_rotate_LEFT(){
		int[] expectedBlockPositions = shapeO.getBlockPositions();
		
		rotationSystem.rotate(shapeO, Direction.LEFT);
		
		// Block positions should not have changed.
		assertArrayEquals(expectedBlockPositions, shapeO.getBlockPositions());
	}
	
	@Test
	public void test_OShape_BlockPositions_rotate_RIGHT(){
		int[] expectedBlockPositions = shapeO.getBlockPositions();
		
		rotationSystem.rotate(shapeO, Direction.RIGHT);
		
		// Block positions should not have changed.
		assertArrayEquals(expectedBlockPositions, shapeO.getBlockPositions());
	}
	
}
