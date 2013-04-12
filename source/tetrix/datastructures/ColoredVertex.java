package tetrix.datastructures;

public class ColoredVertex extends Vertex {
	// Vertex data
	protected float[] rgba = new float[] {1f, 1f, 1f, 1f};
	
	// Elements per parameter
	public static final int colorElementCount = 4;
	
	// Bytes per parameter
	public static final int colorByteCount = colorElementCount * elementBytes;
	
	// Byte offsets per parameter
	public static final int colorByteOffset = positionByteOffset + positionByteCount;
	
	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount;	
	
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionByteCount + colorByteCount;
	
	// Setters
	public void setRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1f);
	}
	
	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] {r, g, b, 1f};
	}
	
	// Constrution Helpers
	public Vertex withRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1f);
		return this;
	}
	public Vertex withRGBA(float r, float g, float b, float a) {
		this.setRGBA(r, g, b, a);
		return this;
	}
	
	// Getters	
	public float[] getElements() {
		float[] out = new float[ColoredVertex.elementCount];
		int i = 0;
		
		// Insert XYZW elements
		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		// Insert RGBA elements
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		
		return out;
	}
	
	public float[] getRGBA() {
		return new float[] {this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3]};
	}
}
