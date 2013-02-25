package tetrix.entities;

public class ConcreteShape extends Shape{
	
	private static final ShapeConfiguration config1 =
			new ShapeConfiguration(2,0, 2,1, 1,1, 1,2);
	
	private static final ShapeConfiguration config2 =
			new ShapeConfiguration(0,0, 1,0, 1,1, 2,1);
	
	private static final ShapeConfiguration[] configurations = {config1, config2};
	
	public ConcreteShape(){
		super(configurations);
	}
}
