package tetrix.entities.shapes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tetrix.entities.shapes.Block;

public class Block_Test {
	private Block block;
	
	@Before
	public void setUp() throws Exception {
		block = new Block(0, 0);
	}

	@Test
	public void test_getRow() {
		assertEquals(block.getRow(), 0);
	}
	
	@Test
	public void test_getColumn() {
		assertEquals(block.getColumn(), 0);
	}
	
	@Test
	public void test_translation1() {
		block.translate(1, 0);
		
		assertEquals(block.getColumn(), 1);
		assertEquals(block.getRow(), 0);
	}
	
	@Test
	public void test_translation2() {
		block.translate(0, 1);
		
		assertEquals(block.getColumn(), 0);
		assertEquals(block.getRow(), 1);
	}
	
	@Test
	public void test_translation3() {
		block = new Block(10, -10);
		block.translate(-10, 10);
		
		assertEquals(block.getColumn(), 0);
		assertEquals(block.getRow(), 0);
	}
	
	@Test
	public void test_translation4() {
		block.translate(-4, -44);
		
		assertEquals(block.getColumn(), -4);
		assertEquals(block.getRow(), -44);
	}

}
