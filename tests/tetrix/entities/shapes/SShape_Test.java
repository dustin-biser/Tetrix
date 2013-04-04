package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SShape_Test {
	private Shape shape;
	
	@Before
	public void setUp() throws Exception {
		shape = new SShape();
	}

	@Test
	public void test_getShapeType() {
		assertEquals(ShapeType.S, shape.getShapeType());
	}
	
	@Test
	public void test_getBlockPositions() {
		int[] expectedPositions = {3,20, 4,20, 4,21, 5,21};
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}
}
