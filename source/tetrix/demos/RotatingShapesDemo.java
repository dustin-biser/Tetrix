package tetrix.demos;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.PixelFormat;

public class RotatingShapesDemo {
	
	public static void main(String[] args) {
		new RotatingShapesDemo();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "Rotating Shapes Demo";
	private final int WINDOW_WIDTH = 600;
	private final int WINDOW_HEIGHT = 800;
	private final int GL_VIEWPORT_WIDTH = 400;
	private final int GL_VIEWPORT_HEIGHT = 400;
	
	
	// Quad variables
	private int vaoId = 0;
	private int vboId = 0;
	private int vboIndicesId = 0;
	private int indicesCount = 0;
	private float blockHalfWidth = 0.05f;
	
	public RotatingShapesDemo() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		
		this.setupShapes();
		
		while (!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();
			
			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}
		
		// Destroy OpenGL (Display)
		this.destroyOpenGL();
	}
	
	public void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
				.withProfileCore(true)
				.withForwardCompatible(true);
			
			Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
			Display.setTitle(WINDOW_TITLE);
			Display.create(pixelFormat, contextAtrributes);
			Display.setResizable(true);
			
			glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Setup an XNA like background color
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, GL_VIEWPORT_WIDTH, GL_VIEWPORT_HEIGHT);
	}
	
	public void setupShapes() {
		// Vertices, the order is not important.
		float[] blockVertices = {
			   -blockHalfWidth,  blockHalfWidth, 0f,	// Left top			ID: 0
			   -blockHalfWidth, -blockHalfWidth, 0f,	// Left bottom		ID: 1
				blockHalfWidth, -blockHalfWidth, 0f,	// Right bottom		ID: 2
				blockHalfWidth,  blockHalfWidth, 0f		// Right top 	    ID: 3
		};
		
		// Sending data to OpenGL requires the usage of (flipped) byte buffers
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(blockVertices.length);
		verticesBuffer.put(blockVertices);
		verticesBuffer.flip();
		
		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = {
				// Left bottom triangle
				0, 1, 2,
				// Right top triangle
				2, 3, 0
		};
		
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind)
		vboIndicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void loopCycle() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		
		// Draw the vertices
		glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public void destroyOpenGL() {		
		// Disable the VBO index from the VAO attributes list
		glDisableVertexAttribArray(0);
		
		// Delete the vertex VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboId);
		
		// Delete the index VBO
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboIndicesId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		Display.destroy();
	}
}