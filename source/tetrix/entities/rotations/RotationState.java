package tetrix.entities.rotations;

public enum RotationState {
	SpawnState, RightOfSpawn, SecondRotation, LeftOfSpawn;
	
	public RotationState right(){
		return values()[ (ordinal() + 1) % values().length];
	}
	
	public RotationState left(){
		int index = ordinal() - 1;
		if ( index < 0) index = values().length - 1; 
		
		return values()[index];
	}
}
