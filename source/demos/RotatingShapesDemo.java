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
import org.lwjgl.util.vector.Vector3f;

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
	
	// OpenGL shader related variables
	private int vaoShapeId;
	private int vboShapeVerticesId;
	private int vboShapeIndicesId;
	private int vboShapeColorsId;
	private int vaoGridId;
	private int vboGridVerticesId;
	private int vboGridColorsId;
	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int modelMatrixLocation;
	private byte[] indices;
	private Vector3f[] gridVertices;
	private final int POSITION_ATTRIBUTE_LOCATION = 0;
	private final int COLOR_ATTRIBUTE_LOCATION= 1;
	
	
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
	private final int X_COORDINATE_LENGTH = 14 * 2 * (int)blockHalfWidth;
	private final int Y_COORDINATE_LENGTH = 14 * 2 * (int)blockHalfWidth;
	
	
	public RotatingShapesDemo() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		this.setupShaders();
		this.setupShapes();
		this.setupGrid();
		this.setupMatrices();
	
		// Allow User to hold down movement keys for rapid firing.
		Keyboard.enableRepeatEvents(true);
		
		while (!Display.isCloseRequested() && !escKeyPressed) {
			this.processUserInputs();
			this.logicCycle();
			this.renderCycle();
			
			if (Display.wasResized()){
				displayResizedHandler();
			}
					
			
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
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Draw only the outline of polygons.
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		
		// Setup an XNA like background color
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Adjust projection matrix and glViewport based on window aspect ratio.
		displayResizedHandler();
	}
	
	private void displayResizedHandler(){
		int width = Display.getWidth();
		int height = Display.getHeight();
		float aspectRatio = ((float)width) / height;
		float xSpan = X_COORDINATE_LENGTH;
		float ySpan = Y_COORDINATE_LENGTH;
		
		if (aspectRatio > 1){
			xSpan *= aspectRatio;
		}
		else{
			ySpan = xSpan / aspectRatio;
		}
			
		projectionMatrix = 
				GLUtils.createOrthoProjectionMatrix(-1*xSpan, xSpan, -1*ySpan, ySpan, -1, 1);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, width, height);
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
		glBindAttribLocation(programId, POSITION_ATTRIBUTE_LOCATION, "in_Position");
		// Set color attribute location to location 1
		glBindAttribLocation(programId, COLOR_ATTRIBUTE_LOCATION, "in_Color");
		
		// Get location of each matrix uniform
		projectionMatrixLocation = glGetUniformLocation(programId, "projectionMatrix");
		viewMatrixLocation = glGetUniformLocation(programId, "viewMatrix");
		modelMatrixLocation = glGetUniformLocation(programId, "modelMatrix");
		
		glValidateProgram(programId);
		
		GLUtils.exitOnGLError("setupShaders");
	}
	
	private void setupMatrices(){
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		// Translate modelMatrix so that origin is near bottom left of screen.
		Matrix4f.translate(new Vector2f(-10*blockHalfWidth, -20*blockHalfWidth),
				modelMatrix, modelMatrix);
		
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
		vaoShapeId = glGenVertexArrays();
		glBindVertexArray(vaoShapeId);
		
		
		//--Upload vertex data to GPU
		// Create a new Vertex Buffer Object to reference position data.
		vboShapeVerticesId = glGenBuffers();
		// Upload vertex position data to attribute location 0.
		glBindBuffer(GL_ARRAY_BUFFER, vboShapeVerticesId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		
		//--Upload index data to GPU
		// Create a new VBO to reference index data.
		vboShapeIndicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboShapeIndicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		
		//--Upload color data to GPU
		vboShapeColorsId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboShapeColorsId);
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		
		
		// Reset everything to default by unbinding targets.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void setupGrid(){
		vaoGridId = glGenVertexArrays();
		glBindVertexArray(vaoGridId);
		
		//----Construct Grid Vertex Data.
		int numColumns = 11;
		int numRows = 23;
		int numLines = numColumns + numRows;
		int verticesPerLine = 2;
		int numVertices = numLines * verticesPerLine; 
		
		gridVertices = new Vector3f[numVertices];
		
		for(int i = 0, j = 0; i < gridVertices.length; i+=2, j++){
			if ( j < numColumns){
				// Vertices for column lines.
				gridVertices[i] =  new Vector3f(j, 0, 0);
				gridVertices[i+1] = new Vector3f(j, numRows - 1, 0);
			}
			else {
				// Vertices for row lines.
				gridVertices[i] = new Vector3f(0, j - numColumns, 0);
				gridVertices[i+1] = new Vector3f(numColumns - 1, j - numColumns, 0);
			}
		}
		
		// Scale and translate grid vertex coordinates so the coordinate system
		// matches that of Shapes.
		for(int i = 0; i < gridVertices.length; i++){
			gridVertices[i].scale(2*blockHalfWidth);
			gridVertices[i].translate(-1*blockHalfWidth, -1*blockHalfWidth, 0f);
		}
		
		FloatBuffer gridVertexBuffer =
				BufferUtils.createFloatBuffer(gridVertices.length * 3);
		
		
		// Store vertex data in gridVertexBuffer
		for(Vector3f v : gridVertices){
			gridVertexBuffer.put(v.x);
			gridVertexBuffer.put(v.y);
			gridVertexBuffer.put(v.z);
		}
		gridVertexBuffer.flip();
		
		
		
		//----Construct Grid Color data.
		Vector3f[] vertexColors = new Vector3f[numVertices];
		// A shade of purple.
		Vector3f gridColor = new Vector3f(78f/255, 106f/255, 199f/255);
		for(int i = 0; i < vertexColors.length; i++){
			vertexColors[i] = gridColor;
		}
		
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(vertexColors.length * 3);
		// Store color data in colorBuffer.
		for(Vector3f v : vertexColors){
			colorBuffer.put(v.x);
			colorBuffer.put(v.y);
			colorBuffer.put(v.z);
		}
		colorBuffer.flip();
		
		
		
		//----Pass data to OpenGL
		// Send vertex coordinates to OpenGL
		vboGridVerticesId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboGridVerticesId);
		glBufferData(GL_ARRAY_BUFFER, gridVertexBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(POSITION_ATTRIBUTE_LOCATION, 3, GL_FLOAT, false, 0, 0);
		
		// Send vertex colors to OpenGL
		vboGridColorsId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboGridColorsId);
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(COLOR_ATTRIBUTE_LOCATION, 3, GL_FLOAT, false, 0, 0);
		
		
		// Unbind targets.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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
		glBindBuffer(GL_ARRAY_BUFFER, vboShapeVerticesId);
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
		
		renderGrid();
		
		renderShape();
		
		// Put everything back to default (deselect)
		glDisableVertexAttribArray(POSITION_ATTRIBUTE_LOCATION);
		glDisableVertexAttribArray(COLOR_ATTRIBUTE_LOCATION);
		glBindVertexArray(0);
		glUseProgram(0);
	}
	
	private void renderShape(){
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoShapeId);
		glEnableVertexAttribArray(POSITION_ATTRIBUTE_LOCATION);
		glEnableVertexAttribArray(COLOR_ATTRIBUTE_LOCATION);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboShapeIndicesId);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_BYTE, 0);
		
		
		GLUtils.exitOnGLError("renderShape");
	}
	
	private void renderGrid(){
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoGridId);
		glEnableVertexAttribArray(POSITION_ATTRIBUTE_LOCATION);
		glEnableVertexAttribArray(COLOR_ATTRIBUTE_LOCATION);
		
		glDrawArrays(GL_LINES, 0, gridVertices.length);
		
		GLUtils.exitOnGLError("renderGrid");
	}
	
	private void destroyOpenGL() {		
		// Disable the VBO index from the VAO attributes list
		glDisableVertexAttribArray(0);
		
		// Delete all VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboShapeVerticesId);
		glDeleteBuffers(vboShapeIndicesId);
		glDeleteBuffers(vboShapeColorsId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoShapeId);
		
		// Delete Shaders
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
		
		// Delete the shader program
		glDeleteProgram(programId);
		
		Display.destroy();
	}
}