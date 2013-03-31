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
import org.lwjgl.util.vector.Vector2f;

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
	private final int WINDOW_WIDTH = 600;
	private final int WINDOW_HEIGHT = 800;
	private final int GL_VIEWPORT_WIDTH = 600;
	private final int GL_VIEWPORT_HEIGHT = 800;
	
	// OpenGL shader related variables
	private int vaoId;
	private int vboVerticesId;
	private int vboIndicesId;
	private int vboColorsId;
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
	private float blockHalfWidth = 1f;
	private FloatBuffer verticesBuffer = null;
	
	boolean escKeyPressed = false;
	
	
	public RotatingShapesDemo() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		this.setupShaders();
		this.setupShapes();
		this.setupMatrices();
		
		while (!Display.isCloseRequested() && !escKeyPressed) {
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
			Display.setResizable(true);
			Display.create(pixelFormat, contextAtrributes);
			
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
		float aspectRatio = GL_VIEWPORT_WIDTH / (float) GL_VIEWPORT_HEIGHT;
		float scale = 0.05f;
		
		float xSpan = 1 / scale;
		float ySpan = xSpan / aspectRatio;
		
		// Setup Projection Matrix so that xSpan / ySpan = aspectRatio.
		projectionMatrix = GLUtils.createOrthoProjectionMatrix(-1*xSpan, xSpan, -1*ySpan, ySpan, -1, 1);
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		// Translate modelMatrix by half a pixel so that lines land on pixel centers.
		// This will allow corner pixels to be rendered for each Shape.
		Matrix4f.translate(new Vector2f(0.5f,0.5f), modelMatrix, modelMatrix);
		
		// Create a FloatBuffer with the proper size to store matrices in GPU memory.
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupShapes() {
		currentShape = new IShape();
		rotationSystem = new SuperRotationSystem();
		
		// Create FloatBuffer to hold vertex data.
		verticesBuffer = ShapeUtils.
				createVertexFloatBuffer(currentShape, this.blockHalfWidth);
		
		// Create index data and store in a direct ByteBuffer
		indices = new byte[]{ 0,1,2,    2,3,0,    	// Triangle indices Block A.
						      4,5,6,    6,7,4,		// Triangle indices Block B.
						      8,9,10,   10,11,8,	// Triangle indices Block C.
						      12,13,14, 14,15,12	// Triangle indices Block D.
							};
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create color data for each vertex.  For each vertex there will be
		// associated three float components corresponding to Red, Green, and Blue
		// intensities.
		float[] colors = new float[verticesBuffer.capacity()];
		for(int i = 0; i < colors.length; i++){
			// Set to black.
			colors[i] = 0.0f;
		}
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors);
		colorBuffer.flip();
		
		
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
		
		
		//--Upload color data to GPU
		vboColorsId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboColorsId);
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		
		
		// Reset everything to default by unbinding targets.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void processUserInputs(){
		while(Keyboard.next()){
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
				if(Keyboard.getEventKeyState()){
					escKeyPressed = true;
				}
			}
			
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
		
		// Delete all VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboVerticesId);
		glDeleteBuffers(vboIndicesId);
		glDeleteBuffers(vboColorsId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		// Delete Shaders
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
		
		// Delete the shader program
		glDeleteProgram(programId);
		
		Display.destroy();
	}
}