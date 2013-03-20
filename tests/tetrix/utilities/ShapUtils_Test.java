package tetrix.utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

import tetrix.entities.shapes.IShape;
import tetrix.entities.shapes.Shape;

public class ShapUtils_Test {
	private Shape shape;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test_createBlockVertices() {
		shape = new IShape();
		float blockHalfWidth = 1.0f;
		Vector3f[] block_vertices = ShapeUtils.createBlockVertices(shape, blockHalfWidth);
		Float[] vertex_coords = new Float[block_vertices.length * 2];
		
		Float[] expected_coords = {-1f,-1f, 1f,-1f, 1f,1f, -1f,1f, // Block A coordinates.
								    1f,-1f, 3f,-1f, 3f,1f,  1f,1f, // Block B coordinates.
								    3f,-1f, 5f,-1f, 5f,1f,  3f,1f, // Block C coordinates.
								    5f,-1f, 7f,-1f, 7f,1f,  5f,1f  // Block D coordinates.
								  };
		
		// Fill up the vertex_coords array with Block vertex coordinates.
		for(int i = 0; i < block_vertices.length; i++){
			vertex_coords[2*i] = block_vertices[i].x;
			vertex_coords[2*i + 1] = block_vertices[i].y;
		}
		
		assertArrayEquals(expected_coords, vertex_coords);
	}

}
