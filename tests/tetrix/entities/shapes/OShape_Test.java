package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OShape_Test {
	private Shape shape;
	
	@Before
	public void setUp() throws Exception {
		shape = new OShape();
	}

	@Test
	public void test_getShapeType() {
		assertEquals(ShapeType.O, shape.getShapeType());
	}
	
	@Test
	public void test_getBlockPositions(){
		int[] expectedPostions = {4,20, 5,20, 5,21, 4,21};
		assertArrayEquals(expectedPostions, shape.getBlockPositions());
	}

}
