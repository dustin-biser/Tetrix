package tetrix.entities.rotations;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;
import static tetrix.entities.rotations.RotationState.*;
import static tetrix.entities.rotations.RotationTransition.*;

public class RotationTransition_Test {
	private RotationTransition transition;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test_OtoR() {
		transition = RotationTransition.getTransition(SPAWN_STATE, RIGHT_OF_SPAWN);
		assertEquals(OtoR, transition);
	}
	
	@Test
	public void test_RtoO() {
		transition = RotationTransition.getTransition(RIGHT_OF_SPAWN, SPAWN_STATE);
		assertEquals(RtoO, transition);
	}
	
	@Test
	public void test_RtoTwo() {
		transition = RotationTransition.getTransition(RIGHT_OF_SPAWN, SECOND_ROTATION);
		assertEquals(RtoTwo, transition);
	}
	
	@Test
	public void test_TwotoR() {
		transition = RotationTransition.getTransition(SECOND_ROTATION, RIGHT_OF_SPAWN);
		assertEquals(TwotoR, transition);
	}
	
	@Test
	public void test_TwotoL() {
		transition = RotationTransition.getTransition(SECOND_ROTATION, LEFT_OF_SPAWN);
		assertEquals(TwotoL, transition);
	}
	
	@Test
	public void test_LtoTwo() {
		transition = RotationTransition.getTransition(LEFT_OF_SPAWN, SECOND_ROTATION);
		assertEquals(LtoTwo, transition);
	}
	
	@Test
	public void test_LtoO() {
		transition = RotationTransition.getTransition(LEFT_OF_SPAWN, SPAWN_STATE);
		assertEquals(LtoO, transition);
	}
	
	@Test
	public void test_OtoL() {
		transition = RotationTransition.getTransition(SPAWN_STATE, LEFT_OF_SPAWN);
		assertEquals(OtoL, transition);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Test invalid input parameters
	/////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=InvalidParameterException.class)
	public void test_invalid_OtoTwo(){
		transition = RotationTransition.getTransition(SPAWN_STATE, SECOND_ROTATION);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void test_invalid_TwotoO(){
		RotationTransition.getTransition(SECOND_ROTATION, SPAWN_STATE);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void test_invalid_RtoL(){
		RotationTransition.getTransition(RIGHT_OF_SPAWN, LEFT_OF_SPAWN);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void test_invalid_LtoR(){
		RotationTransition.getTransition(LEFT_OF_SPAWN, RIGHT_OF_SPAWN);
	}
}
