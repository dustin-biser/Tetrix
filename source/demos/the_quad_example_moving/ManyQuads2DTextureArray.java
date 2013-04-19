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
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tetrix.datastructures.TexturedVertex;
import tetrix.utilities.GLUtils;
import tetrix.utilities.ShaderUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;


/**
 * Goal of this demo is to render multiple textured Quads in a single
 * glDrawElements(...) call.  Will make use of:
 * - 1 Texture Image Unit
 * - 1 VAO,
 * - 7 VBOS (one VBO per Quad).
 * 
 * @author Dustin Biser
 *
 */
public class ManyQuads2DTextureArray {
	// Entry point for the application
	public static void main(String[] args) {
		new ManyQuads2DTextureArray();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "Many Moving Quads with Texture Array";
	private final int WIDTH = 600;
	private final int HEIGHT = 600;
	private final double PI = 3.14159265358979323846;
	private final int numQuads = 7;
	
	// Quad variables
	private int vao;
	private int[] vboList = new int[3];
	private int indicesVboId;
	private int indicesCount;
	
	// Shader variables
	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;
	
	// Texture object variable
	private int texId;
	
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
	
	public ManyQuads2DTextureArray() {
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
		loadPNGTextures();
		
		// Set the active texture image unit.
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		// Set texture sampler to use Texture Image Unit Index 0.
		int textureSamplerLoc = GL20.glGetUniformLocation(programId, "texture_diffuse");
		GL20.glUseProgram(programId);
		GL20.glUniform1i(textureSamplerLoc, 0);
		
		// Restore Defaults
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
		GL20.glUseProgram(0);
		
		GLUtils.exitOnGLError("setupTexture");
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
		
		GLUtils.exitOnGLError("setupOpenGL");
	}
	
	private void setupQuads() {
		// Create an array of FloatBuffers, where each FloatBuffer contains the
		// vertices for one quad.
		FloatBuffer[] vertexBuffers = createQuadVertexBuffers();
		
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		// Upload Position Data
		vboList[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboList[0]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffers[0], GL15.GL_STREAM_DRAW);
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount,
				GL11.GL_FLOAT, false, 0, 0);

		// Upload Color Data
		vboList[1] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboList[1]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffers[1],
				GL15.GL_STREAM_DRAW);
		// Put the color coordinates in attribute list 1
		GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount,
				GL11.GL_FLOAT, false, 0, 0);

		// Upload Texture Data
		vboList[2] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboList[2]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffers[2],
				GL15.GL_STREAM_DRAW);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount+1,
				GL11.GL_FLOAT, false, 0, 0);
		
		// Vertex indices for tessellating each quad into 2 triangles, with
		// indices given in counter clockwise order.
		byte[] indices = new byte[6*numQuads];
		
		for(int i = 0; i < numQuads; i++){
			indices[i*6]   = (byte)(0 + i*4);
			indices[i*6+1] = (byte)(1 + i*4);
			indices[i*6+2] = (byte)(2 + i*4);
			indices[i*6+3] = (byte)(2 + i*4);
			indices[i*6+4] = (byte)(3 + i*4);
			indices[i*6+5] = (byte)(0 + i*4);
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
		
		GLUtils.exitOnGLError("setupQuad");
	}
	
	private FloatBuffer[] createQuadVertexBuffers(){
		FloatBuffer positionBuffer, colorBuffer, textureBuffer;
		
		TexturedVertex[] vertices;
		TexturedVertex topLeft = new TexturedVertex();
		TexturedVertex bottomLeft = new TexturedVertex();
		TexturedVertex bottomRight = new TexturedVertex();
		TexturedVertex topRight = new TexturedVertex();
		
		Vector3f topLeft_xyz;
		Vector3f bottomLeft_xyz;
		Vector3f bottomRight_xyz;
		Vector3f topRight_xyz;
		
			
		// Allocate enough space to conglomerate vertex data
		positionBuffer = BufferUtils.createFloatBuffer(TexturedVertex.positionByteCount*numQuads*4);
		colorBuffer = BufferUtils.createFloatBuffer(TexturedVertex.colorByteCount*numQuads*4);
		textureBuffer = BufferUtils.createFloatBuffer((TexturedVertex.textureByteCount+4)*numQuads*4);
		
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
			
			for (int j = 0; j < vertices.length; j++) {
				// Add position, color and texture floats to their respective buffers
				positionBuffer.put(vertices[j].getXYZW());
				colorBuffer.put(vertices[j].getRGBA());
				textureBuffer.put(vertices[j].getST());
				textureBuffer.put(i); // Texture array index
			}
			
		}
		// Done putting data in buffers, so get them ready for reading.
		positionBuffer.flip();
		colorBuffer.flip();
		textureBuffer.flip();
		
		return new FloatBuffer[] {positionBuffer, colorBuffer, textureBuffer};
	}
	
	
	private void setupShaders() {		
		// Load the vertex shader
		vertexShaderId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/vertexTextureArray.glsl", 
				GL20.GL_VERTEX_SHADER);
		
		// Load the fragment shader
		fragmentShaderId = ShaderUtils.loadShader("source/demos/the_quad_example_moving/fragmentTextureArray.glsl", 
				GL20.GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderId);
		GL20.glAttachShader(programId, fragmentShaderId);
		ShaderUtils.linkProgram(programId);

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
		
		GLUtils.exitOnGLError("setupShaders");
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
		
		GLUtils.exitOnGLError("logicCycle");
	}
	
	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL20.glUseProgram(programId);
		
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vao);
		
		// Bind the texture
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
		
		GL20.glEnableVertexAttribArray(0); // Position Data.
		GL20.glEnableVertexAttribArray(1); // Color Data.
		GL20.glEnableVertexAttribArray(2); // Texture Data.
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
		GL30.glBindVertexArray(0);
		
		GL20.glUseProgram(0);
		
		GLUtils.exitOnGLError("renderCycle");
	}
	
	private void loopCycle() {
		// Update logic
		this.logicCycle();
		// Update rendered frame
		this.renderCycle();
		
		GLUtils.exitOnGLError("loopCycle");
	}
	
	private void destroyOpenGL() {	
		// Delete the texture object.
		GL11.glDeleteTextures(texId);
		
		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);
		
		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);
		GL20.glDeleteProgram(programId);
		
		// Select the VAO
		GL30.glBindVertexArray(vao);
		
		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		for(int i = 0 ; i < vboList.length; i++){
			GL15.glDeleteBuffers(vboList[i]);
		}
			
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vao);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(indicesVboId);
		
		
		GLUtils.exitOnGLError("destroyOpenGL");
		
		Display.destroy();
	}
	
	private void loadPNGTextures() {
		ByteBuffer buf = null;
		int textureWidth = 0;
		int textureHeight = 0;
		
		texId = GL11.glGenTextures();
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte.
		// Alignment setting used for sending data to OpenGL.
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		String[] textureFileStrings = new String[] {
				"resources/textures/blue-block.png",
				"resources/textures/purple-block.png",
				"resources/textures/cyan-block.png",
				"resources/textures/green-block.png",
				"resources/textures/orange-block.png",
				"resources/textures/red-block.png",
				"resources/textures/yellow-block.png" };
		
		InputStream in;
		PNGDecoder decoder;
		
		try {
			in = new FileInputStream(textureFileStrings[0]);
			decoder = new PNGDecoder(in);
			
			textureWidth = decoder.getWidth();
			textureHeight = decoder.getHeight();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGB, textureWidth,
				textureHeight, numQuads, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer)null);
		
		for(int textureDepth = 0; textureDepth < textureFileStrings.length; textureDepth++){
			try {
				// Open the PNG file as an InputStream
				in = new FileInputStream(textureFileStrings[textureDepth]);
				// Link the PNG decoder to this stream
				decoder = new PNGDecoder(in);
				
				
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
			
			GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, textureDepth,
					textureWidth, textureHeight, 1, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, buf);
		}
		
		GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		// Reset defaults.
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
		
		GLUtils.exitOnGLError("loadPNGTexture");
	}
	
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
}