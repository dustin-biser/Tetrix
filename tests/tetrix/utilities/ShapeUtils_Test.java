package tetrix.utilities;

import static org.junit.Assert.*;

import java.nio.FloatBuffer;

import org.junit.Before;
import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

import tetrix.entities.shapes.IShape;
import tetrix.entities.shapes.JShape;
import tetrix.entities.shapes.Shape;

public class ShapeUtils_Test {
	private Shape shape;
	
	// Use Float arrays so we can use assertArrayEquals().
	private Float[] expected_coords_IShape;
	private Float[] expected_coords_JShape;

	@Before
	public void setUp() throws Exception {
		expected_coords_IShape = new Float[]{
		       -1f,-1f,0f,  1f,-1f,0f,  1f,1f,0f,  -1f,1f,0f,  // Block A xyz coordinates.
			    1f,-1f,0f,  3f,-1f,0f,  3f,1f,0f,   1f,1f,0f,  // Block B xyz coordinates.
			    3f,-1f,0f,  5f,-1f,0f,  5f,1f,0f,   3f,1f,0f,  // Block C xyz coordinates.
			    5f,-1f,0f,  7f,-1f,0f,  7f,1f,0f,   5f,1f,0f   // Block D xyz coordinates.
			   };
		
		expected_coords_JShape = new Float[]{
		       -1f,-1f,0f,  1f,-1f,0f,  1f,1f,0f,  -1f,1f,0f,  // Block A xyz coordinates.
			   -1f,-3f,0f,  1f,-3f,0f,  1f,-1f,0f, -1f,-1f,0f, // Block B xyz coordinates.
			    1f,-3f,0f,  3f,-3f,0f,  3f,-1f,0f,  1f,-1f,0f, // Block C xyz coordinates.
			    3f,-3f,0f,  5f,-3f,0f,  5f,-1f,0f,  3f,-1f,0f  // Block D xyz coordinates.
			   };
	}
	
	/**
	 * @return Float array with only x, y coordinates from vectorArray argument.
	 */
	private Float[] toFloatArray(Vector3f[] vectorArray){
		Float[] floatArray = new Float[vectorArray.length * 3];
		
		
		// Fill up the floatArray array with Block vertex coordinates.
		for(int i = 0; i < vectorArray.length; i++){
			floatArray[3*i]     = vectorArray[i].x;
			floatArray[3*i + 1] = vectorArray[i].y;
			floatArray[3*i + 2] = vectorArray[i].z;
		}
		
		return floatArray;
	}
	
	private void scaleFloatArray(Float[] floatArray, float scaleFactor){
		for(int i = 0; i < floatArray.length; i++){
			floatArray[i] *= scaleFactor;
		}
	}
	
	private Float[] floatBufferToFloatArray(FloatBuffer floatBuffer){
		Float[] result = new Float[floatBuffer.capacity()];
		
		// Reset the buffer's position before calling get().
		floatBuffer.rewind();
		
		for(int i = 0; i < floatBuffer.capacity(); i++){
			result[i] = floatBuffer.get(); 
		}
		
		// Reset the buffer back to its initial state.
		floatBuffer.rewind();
		
		return result;
	}

	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth1() {
		shape = new IShape();
		float blockHalfWidth = 1.0f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = this.toFloatArray(block_vertices);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth2() {
		shape = new IShape();
		float blockHalfWidth = 2.0f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_IShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_IShape_blockHalfWidth1_5() {
		shape = new IShape();
		float blockHalfWidth = 1.5f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_IShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_IShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_JShape_blockHalfWidth1() {
		shape = new JShape();
		float blockHalfWidth = 1f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toFloatArray(block_vertices);
		
		assertArrayEquals(expected_coords_JShape, vertex_coords);
	}
	
	@Test
	public void test_createBlockVertices_JShape_blockHalfWidth3_2() {
		shape = new JShape();
		float blockHalfWidth = 3.2f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = toFloatArray(block_vertices);
		
		this.scaleFloatArray(expected_coords_JShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_JShape, vertex_coords);
	}
	
	@Test
	public void test_createVertexFloatBuffer_IShape_blockHalfWidth1() {
		shape = new IShape();
		float blockHalfWidth = 1f;
		FloatBuffer vertexBuffer =
				ShapeUtils.createVertexFloatBuffer(shape, blockHalfWidth);
	
		Float[] vertexArray = this.floatBufferToFloatArray(vertexBuffer);
		
		assertArrayEquals(expected_coords_IShape, vertexArray);
	}
	
	@Test
	public void test_createVertexFloatBuffer_IShape_blockHalfWidth2() {
		shape = new IShape();
		float blockHalfWidth = 2f;
		FloatBuffer vertexBuffer =
				ShapeUtils.createVertexFloatBuffer(shape, blockHalfWidth);
	
		Float[] vertexArray = this.floatBufferToFloatArray(vertexBuffer);
		
		this.scaleFloatArray(expected_coords_IShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_IShape, vertexArray);
	}
	
	@Test
	public void test_createVertexFloatBuffer_JShape_blockHalfWidth1() {
		shape = new JShape();
		float blockHalfWidth = 1f;
		FloatBuffer vertexBuffer =
				ShapeUtils.createVertexFloatBuffer(shape, blockHalfWidth);
	
		Float[] vertexArray = this.floatBufferToFloatArray(vertexBuffer);
		
		assertArrayEquals(expected_coords_JShape, vertexArray);
	}
	
	@Test
	public void test_createVertexFloatBuffer_JShape_blockHalfWidth3_4() {
		shape = new JShape();
		float blockHalfWidth = 3.4f;
		FloatBuffer vertexBuffer =
				ShapeUtils.createVertexFloatBuffer(shape, blockHalfWidth);
	
		Float[] vertexArray = this.floatBufferToFloatArray(vertexBuffer);
		this.scaleFloatArray(expected_coords_JShape, blockHalfWidth);
		
		assertArrayEquals(expected_coords_JShape, vertexArray);
	}
}
