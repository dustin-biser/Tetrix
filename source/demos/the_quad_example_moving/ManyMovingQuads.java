package demos.the_quad_example_moving;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tetrix.datastructures.TexturedVertex;
import tetrix.utilities.GLUtils;
import tetrix.utilities.ShaderUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;


/**
 * Demo showing how to render a various parts of a single texture.
 * @author Dustin Biser
 *
 */
public class ManyMovingQuads {
	// Entry point for the application
	public static void main(String[] args) {
		new ManyMovingQuads();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "Many Moving Quads";
	private final int WIDTH = 600;
	private final int HEIGHT = 600;
	private final double PI = 3.14159265358979323846;
	private final int numQuads = 7;
	
	// Quad variables
	private int[] vaoList = new int[numQuads];
	private int[] vboList = new int[numQuads];
	private int indicesVboId;
	private int indicesCount;
	
	// Shader variables
	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;
	
	// Texture variables
	private int[] texIds = new int[numQuads];
	
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int modelMatrixLocation;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f modelMatrix;
	private Vector3f modelPos;
	private Vector3f modelAngle;
	private Vector3f modelScale;
	private Vector3f cameraPos;
	private FloatBuffer matrix44Buffer;
	
	private boolean escKeyPressed = false;
	
	public ManyMovingQuads() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		
		this.setupQuads();
		this.setupShaders();
		this.setupTextures();
		this.setupMatrices();
		
		while (!Display.isCloseRequested() && !escKeyPressed) {
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

	private void setupMatrices() {
		// Setup projection matrix
		projectionMatrix = GLUtils.createOrthoProjectionMatrix(-6f, 6f, -6f, 6f, -1f, 1f);
		
		// Setup view matrix
		viewMatrix = new Matrix4f();
		
		// Setup model matrix
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}

	private void setupTextures() {
		// Load PNG images to specific texture objects, and associate each texture
		// with a specific Texture Image Unit.
		texIds[0] = this.loadPNGTexture("resources/textures/blue-block.png");
		
		texIds[1] = this.loadPNGTexture("resources/textures/cyan-block.png");
		
		texIds[2] = this.loadPNGTexture("resources/textures/green-block.png");
		
		texIds[3] = this.loadPNGTexture("resources/textures/orange-block.png");
		
		texIds[4] = this.loadPNGTexture("resources/textures/purple-block.png");
		
		texIds[5] = this.loadPNGTexture("resources/textures/red-block.png");
		
		texIds[6] = this.loadPNGTexture("resources/textures/yellow-block.png");
		
		// Set texture sampler to use Texture Image Unit Index 0.
		int textureSamplerLoc = GL20.glGetUniformLocation(programId, "texture_diffuse");
		GL20.glUseProgram(programId);
		GL20.glUniform1i(textureSamplerLoc, 0);
		
		// Restore Defaults
		GL20.glUseProgram(0);
		
		this.exitOnGLError("setupTexture");
	}

	private void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);
			
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(WINDOW_TITLE);
			Display.create(pixelFormat, contextAtrributes);
			
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Setup an XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		
		this.exitOnGLError("setupOpenGL");
	}
	
	private void setupQuads() {
		// Create an array of FloatBuffers, where each FloatBuffer contains the
		// vertices for one quad.
		FloatBuffer[] vertexBufferArray = createQuadVertexBuffers();
		
		// Generate 4 VAOs, one for each Quad.  Load vertex data into a separate VBO
		// for each Quad.
		for(int i = 0; i < numQuads; i++){
			// Create a new Vertex Array Object in memory and select it (bind)
			vaoList[i] = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoList[i]);
			
			// Create a new Vertex Buffer Object in memory and select it (bind)
			vboList[i] = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboList[i]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBufferArray[i], GL15.GL_STREAM_DRAW);
			
