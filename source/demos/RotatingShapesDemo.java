package demos;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

import tetrix.entities.Direction;
import tetrix.entities.rotations.RotationSystem;
import tetrix.entities.rotations.SuperRotationSystem;
import tetrix.entities.shapes.IShape;
import tetrix.entities.shapes.JShape;
import tetrix.entities.shapes.LShape;
import tetrix.entities.shapes.OShape;
import tetrix.entities.shapes.SShape;
import tetrix.entities.shapes.Shape;
import tetrix.entities.shapes.TShape;
import tetrix.entities.shapes.ZShape;
import tetrix.utilities.GLUtils;
import tetrix.utilities.ShaderUtils;
import tetrix.utilities.ShapeUtils;


public class RotatingShapesDemo {
	
	public static void main(String[] args) {
		new RotatingShapesDemo();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "Rotating Shapes Demo";
	private final int WINDOW_WIDTH = 400;
	private final int WINDOW_HEIGHT = 400;
	private final int GL_VIEWPORT_WIDTH = 400;
	private final int GL_VIEWPORT_HEIGHT = 400;
	
	// OpenGL shader related variables
	private int vaoId = 0;
	private int vboVerticesId = 0;
	private int vboIndicesId = 0;
	private int vboColorsId = 0;
	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int modelMatrixLocation;
	private byte[] indices;
	
	// OpenGL Matrix related variables
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private FloatBuffer matrix44Buffer = null;
	
	// Tetrix related variables.
	private Shape currentShape = null;
	private RotationSystem rotationSystem = null; 
	private float blockHalfWidth = 1f;  		// 1/2 Block dimension size.
	private FloatBuffer verticesBuffer = null;  // To hold Shape vertices.
	
	
	public RotatingShapesDemo() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		this.setupShaders();
		this.setupShapes();
		this.setupMatrices();
		
		while (!Display.isCloseRequested()) {
			this.processUserInputs();
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
		vertexShaderId = ShaderUtils.loadShader("source/tetrix/shaders/vertex.glsl", 
				GL_VERTEX_SHADER);
		
		// Load the fragment shader and get the shader ID.
		fragmentShaderId = ShaderUtils.loadShader("source/tetrix/shaders/fragment.glsl", 
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
		projectionMatrix = GLUtils.createOrthoProjectionMatrix(-10, 10, -10, 10, -1, 1);
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store matrices in GPU memory.f
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupShapes() {
		currentShape = new IShape();
		rotationSystem = new SuperRotationSystem();
		
		verticesBuffer = ShapeUtils.
				createVertexFloatBuffer(currentShape, this.blockHalfWidth);
		
		indices = new byte[]{ 0,1,2,    2,3,0,    	// Triangle indices Block A.
						      4,5,6,    6,7,4,		// Triangle indices Block B.
						      8,9,10,   10,11,8,	// Triangle indices Block C.
						      12,13,14, 14,15,12	// Triangle indices Block D.
							};
	
		
		// Create ByteBuffer to hold index data.
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		
		//--Upload vertex data to GPU
		// Create a new Vertex Buffer Object to reference position data.
		vboVerticesId = glGenBuffers();
		// Upload vertex position data to attribute location 0.
		glBindBuffer(GL_ARRAY_BUFFER, vboVerticesId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		
		//--Upload index data to GPU
		// Create a new VBO to reference index data.
		vboIndicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		
		// Reset everything to default by unbinding targets.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void processUserInputs(){
		while(Keyboard.next()){
			
			//-- Shape movement keys.
			if(Keyboard.getEventKey() == Keyboard.KEY_LEFT){
				if(Keyboard.getEventKeyState()){
					currentShape.moveLeft();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT){
				if(Keyboard.getEventKeyState()){
					currentShape.moveRight();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_DOWN){
				if(Keyboard.getEventKeyState()){
					currentShape.moveDown();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_D){
				if(Keyboard.getEventKeyState()){
					rotationSystem.rotate(currentShape, Direction.LEFT);
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_F){
				if(Keyboard.getEventKeyState()){
					rotationSystem.rotate(currentShape, Direction.RIGHT);
				}
			}
			
			//-- Change Shape Type.
			else if(Keyboard.getEventKey() == Keyboard.KEY_1){
				if(Keyboard.getEventKeyState()){
					currentShape = new IShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_2){
				if(Keyboard.getEventKeyState()){
					currentShape = new JShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_3){
				if(Keyboard.getEventKeyState()){
					currentShape = new LShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_4){
				if(Keyboard.getEventKeyState()){
					currentShape = new OShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_5){
				if(Keyboard.getEventKeyState()){
					currentShape = new SShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_6){
				if(Keyboard.getEventKeyState()){
					currentShape = new TShape();
				}
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_7){
				if(Keyboard.getEventKeyState()){
					currentShape = new ZShape();
				}
			}
		}
	}
	
	private void logicCycle(){
		// -- Update Vertices
		// Update Shape vertex locations within the VBO.
		verticesBuffer = ShapeUtils.createVertexFloatBuffer(currentShape, blockHalfWidth);
		glBindBuffer(GL_ARRAY_BUFFER, vboVerticesId);
		glBufferSubData(GL_ARRAY_BUFFER, 0, verticesBuffer);
		
		// Unbind the target.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		
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
		glEnableVertexAttribArray(0);
		
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
		glDeleteBuffers(vboVerticesId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		// Delete the shader program
		glDeleteProgram(programId);
		
		Display.destroy();
	}
}