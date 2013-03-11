package tetrix.entities.rotations;

import static tetrix.entities.rotations.RotationState.*;

import java.security.InvalidParameterException;

/**
 * Enumeration of all possible RotationTransitions that a Shape can undergo.  A
 * RotationTransition can be thought of has having of a start
 * <code>RotationState</code> and an end <code>RotationState</code> that defines
 * the transition.
 * @see RotationState
 * 
 * @author Dustin Biser
 *
 */
public enum RotationTransition {
	OtoR("SpawnState->RightOfSpawn"),
	RtoO("RightOfSpawn->SpawnState"),
	RtoTwo("RightOfSpawn->SecondRotation"), 
	TwotoR("SecondRotation->RightOfSpawn"),
	TwotoL("SecondRotation->LeftOfSpawn"),
	LtoTwo("LeftOfSpawn->SecondRotation"),
	LtoO("LeftOfSpawn->SpawnState"),
	OtoL("SpawnState->LeftOfSpawn");	
	
	private String descriptor;
	
	private RotationTransition(String description) {
		this.descriptor = description;
	}
	
	/**
	 * Returns the RotationTransition that begins with <code>startState</code> and
	 * ends with <code>endState</code>.  A valid RotationTransition is one such that
	 * <p>
	 * <code>endState == startState.left() || endState == startState.right()</code>
	 * </p>
	 * If the arguments passed do not satisfy the condition above,
	 * then an <code>InvalidParameterException</code> is thrown.
	 * 
	 * @param startState - RotationState before the rotation.
	 * @param endState - RotationState after the rotation.
	 * @return RotationTransition
	 */
	public static RotationTransition getTransition(RotationState startState,
			RotationState endState) throws InvalidParameterException {
		
		if (startState == SPAWN_STATE){
			if (endState == LEFT_OF_SPAWN){
				return OtoL;
			}
			else if (endState == RIGHT_OF_SPAWN){
				return OtoR;
			}
		}
		else if (startState == RIGHT_OF_SPAWN){
			if (endState == SPAWN_STATE){
				return RtoO;
			}
			else if (endState == SECOND_ROTATION){
				return RtoTwo;
			}
		}
		else if (startState == SECOND_ROTATION){
			if (endState == RIGHT_OF_SPAWN){
				return TwotoR;
			}
			else if (endState == LEFT_OF_SPAWN){
				return TwotoL;
			}
		}
		else if (startState == LEFT_OF_SPAWN){
			if (endState == SECOND_ROTATION){
				return LtoTwo;
			}
			else if (endState == SPAWN_STATE){
				return LtoO;
			}
		}
		
		throw new InvalidParameterException("Invalid parameter. " +
				"endState must equal startState.left() or startState.right().");
	}
	
	
	/**
	 * @return a String description of the RotationTransition.
	 */
	public String toString(){
		return descriptor;
	}
}
