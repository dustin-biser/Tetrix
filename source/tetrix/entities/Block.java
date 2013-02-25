package tetrix.entities;

public class Block {
	private static Block blockInstance;
	private float halfWidth;
	
	float[] vertices = {
		   -halfWidth,  halfWidth, 0f,	// Left top
		   -halfWidth, -halfWidth, 0f,	// Left bottom
			halfWidth, -halfWidth, 0f,	// Right bottom
			halfWidth,  halfWidth, 0f	// Right top
	};
	
	private Block(){
		halfWidth = 0.5f;
	}
	
	public static Block getInstance(){
		if(blockInstance == null)
			blockInstance = new Block();
		
		return blockInstance;
	}
	
	public float[] getVertices(){
		return vertices;
	}
	
	public void setHalfWidth(float halfWidth){
		this.halfWidth = halfWidth;
	}
	
}
