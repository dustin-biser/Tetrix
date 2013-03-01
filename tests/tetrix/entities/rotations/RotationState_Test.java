package tetrix.entities.rotations;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static tetrix.entities.rotations.RotationState.*;

public class RotationState_Test {
	private RotationState rotationState;

	@Before
	public void setUp() throws Exception {
		rotationState = RotationState.SPAWN_STATE;
	}

	@Test
	public void test_spawnState() {
		assertEquals(rotationState, SPAWN_STATE);
	}
	
	@Test
	public void test_right1() {
		rotationState = rotationState.right();
		assertEquals(rotationState, RIGHT_OF_SPAWN);
	}
	
	@Test
	public void test_right2() {
		rotationState = rotationState.right().right();
		assertEquals(rotationState, SECOND_ROTATION);
	}
	
	@Test
	public void test_right3() {
		rotationState = rotationState.right().right().right();
		assertEquals(rotationState, LEFT_OF_SPAWN);
	}
	
	@Test
	public void test_right_fullCircle() {
		rotationState = rotationState.right().right().right().right();
		assertEquals(rotationState, SPAWN_STATE);
	}
	
	@Test
	public void test_left1() {
		rotationState = rotationState.left();
		assertEquals(rotationState, LEFT_OF_SPAWN);
	}
	
	@Test
	public void test_left2() {
		rotationState = rotationState.left().left();
		assertEquals(rotationState, SECOND_ROTATION);
	}
	
	@Test
	public void test_left3() {
		rotationState = rotationState.left().left().left();
		assertEquals(rotationState, RIGHT_OF_SPAWN);
	}
	
	@Test
	public void test_left_fullCircle() {
		rotationState = rotationState.left().left().left().left();
		assertEquals(rotationState, SPAWN_STATE);
	}	
	
	@Test
	public void test_right_left() {
		rotationState = rotationState.right().left();
		assertEquals(rotationState, SPAWN_STATE);
	}	
	
	@Test
	public void test_right_right_left() {
		rotationState = rotationState.right().right().left();
		assertEquals(rotationState, RIGHT_OF_SPAWN);
	}	
	
	@Test
	public void test_left_left_right() {
		rotationState = rotationState.left().left().right();
		assertEquals(rotationState, LEFT_OF_SPAWN);
	}	

}
