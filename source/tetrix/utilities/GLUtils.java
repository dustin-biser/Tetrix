package tetrix.utilities;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

public class GLUtils {
	public static void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
	
	public static Matrix4f createProjectionMatrix(float aspectRatio, float fieldOfView,
			float near_plane, float far_plane){
		// Setup projection matrix
		Matrix4f projectionMatrix = new Matrix4f();
		
		float y_scale = coTangent(degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		
		return projectionMatrix;
	}
	
	/**
	 * Creates an orthographic projection matrix with specified parameters.
	 * @param left - minimum x direction for the x bounding region. 
	 * @param right - maximum x direction for the x bounding region.
	 * @param bottom - minimum y direction for the y bounding region.
	 * @param top - maximum y direction for the y bounding region.
	 * @param near - minimum z direction for the z bounding region.
	 * @param far - maximum z direction for the z bounding region.
	 * @return Matrix4f representing the orthographic projection matrix.
	 */
	public static Matrix4f createOrthoProjectionMatrix(float left, float right,
			float bottom, float top, float near, float far){
		
		// Initialized to indenity.
		Matrix4f projectionMatrix = new Matrix4f();
		
		// Set scaling components for x, y, and z directions.
		projectionMatrix.m00 = 2 / (right - left);
		projectionMatrix.m11 = 2 / (top - bottom);
		projectionMatrix.m22 = -2 / (far - near);
		
		// Set translation components.
		projectionMatrix.m03 = -1 * (right + left) / (right - left);
		projectionMatrix.m13 = -1 * (top + bottom) / (top - bottom);
		projectionMatrix.m23 = -1 * (far + near) / (far - near);
		
		return projectionMatrix;
	}
	
	private static float coTangent(float radians){
		return (float)(1 / Math.tan(radians));
	}
	
	private static float degreesToRadians(float degrees){
		return degrees * (float)(Math.PI / 180f);
	}
}
