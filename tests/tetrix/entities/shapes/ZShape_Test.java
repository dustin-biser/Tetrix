package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ZShape_Test {
	private Shape shape;

	@Before
	public void setUp() throws Exception {
		shape = new ZShape();
	}

	@Test
	public void test_getShapeType() {
		assertEquals(ShapeType.Z, shape.getShapeType());
	}
	
	@Test
	public void test_getBlockPositions(){
		int[] expectedPositions = {3,21, 4,21, 4,20, 5,20};
		assertArrayEquals(expectedPositions, shape.getBlockPositions());
	}

}
