package demos.tessalated_quad;

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
import org.lwjgl.util.vector.Matrix4f;

import tetrix.utilities.GLUtils;
import tetrix.utilities.ShaderUtils;


public class TessalatedQuadDemo {
	
	public static void main(String[] args) {
		new TessalatedQuadDemo();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "Tessalated Quad";
	private final int WINDOW_WIDTH = 400;
	private final int WINDOW_HEIGHT = 400;
	private final int GL_VIEWPORT_WIDTH = 400;
	private final int GL_VIEWPORT_HEIGHT = 400;
	
	// OpenGL shader related variables
	private int vaoId = 0;
	private int vboVertexId = 0;
	private int vboIndicesId = 0;
	private int vboColorId = 0;
	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;
	private byte[] indices; // to hold quad vertex indices.
	
	// OpenGL Matrix related variables
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int modelMatrixLocation;
	private FloatBuffer matrix44Buffer = null;
	
	public TessalatedQuadDemo() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		this.setupShaders();
		this.setupShapes();
		this.setupMatrices();
		
		while (!Display.isCloseRequested()) {
			this.logicCycle();
			this.renderCycle();
			
			// Force a maximum FPS of about 60
			Display.sync(60);
			
			// Update window by swapping buffers and polling input devices.
			Display.update();
		}
		
		// Destroy OpenGL (Display)
		this.destroyOpenGL();
	}
	
	private void setupOpenGL() {
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
		
		// Draw only the outline of polygons.
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		
		// Setup an XNA like background color
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, GL_VIEWPORT_WIDTH, GL_VIEWPORT_HEIGHT);
	}
	
	private void setupShaders() {		
		// Load the vertex shader and get the shader ID.
		vertexShaderId = ShaderUtils.loadShader(
				"source/demos/tessalated_quad/shaders/vertex.glsl",
				GL_VERTEX_SHADER);
		
		// Load the fragment shader and get the shader ID.
		fragmentShaderId = ShaderUtils.loadShader(
				"source/demos/tessalated_quad/shaders/fragment.glsl", 
				GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		programId = glCreateProgram();
		glAttachShader(programId, vertexShaderId);
		glAttachShader(programId, fragmentShaderId);
		glLinkProgram(programId);

		// Set position attribute location to location 0
		glBindAttribLocation(programId, 0, "in_Position");
		// Set color attribute location to location 1
		glBindAttribLocation(programId, 1, "in_Color");
		
		// Get location of each matrix uniform
		projectionMatrixLocation = glGetUniformLocation(programId, "projectionMatrix");
		viewMatrixLocation = glGetUniformLocation(programId, "viewMatrix");
		modelMatrixLocation = glGetUniformLocation(programId, "modelMatrix");
		
		glValidateProgram(programId);
		
		GLUtils.exitOnGLError("setupShaders");
	}
	
	
	private void setupMatrices(){
		projectionMatrix = GLUtils.createOrthoProjectionMatrix(-0.8f, 0.8f,
															   -0.8f, 0.8f,
															   -0.8f, 0.8f);
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store matrices in GPU memory.f
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupShapes() {
		// Vertex data that defines the Quad.
		float[] vertices = { -0.75f, -0.75f,  0.0f,
							  0.75f, -0.75f,  0.0f,
							  0.75f,  0.75f,  0.0f,
							 -0.75f, 0.75f,  0.0f };
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.flip();
		
		// Index data for specifying the two triangles that make up the Quad.  Indices
		// are given in counter-clockwise order.
	    byte[] quad_indices = { 0, 1, 2,
					            2, 3, 0 };
	    
	    this.indices = quad_indices;
				
		ByteBuffer indexBuffer = BufferUtils.createByteBuffer(indices.length);
		indexBuffer.put(indices);
		indexBuffer.flip();
		
		// Color data
		float[] colors = { 0.0f, 0.0f, 0.0f,
						   0.0f, 0.0f, 0.0f,
						   0.0f, 0.0f, 0.0f,
						   0.0f, 0.0f, 0.0f};
		
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors);
		colorBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		//-- Upload vertex data to GPU
		// Create a new Vertex Buffer Object to reference position data.
		vboVertexId = glGenBuffers();
		// Upload vertex position data to attribute location 0.
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexId);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		//-- Upload indices to GPU
		vboIndicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		//-- Upload color data to GPU
		// Create a new Vertex Buffer Object to reference color data.
		vboColorId = glGenBuffers();
		// Upload color data to attribute location 1.
		glBindBuffer(GL_ARRAY_BUFFER, vboColorId);
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		
		
		// Unbind all targets.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void logicCycle(){
		//-- Update OpenGL Matrices
		// Upload matrices to the uniform variables
		glUseProgram(programId);
		
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
		
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
		
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
		
		glUseProgram(0);
		
		GLUtils.exitOnGLError("logicCycle");
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		glUseProgram(programId);
		
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoId);
		// Enable vertex position attribute location.
		glEnableVertexAttribArray(0);
		// Enable vertex color attribute location.
		glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_BYTE, 0); 
		
		// Put everything back to default (deselect)
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		glUseProgram(0);
		
		GLUtils.exitOnGLError("renderCycle");
	}
	
	private void destroyOpenGL() {		
		// Disable the VBO index from the VAO attributes list
		glDisableVertexAttribArray(0);
		
		// Delete the vertex VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboVertexId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		// Delete the shader program
		glDeleteProgram(programId);
		
		Display.destroy();
	}
}