package tetrix.entities.rotations;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import tetrix.entities.Direction;
import tetrix.entities.shapes.Shape;
import tetrix.entities.shapes.ShapeType;

public class SuperRotationSystem extends RotationSystem {
	
	public SuperRotationSystem(){
		int initialCapacity = ShapeType.values().length + 1;
		float loadFactor = 1.0f;
		
		// Allocate a new HashMap with initialCapacity and loadFactor so that it does
		// not double in size when 7 ShapeType keys are added to it.
		shapeRotationResponses = new
				HashMap<ShapeType, Map<RotationTransition, TranslationResponse>>(initialCapacity, loadFactor);
		
		initializeRotationResponses();
	}
	
	private void initializeRotationResponses(){
		shapeRotationResponses.put(key, value)
	}

	@Override
	public void rotate(Shape shape, Direction direction) throws InvalidParameterException {
		RotationState startState = shape.getRotationState();
		RotationState endState;

		switch (direction) {
		case LEFT:
			endState = startState.left();
			break;
		case RIGHT:
			endState = startState.right();
			break;
		default:
			throw new InvalidParameterException("Invalid Direction argument." +
					"Direction must equal LEFT or RIGHT.\n");
		}

		ShapeType shapeType = shape.getShapeType();

		Map<RotationTransition, TranslationResponse> responseMap =
				shapeRotationResponses.get(shapeType);

		RotationTransition transition =
				RotationTransition.getTransition(startState, endState);

		TranslationResponse response = responseMap.get(transition);
		
		response.translate(shape);

		shape.setRotationState(endState);
	}
}
