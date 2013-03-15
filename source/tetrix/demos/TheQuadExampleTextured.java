package tetrix.demos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import tetrix.datastructures.TexturedVertex;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TheQuadExampleTextured {
	// Entry point for the application
	public static void main(String[] args) {
		new TheQuadExampleTextured();
	}
	
	// Setup variables
	private final String WINDOW_TITLE = "The Quad: Textured";
	private final int WIDTH = 1024;
	private final int HEIGHT = 768;
	// Quad variables
	private int vaoId = 0;
	private int vboId = 0;
	private int vboiId = 0;
	private int indicesCount = 0;
	// Shader variables
	private int vsId = 0;
	private int fsId = 0;
	private int pId = 0;
	// Texture variables
	private int[] texIds = new int[] {0, 0};
	private int textureSelector = 0;
	
	public TheQuadExampleTextured() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		
		this.setupQuad();
		this.setupShaders();
		this.setupTextures();
		
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

	private void setupTextures() {
		texIds[0] = this.loadPNGTexture("resources/textures/stGrid1.png", GL_TEXTURE0);
		texIds[1] = this.loadPNGTexture("resources/textures/stGrid2.png", GL_TEXTURE0);
		
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
			
			glViewport(0, 0, WIDTH, HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Setup an XNA like background color
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, WIDTH, HEIGHT);
		
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
		
		TexturedVertex[] vertices = new TexturedVertex[] {v0, v1, v2, v3};
		
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
				TexturedVertex.elementCount);
		
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		
		verticesBuffer.flip();	
		
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
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
		// Put the color components in attribute list 1
		glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.textureByteOffset);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		this.exitOnGLError("setupQuad");
	}
	
	private void setupShaders() {		
		// Load the vertex shader
		vsId = this.loadShader("source/tetrix/shaders/vertex.glsl", GL_VERTEX_SHADER);
		// Load the fragment shader
		fsId = this.loadShader("source/tetrix/shaders/fragment.glsl", GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		pId = glCreateProgram();
		glAttachShader(pId, vsId);
		glAttachShader(pId, fsId);
		glLinkProgram(pId);

		// Position information will be attribute 0
		glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		glBindAttribLocation(pId, 1, "in_Color");
		// Textute information will be attribute 2
		glBindAttribLocation(pId, 2, "in_TextureCoord");
		
		glValidateProgram(pId);
		
		this.exitOnGLError("setupShaders");
	}
	
	private void loopCycle() {
		// Logic
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
		}
		
		// Render
		glClear(GL_COLOR_BUFFER_BIT);
		
		glUseProgram(pId);
		
		// Bind the texture
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texIds[textureSelector]);
		
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		
		// Draw the vertices
		glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		glUseProgram(0);
		
		this.exitOnGLError("loopCycle");
	}
	
	private void destroyOpenGL() {	
		// Delete the texture
		glDeleteTextures(texIds[0]);
		glDeleteTextures(texIds[1]);
		
		// Delete the shaders
		glUseProgram(0);
		glDetachShader(pId, vsId);
		glDetachShader(pId, fsId);
		
		glDeleteShader(vsId);
		glDeleteShader(fsId);
		glDeleteProgram(pId);
		
		// Select the VAO
		glBindVertexArray(vaoId);
		
		// Disable the VBO index from the VAO attributes list
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		// Delete the vertex VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboId);
		
		// Delete the index VBO
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboiId);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		
		this.exitOnGLError("destroyOpenGL");
		
		Display.destroy();
	}
	
	private int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		this.exitOnGLError("loadShader");
		
		return shaderID;
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
		int texId = glGenTextures();
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tWidth, tHeight, 0, 
				GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, 
				GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, 
				GL_LINEAR_MIPMAP_LINEAR);
		
		this.exitOnGLError("loadPNGTexture");
		
		return texId;
	}
	
	private void exitOnGLError(String errorMessage) {
		int errorValue = glGetError();
		
		if (errorValue != GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
}