			// Put the position coordinates in attribute list 0
			GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL11.GL_FLOAT, 
					false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
			// Put the color coordinates in attribute list 1
			GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL11.GL_FLOAT, 
					false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
			// Put the texture coordinates in attribute list 1
			GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL11.GL_FLOAT, 
					false, TexturedVertex.stride, TexturedVertex.textureByteOffset);
		}
		
		// Vertex indices for tessellating each quad into 2 triangles, with
		// indices given in counter clockwise order.
		byte[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		indicesVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, 
				GL15.GL_STATIC_DRAW);
		
		
		// Deselect and unbind all buffers.
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		// Set the default quad rotation, scale and position values
		modelPos = new Vector3f(0, 0, 0);
		modelAngle = new Vector3f(0, 0, 0);
		modelScale = new Vector3f(1, 1, 1);
		cameraPos = new Vector3f(0, 0, 0);
		
		this.exitOnGLError("setupQuad");
	}
	
	private FloatBuffer[] createQuadVertexBuffers(){
		FloatBuffer[] result = new FloatBuffer[numQuads];
		ByteBuffer verticesByteBuffer;
		FloatBuffer verticesFloatBuffer;
		
		TexturedVertex[] vertices;
		TexturedVertex topLeft = new TexturedVertex();
		TexturedVertex bottomLeft = new TexturedVertex();
		TexturedVertex bottomRight = new TexturedVertex();
		TexturedVertex topRight = new TexturedVertex();
		
		Vector3f topLeft_xyz;
		Vector3f bottomLeft_xyz;
		Vector3f bottomRight_xyz;
		Vector3f topRight_xyz;
		
		Vector2f delta;
		double w = 2*Math.PI / 7D; // angular frequency
		
		for(int i = 0; i < numQuads; i++){
			topLeft_xyz = new Vector3f(-1, 1, 0);
			bottomLeft_xyz = new Vector3f(-1, -1, 0);
			bottomRight_xyz = new Vector3f(1, -1, 0);
			topRight_xyz = new Vector3f(1, 1, 0);
			
			//-- Each quad starts out at the origin, and then we translate each around
			//   circumference of circle.
			delta = new Vector2f((float)Math.cos(w * i), (float)Math.sin(w *i));
			delta.scale(4f);
			
			topLeft_xyz.translate(delta.x, delta.y, 0);
			bottomLeft_xyz.translate(delta.x, delta.y, 0);
			bottomRight_xyz.translate(delta.x, delta.y, 0);
			topRight_xyz.translate(delta.x, delta.y, 0);
			
			topLeft.setXYZ(topLeft_xyz.x, topLeft_xyz.y, topLeft_xyz.z);
			topLeft.setST(0, 0);
			
			bottomLeft.setXYZ(bottomLeft_xyz.x, bottomLeft_xyz.y, bottomLeft_xyz.z);
			bottomLeft.setST(0, 1);
			
			bottomRight.setXYZ(bottomRight_xyz.x, bottomRight_xyz.y, bottomRight_xyz.z);
			bottomRight.setST(1, 1);
			
			topRight.setXYZ(topRight_xyz.x, topRight_xyz.y, topRight_xyz.z);
			topRight.setST(1, 0);
			
			vertices = new TexturedVertex[] {topLeft, bottomLeft, bottomRight, topRight};
		
			// Put each 'Vertex' in one FloatBuffer
			verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * 
					TexturedVertex.stride);				
			
			verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
			
			for (int j = 0; j < vertices.length; j++) {
				// Add position, color and texture floats to the buffer
				verticesFloatBuffer.put(vertices[j].getElements());
			}
			verticesFloatBuffer.flip();
			
			result[i] = verticesFloatBuffer;
		}
		
		return result;
	}
	
	
	private void setupShaders() {		
		// Load the vertex shader
		vertexShaderId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/vertex.glsl", 
				GL20.GL_VERTEX_SHADER);
		
		// Load the fragment shader
		fragmentShaderId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/fragment.glsl", 
				GL20.GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderId);
		GL20.glAttachShader(programId, fragmentShaderId);
		GL20.glLinkProgram(programId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(programId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(programId, 1, "in_Color");
		// Texture information will be attribute 1
		GL20.glBindAttribLocation(programId, 2, "in_TextureCoord");
		
		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(programId, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programId, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programId, "modelMatrix");
		
		GL20.glValidateProgram(programId);
		
		this.exitOnGLError("setupShaders");
	}
	
	private void logicCycle() {
		//-- Input processing
		float rotationDelta = 15f;
		float scaleDelta = 0.1f;
		float posDelta = 0.5f;
		Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
		Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, 
				-scaleDelta);
		
		while(Keyboard.next()) {			
			// Only listen to events where the key was pressed (down event)
			if (!Keyboard.getEventKeyState()) continue;
			
			// Escape key pressed.
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
				if(Keyboard.getEventKeyState()){
					escKeyPressed = true;
				}
			}
			
			// Change model scale, rotation and translation values
			switch (Keyboard.getEventKey()) {
			// Move
			case Keyboard.KEY_UP:
				modelPos.y += posDelta;
				break;
			case Keyboard.KEY_DOWN:
				modelPos.y -= posDelta;
				break;
			// Scale
			case Keyboard.KEY_P:
				Vector3f.add(modelScale, scaleAddResolution, modelScale);
				break;
			case Keyboard.KEY_M:
				Vector3f.add(modelScale, scaleMinusResolution, modelScale);
				break;
			// Rotation
			case Keyboard.KEY_LEFT:
				modelAngle.z += rotationDelta;
				break;
			case Keyboard.KEY_RIGHT:
				modelAngle.z -= rotationDelta;
				break;
			}
		}
		
		//-- Update matrices
		// Reset view and model matrices
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		// Translate camera
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
		
		// Scale, translate and rotate model
		Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
		Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1), 
				modelMatrix, modelMatrix);
		Matrix4f.rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0), 
				modelMatrix, modelMatrix);
		Matrix4f.rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0), 
				modelMatrix, modelMatrix);
		
		// Upload matrices to the uniform variables
		GL20.glUseProgram(programId);
		
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
		
		GL20.glUseProgram(0);
		
		this.exitOnGLError("logicCycle");
	}
	
	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL20.glUseProgram(programId);
		
		// Set the active texture image unit.
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		// Render each Quad separately.
		for(int i = 0; i < numQuads; i++){
			// Bind the texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[i]);
			
			// Bind to the VAO that has all the information about the vertices
			GL30.glBindVertexArray(vaoList[i]);
			GL20.glEnableVertexAttribArray(0); // Position Data.
			GL20.glEnableVertexAttribArray(1); // Color Data.
			GL20.glEnableVertexAttribArray(2); // Texture Data.
			
			// Bind to the index VBO that has all the information about the order of the vertices
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
			
			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
		}
		
		
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL20.glUseProgram(0);
		
		this.exitOnGLError("renderCycle");
	}
	
	private void loopCycle() {
		// Update logic
		this.logicCycle();
		// Update rendered frame
		this.renderCycle();
		
		this.exitOnGLError("loopCycle");
	}
	
	private void destroyOpenGL() {	
		// Delete the texture objects.
		for(int i = 0; i < texIds.length; i++){
			GL11.glDeleteTextures(texIds[i]);
		}
		
		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);
		
		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);
		GL20.glDeleteProgram(programId);
		
		for(int i = 0; i < vaoList.length; i++){
			// Select the VAO
			GL30.glBindVertexArray(vaoList[i]);
			
			// Disable the VBO index from the VAO attributes list
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			
			// Delete the vertex VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL15.glDeleteBuffers(vboList[i]);
			
			// Delete the VAO
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(vaoList[i]);
		}
		
		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(indicesVboId);
		
		
		this.exitOnGLError("destroyOpenGL");
		
		Display.destroy();
	}
	
	private int loadPNGTexture(String filename) {
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		
		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			
			
			// Decode the PNG file in a ByteBuffer.  Each image texel has 4 components,
			// RGBA, where each component uses 1 bytes.
			buf = ByteBuffer.allocateDirect(
					4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte.
		// Alignment setting used for sending data to OpenGL.
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		
		// Restore defaults.
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		this.exitOnGLError("loadPNGTexture");
		
		return texId;
	}
	
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	
	private void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
}