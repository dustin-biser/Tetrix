package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LShape_Test {
	private Shape shape;

	@Before
	public void setUp() throws Exception {
		shape = new LShape();
	}
	
	@Test
	public void test_getShapeType(){
		assertEquals(ShapeType.L, shape.getShapeType());
	}

	@Test
	public void test_getBlockPositions() {
		int[] expectedPositions = {3,20, 4,20, 5,20, 5,21};
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}

}
