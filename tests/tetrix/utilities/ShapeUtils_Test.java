package tetrix.utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

import tetrix.entities.shapes.IShape;
import tetrix.entities.shapes.JShape;
import tetrix.entities.shapes.Shape;

public class ShapeUtils_Test {
	private Shape shape;
	private Float[] expected_coords_IShape;
	private Float[] expected_coords_JShape;

	@Before
	public void setUp() throws Exception {
		expected_coords_IShape = new Float[]{
		       -1f,-1f, 1f,-1f, 1f,1f, -1f,1f, // Block A coordinates.
			    1f,-1f, 3f,-1f, 3f,1f,  1f,1f, // Block B coordinates.
			    3f,-1f, 5f,-1f, 5f,1f,  3f,1f, // Block C coordinates.
			    5f,-1f, 7f,-1f, 7f,1f,  5f,1f  // Block D coordinates.
			   };
		
		expected_coords_JShape = new Float[]{
		       -1f,-1f, 1f,-1f, 1f,1f,  -1f,1f,    // Block A coordinates.
			   -1f,-3f, 1f,-3f, 1f,-1f, -1f,-1f,   // Block B coordinates.
			    1f,-3f, 3f,-3f, 3f,-1f, 1f,-1f,    // Block C coordinates.
			    3f,-3f, 5f,-3f, 5f,-1f, 3f,-1f     // Block D coordinates.
			   };
	}
	
	/**
	 * @return Float array with only x, y coordinates from vectorArray argument.
	 */
	private Float[] toXYFloatArray(Vector3f[] vectorArray){
		Float[] floatArray = new Float[vectorArray.length * 2];
		
		
		// Fill up the floatArray array with Block vertex coordinates.
		for(int i = 0; i < vectorArray.length; i++){
			floatArray[2*i]     = vectorArray[i].x;
			floatArray[2*i + 1] = vectorArray[i].y;
		}
		
		return floatArray;
	}
	
	private void scaleFloatArray(Float[] floatArray, float scaleFactor){
		for(int i = 0; i < floatArray.length; i++){
			floatArray[i] *= scaleFactor;
		}
	}

	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth1() {
		shape = new IShape();
		float blockHalfWidth = 1.0f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toXYFloatArray(block_vertices);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth2() {
		shape = new IShape();
		float blockHalfWidth = 2.0f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toXYFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_IShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth1_5() {
		shape = new IShape();
		float blockHalfWidth = 1.5f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toXYFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_IShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_JShape_blockHalfWidth1() {
		shape = new JShape();
		float blockHalfWidth = 1f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toXYFloatArray(block_vertices);
		
		assertArrayEquals(expected_coords_JShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_JShape_blockHalfWidth3_2() {
		shape = new JShape();
		float blockHalfWidth = 3.2f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toXYFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_JShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_JShape, vertex_coords);
	}
}
