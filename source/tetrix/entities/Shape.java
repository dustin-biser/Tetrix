package tetrix.entities;

public abstract class Shape {
	protected final ShapeConfiguration[] configurations;
	
	public Shape(ShapeConfiguration[] configurations){
		this.configurations = configurations;
	}
	
	public ShapeConfiguration[] getShapeConfigurations(){
		return (configurations == null) ? null : configurations;
	}
}
