package tetrix.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tetrix.entities.rotations.RotationState;

public class Shape_Test {
	private Shape iShape;
	private Shape sShape;
	private Shape zShape;
	
	@Before
	public void setUp() throws Exception {
		iShape = new Shape(1,0, 2,0, 3,0);
		sShape = new Shape(1,0, 1,1, 2,1);
		zShape = new Shape(1,0, 1,-1, 2,-1);
	}

	@Test
	public void test_initial_RotationState() {
		assertEquals(iShape.getRotationState(), RotationState.SPAWN_STATE);
	}
	
	@Test
	public void test_set_RotationState() {
		iShape.setRotationState(RotationState.SECOND_ROTATION);
		assertEquals(iShape.getRotationState(), RotationState.SECOND_ROTATION);
	}
	
	@Test
	public void test_getBlockPositions(){
		int[] positions = iShape.getBlockPositions();
	}

}
