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
import org.lwjgl.util.vector.Vector3f;

import tetrix.datastructures.TexturedVertex;
import tetrix.utilities.GLUtils;
import tetrix.utilities.ShaderUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TheQuadExampleMoving {
	// Entry point for the application
	public static void main(String[] args) {
		new TheQuadExampleMoving();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "The Quad: Moving";
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private final double PI = 3.14159265358979323846;
	
	// Quad variables
	private int vaoId = 0;
	private int vboId = 0;
	private int vboiId = 0;
	private int indicesCount = 0;
	private TexturedVertex[] vertices = null;
	private ByteBuffer verticesByteBuffer = null;
	
	// Shader variables
	private int vsId = 0;
	private int fsId = 0;
	private int pId = 0;
	private int baseImageLoc = 0;
	
	// Texture variables
	private int[] texIds = new int[] {0, 0};
	private int textureSelector = 0;
	
	// Moving variables
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private Vector3f modelPos = null;
	private Vector3f modelAngle = null;
	private Vector3f modelScale = null;
	private Vector3f cameraPos = null;
	private FloatBuffer matrix44Buffer = null;
	
	public TheQuadExampleMoving() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		
		this.setupQuad();
		this.setupShaders();
		this.setupTextures();
		this.setupMatrices();
		
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

	private void setupMatrices() {
		float fieldOfView = 60f;
		float aspectRatio = (float)WIDTH / (float)HEIGHT;
		float near_plane = 0.1f;
		float far_plane = 100f;
		
		// Setup projection matrix
		projectionMatrix = GLUtils.createProjectionMatrix(aspectRatio, fieldOfView,
				near_plane, far_plane);
		
		// Setup view matrix
		viewMatrix = new Matrix4f();
		
		// Setup model matrix
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}

	private void setupTextures() {
		texIds[0] = this.loadPNGTexture("resources/textures/stGrid1.png",
				GL13.GL_TEXTURE0);
		
		texIds[1] = this.loadPNGTexture("resources/textures/stGrid2.png",
				GL13.GL_TEXTURE0);
		
		// Set texture sampler to use Texture Image Unit Index 0.
		baseImageLoc = GL20.glGetUniformLocation(pId, "texture_diffuse");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(baseImageLoc, 0);
		
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
	
	private void setupQuad() {
		// We'll define our quad using 4 vertices of the custom 'TexturedVertex' class
		TexturedVertex v0 = new TexturedVertex(); 
		v0.setXYZ(-0.5f, 0.5f, 0); v0.setRGB(1, 0, 0); v0.setST(0, 0);
		TexturedVertex v1 = new TexturedVertex(); 
		v1.setXYZ(-0.5f, -0.5f, 0); v1.setRGB(0, 1, 0); v1.setST(0, 1);
		TexturedVertex v2 = new TexturedVertex(); 
		v2.setXYZ(0.5f, -0.5f, 0); v2.setRGB(0, 0, 1); v2.setST(1, 1);
		TexturedVertex v3 = new TexturedVertex(); 
		v3.setXYZ(0.5f, 0.5f, 0); v3.setRGB(1, 1, 1); v3.setST(1, 0);
		
		vertices = new TexturedVertex[] {v0, v1, v2, v3};
		
		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * 
				TexturedVertex.stride);				
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();
		
		
		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL11.GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL11.GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL11.GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.textureByteOffset);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, 
				GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		// Set the default quad rotation, scale and position values
		modelPos = new Vector3f(0, 0, 0);
		modelAngle = new Vector3f(0, 0, 0);
		modelScale = new Vector3f(1, 1, 1);
		cameraPos = new Vector3f(0, 0, 0);
		
		this.exitOnGLError("setupQuad");
	}
	
	private void setupShaders() {		
		// Load the vertex shader
		vsId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/vertex.glsl", 
				GL20.GL_VERTEX_SHADER);
		
		// Load the fragment shader
		fsId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/fragment.glsl", 
				GL20.GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);
		GL20.glLinkProgram(pId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(pId, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");
		
		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
		
		GL20.glValidateProgram(pId);
		
		this.exitOnGLError("setupShaders");
	}
	
	private void logicCycle() {
		//-- Input processing
		float rotationDelta = 15f;
		float scaleDelta = 0.1f;
		float posDelta = 0.1f;
		Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
		Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, 
				-scaleDelta);
		
		while(Keyboard.next()) {			
			// Only listen to events where the key was pressed (down event)
			if (!Keyboard.getEventKeyState()) continue;
			
			// Switch textures depending on the key released
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_1:
				textureSelector = 0;
				break;
			case Keyboard.KEY_2:
				textureSelector = 1;
				break;
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
		GL20.glUseProgram(pId);
		
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
		
		GL20.glUseProgram(pId);
		
		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);
		
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
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
		// Delete the texture
		GL11.glDeleteTextures(texIds[0]);
		GL11.glDeleteTextures(texIds[1]);
		
		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);
		
		GL20.glDeleteShader(vsId);
		GL20.glDeleteShader(fsId);
		GL20.glDeleteProgram(pId);
		
		// Select the VAO
		GL30.glBindVertexArray(vaoId);
		
		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		
		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		
		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);
		
		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
		
		this.exitOnGLError("destroyOpenGL");
		
		Display.destroy();
	}
	
	private int loadPNGTexture(String filename, int textureUnit) {
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
			
			
			// Decode the PNG file in a ByteBuffer
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