package tetrix.entities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShapeConfigurationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_getBlockCoordinates() {
		ShapeConfiguration config = new ShapeConfiguration(0,0, 0,1, 0,2, 0,3); 
		int[] expected = {0,0, 0,1, 0,2, 0,3};
		int[] blockCoordinates = config.getBlockCoordinates(); 
		
		for(int i = 0; i < expected.length; i++){
			assertEquals(blockCoordinates[i], expected[i]);
		}
		
	}

}
