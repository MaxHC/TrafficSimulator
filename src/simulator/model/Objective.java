package simulator.model;


public class Objective extends Stop{

	public String name;

	public Objective(String name, int x, int y){
		super(x, y);
		this.name = name;
	}

	public String toString(){
		return this.name + " at coordinates : x=" + this.coordinates[0] + ", y=" + this.coordinates[1];
	}

}