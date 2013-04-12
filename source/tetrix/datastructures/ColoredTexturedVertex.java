package tetrix.datastructures;

public class ColoredTexturedVertex extends ColoredVertex {
	// Vertex data
	private float[] st = new float[] {0f, 0f};
	
	// Elements per parameter
	public static final int textureElementCount = 2;
	
	// Bytes per parameter
	public static final int textureByteCount = textureElementCount * elementBytes;
	
	// Byte offsets per parameter
	public static final int textureByteOffset = colorByteOffset + colorByteCount;
	
	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount +
			textureElementCount;	
	
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionByteCount + colorByteCount +
			textureByteCount;
	
	public void setST(float s, float t) {
		this.st = new float[] {s, t};
	}
	
	// Constrution Helpers
	public ColoredTexturedVertex withST(float s, float t) {
		this.setST(s, t);
		return this;
	}
	
	// Getters	
	public float[] getElements() {
		float[] out = new float[ColoredTexturedVertex.elementCount];
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
		// Insert ST elements
		out[i++] = this.st[0];
		out[i++] = this.st[1];
		
		return out;
	}
	
	public float[] getST() {
		return new float[] {this.st[0], this.st[1]};
	}
}
