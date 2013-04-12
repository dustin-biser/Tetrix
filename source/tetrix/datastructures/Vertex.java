package tetrix.datastructures;

public class Vertex {
	// Vertex data
	protected float[] xyzw = new float[] {0f, 0f, 0f, 1f};
	
	// The amount of bytes an element has
	public static final int elementBytes = 4;
	
	// Elements per parameter
	public static final int positionElementCount = 4;
	
	// Bytes per parameter
	public static final int positionByteCount = positionElementCount * elementBytes;
	
	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	
	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount;
	
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionByteCount;
	
	// Setters
	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
	}
	
	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] {x, y, z, w};
	}
	
	// Constrution Helpers
	public Vertex withXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
		return this;
	}
	
	public Vertex withXYZW(float x, float y, float z, float w) {
		this.setXYZW(x, y, z, w);
		return this;
	}
	
	// Getters	
	public float[] getElements() {
		float[] out = new float[Vertex.elementCount];
		int i = 0;
		
		// Insert XYZW elements
		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		
		return out;
	}
	
	public float[] getXYZ() {
		return new float[] {this.xyzw[0], this.xyzw[1], this.xyzw[2]};
	}
	
	public float[] getXYZW() {
		return new float[] {this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3]};
	}
}
