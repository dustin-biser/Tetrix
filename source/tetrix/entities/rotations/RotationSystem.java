package tetrix.entities.rotations;

import java.util.HashMap;
import java.util.Map;

import tetrix.entities.shapes.ShapeType;

public class RotationSystem {

	private Map<ShapeType, Map<RotationTransition, TranslationResponse>> shapeRotationResponses;
}
