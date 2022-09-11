package simulator.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Road{

	private Set<Objective> objectives; //objectives like buildings on the road
	private int[] start, end;
	private double[] vector;
	private ArrayList<Intersection> intersections; //intersection with this road
	private ArrayList<Stop> stops;
	private boolean twoWays;
	public Road reverse;

	public Road(int startx, int starty, int endx, int endy, boolean twoWays){
		this.start = new int[2];
		this.start[0] = startx;
		this.start[1] = starty;
		this.end = new int[2];
		this.end[0] = endx;
		this.end[1] = endy;
		this.objectives = new HashSet<>();
		this.vector = new double[2];
		this.calculateVector();
		this.intersections = new ArrayList<>();
		this.stops = new ArrayList<>();
		this.twoWays = twoWays;
		this.reverse = null;
	}

	private void calculateVector(){
		this.vector[0] = this.end[0] - this.start[0];
		this.vector[1] = this.end[1] - this.start[1];
		double standard = Math.sqrt(this.vector[0]*this.vector[0] + this.vector[1]*this.vector[1]);
		this.vector[0] /= standard;
		this.vector[1] /= standard;
	}

	public boolean isOnRoad(int[] point){
		double[] ca = new double[2];
		double[] cb = new double[2];

		ca[0] = this.start[0] - point[0];
		ca[1] = this.start[1] - point[1];
		cb[0] = this.end[0] - point[0];
		cb[1] = this.end[1] - point[1];

		double prodVect = this.vector[0]*(-ca[1])-(-ca[0])*this.vector[1]; //->AB^->AC = ABx*ACy-ACx*ABy (AC = -CA)
		if(prodVect == 0){ //if prodVect == 0 <=> ABC aligne
			double prodScal = ca[0]*cb[0]+ca[1]*cb[1]; //->CA.->CB = CAx*CBx+CAy*CBy (xx'+yy')
			if(prodScal <= 0){ //if prodScal <= 0 C between A and B
				return true;
			}
		}

		return false;
	}

	public boolean isOnRoad(double[] point){
		int[] intCoord = new int[2];
		intCoord[0] = (int) point[0];
		intCoord[1] = (int) point[1];

		return this.isOnRoad(intCoord);
	}

	public int[] getStart(){
		return this.start;
	}

	public int[] getEnd(){
		return this.end;
	}

	public void addObjective(Objective objective){
		this.stops.add(objective);
		this.objectives.add(objective);
	}

	public void removeObjectives(Objective objective){
		this.objectives.remove(objective);
	}

	public Set<Objective> getObjectives(){
		return this.objectives;
	}

	public double[] getVector(){
		return this.vector;
	}

	//calculate if there is an intersection with another road
	public int[] intersection(Road road){
		int[] intersection = new int[2];
		int[] start2 = road.getStart();
		int[] end2 = road.getEnd();

		int deno = (end2[1] - start2[1])*(this.end[0] - this.start[0]) - (end2[0] - start2[0])*(this.end[1] - this.start[1]);
		int nume1 = (end2[0] - start2[0])*(this.start[1] - start2[1]) - (end2[1] - start2[1])*(this.start[0] - start2[0]);
		int nume2 = (this.end[0] - this.start[0])*(this.start[1] - start2[1]) - (this.end[1] - this.start[1])*(this.start[0] - start2[0]);

		if (deno == 0) {
			return null;
		}
		float ua = (float) nume1 / (float) deno;
		float ub = (float) nume2 / (float) deno;

		if (0<=ua && ua<=1 && 0<=ub && ub<=1) {
			intersection[0] = (int) (this.start[0] + ua*(this.end[0]-this.start[0]));
			intersection[1] = (int) (this.start[1] + ua*(this.end[1]-this.start[1]));
			return intersection;
		}
		return null;
	}

	public void addIntersection(Intersection intersection) {
		this.intersections.add(intersection);
		this.stops.add(intersection);
	}

	public ArrayList<Intersection> getIntersections() {
		return this.intersections;
	}

	public ArrayList<Stop> getStops() { return this.stops; }

	public boolean getTwoWays() { return this.twoWays; }

	public String toString(){
		return "Road starting at (" + this.start[0] + "," + this.start[1] + "), ending at (" + this.end[0] + "," + this.end[1] + ")";
	}

}