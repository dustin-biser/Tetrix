package tetrix.entities.rotations;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import tetrix.entities.Direction;
import tetrix.entities.shapes.Shape;
import tetrix.entities.shapes.ShapeType;

public class SuperRotationSystem extends RotationSystem {
	
	public SuperRotationSystem(){
		this.shapeRotationResponses = new
				HashMap<ShapeType, Map<RotationTransition, TranslationResponse>>();
		
		computeIShapeResponses();
		computeJShapeReponses();
		computeLShapeReponses();
		computeOShapeReponses();
		computeSShapeReponses();
		computeTShapeReponses();
		computeZShapeReponses();
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
	
	/*
	 * Shape I
	 * RotationStates: 
	 *     O          R          2          L
	 * |-|-|-|-|  |-|-|A|-|  |-|-|-|-|  |-|D|-|-|
	 * |A|B|C|D|  |-|-|B|-|  |-|-|-|-|  |-|C|-|-|
	 * |-|-|-|-|  |-|-|C|-|  |D|C|B|A|  |-|B|-|-|
	 * |-|-|-|-|  |-|-|D|-|  |-|-|-|-|  |-|A|-|-|
	 * 
	 * +col (right), +row (up) 
	 */
	private void computeIShapeResponses(){
		Map<RotationTransition, TranslationResponse> responseMap =
				new HashMap<RotationTransition, TranslationResponse>();
		
		TranslationResponse translationResponse;
		
		// OtoR and RtoO
		translationResponse = new TranslationResponse().translateA( 2,  1)
													   .translateB( 1,  0)
													   .translateC( 0, -1)
													   .translateD(-1, -2);
		responseMap.put(RotationTransition.OtoR, translationResponse);
		responseMap.put(RotationTransition.RtoO, translationResponse.getInverse());
		
		
		// RtoTwo and TwotoR
		translationResponse = new TranslationResponse().translateA( 1, -2)
													   .translateB( 0, -1)
													   .translateC(-1,  0)
													   .translateD(-2,  1);
		responseMap.put(RotationTransition.RtoTwo, translationResponse);
		responseMap.put(RotationTransition.TwotoR, translationResponse.getInverse());
		
		// TwotoL and LtoTwo 
		translationResponse = new TranslationResponse().translateA(-2, -1)
													   .translateB(-1,  0)
													   .translateC( 0,  1)
													   .translateD( 1,  2);
		responseMap.put(RotationTransition.TwotoL, translationResponse);
		responseMap.put(RotationTransition.LtoTwo, translationResponse.getInverse());
		
		// LtoO and OtoL
		translationResponse = new TranslationResponse().translateA(-1,  2)
													   .translateB( 0,  1)
													   .translateC( 1,  0)
													   .translateD( 2, -1);
		responseMap.put(RotationTransition.LtoO, translationResponse);
		responseMap.put(RotationTransition.OtoL, translationResponse.getInverse());
		
		this.shapeRotationResponses.put(ShapeType.I, responseMap);
	}

	/*
	 * Shape J
	 * RotationStates: 
	 *    O         R         2         L
	 * |A|-|-|   |-|B|A|   |-|-|-|   |-|D|-|  
	 * |B|C|D|   |-|C|-|   |D|C|B|   |-|C|-|  
	 * |-|-|-|   |-|D|-|   |-|-|A|   |A|B|-|  
	 * 
	 * +col (right), +row (up) 
	 */
	private void computeJShapeReponses() {
		Map<RotationTransition, TranslationResponse> responseMap =
				new HashMap<RotationTransition, TranslationResponse>();
		
		TranslationResponse translationResponse;
		
		// OtoR and RtoO translation responses.
		translationResponse = new TranslationResponse().translateA(  2,  0)
													   .translateB(  1,  1)
													   .translateD( -1, -1);
		responseMap.put(RotationTransition.OtoR, translationResponse);
		responseMap.put(RotationTransition.RtoO, translationResponse.getInverse());
		
		
		// RtoTwo and TwotoR translation responses.
		translationResponse = new TranslationResponse().translateA( 0, -2)
													   .translateB( 1, -1)
													   .translateD(-1,  1);
		responseMap.put(RotationTransition.RtoTwo, translationResponse);
		responseMap.put(RotationTransition.TwotoR, translationResponse.getInverse());
		
		// TwotoL and LtoTwo translation responses.
		translationResponse = new TranslationResponse().translateA(-2,  0)
													   .translateB(-1, -1)
													   .translateD( 1,  1);
		responseMap.put(RotationTransition.TwotoL, translationResponse);
		responseMap.put(RotationTransition.LtoTwo, translationResponse.getInverse());
		
		// LtoO and OtoL translation responses.
		translationResponse = new TranslationResponse().translateA( 0,  2)
													   .translateB(-1,  1)
													   .translateD( 1, -1);
		responseMap.put(RotationTransition.LtoO, translationResponse);
		responseMap.put(RotationTransition.OtoL, translationResponse.getInverse());
		
		this.shapeRotationResponses.put(ShapeType.J, responseMap);
	}
	
	/*
	 * Shape L
	 * RotationStates: 
	 *    O         R         2         L
	 * |-|-|D|   |-|A|-|   |-|-|-|   |D|C|-|  
	 * |A|B|C|   |-|B|-|   |C|B|A|   |-|B|-|  
	 * |-|-|-|   |-|C|D|   |D|-|-|   |-|A|-|  
	 * 
	 * +col (right), +row (up) 
	 */
	private void computeLShapeReponses() {
		Map<RotationTransition, TranslationResponse> responseMap =
				new HashMap<RotationTransition, TranslationResponse>();
		
		TranslationResponse translationResponse;
		
		// OtoR and RtoO translation responses.
		translationResponse = new TranslationResponse().translateA( 1,  1)
													   .translateC(-1, -1)
													   .translateD( 0, -2);
		responseMap.put(RotationTransition.OtoR, translationResponse);
		responseMap.put(RotationTransition.RtoO, translationResponse.getInverse());
		
		
		// RtoTwo and TwotoR translation responses.
		translationResponse = new TranslationResponse().translateA( 1, -1)
													   .translateC(-1,  1)
													   .translateD(-2,  0);
		responseMap.put(RotationTransition.RtoTwo, translationResponse);
		responseMap.put(RotationTransition.TwotoR, translationResponse.getInverse());
		
		// TwotoL and LtoTwo translation responses.
		translationResponse = new TranslationResponse().translateA(-1, -1)
													   .translateC( 1,  1)
													   .translateD( 0,  2);
		responseMap.put(RotationTransition.TwotoL, translationResponse);
		responseMap.put(RotationTransition.LtoTwo, translationResponse.getInverse());
		
		// LtoO and OtoL translation responses.
		translationResponse = new TranslationResponse().translateA(-1,  1)
													   .translateC( 1, -1)
													   .translateD( 2,  0);
		responseMap.put(RotationTransition.LtoO, translationResponse);
		responseMap.put(RotationTransition.OtoL, translationResponse.getInverse());
		
		this.shapeRotationResponses.put(ShapeType.L, responseMap);
		
	}
	
	private void computeOShapeReponses() {
		// TODO Auto-generated method stub
		
	}
	
	private void computeSShapeReponses() {
		// TODO Auto-generated method stub
		
	}
	
	private void computeTShapeReponses() {
		// TODO Auto-generated method stub
		
	}
	
	private void computeZShapeReponses() {
		// TODO Auto-generated method stub
	}





}
