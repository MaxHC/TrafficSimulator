package simulator.model;

import java.util.LinkedList;
import java.util.Map;

import simulator.policies.car.*;

public class Car{

	private double[] coordinates;
	private Objective finalObjective;
	private Stop currentObjective, nextObjective;
	private boolean reached, damaged, move;
	private Road currentRoad, nextRoad;
	private double speed;
	private Intersection currentIntersection;
	private CarPolicyMove movePolicy;
	private CarTrafficLightPolicy trafficLightPolicy;
	private CarStopPolicy stopPolicy;

	public Car(double x, double y, Objective finalObjective, double speed, CarTrafficLightPolicy trafficLightPolicy, CarStopPolicy stopPolicy){
		this.coordinates = new double[2];
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.finalObjective = finalObjective;
		this.reached = false;
		this.damaged = false;
		this.speed = speed;
		this.move = true;
		this.trafficLightPolicy = trafficLightPolicy;
		this.stopPolicy = stopPolicy;
	}

	public String toString(){
		return "car at coordinates (" + this.coordinates[0] + ", " + this.coordinates[1] +") on " + this.currentRoad + " with final Objective " + this.finalObjective;
	}

	public void addPolicy(CarPolicyMove movePolicy){
		this.movePolicy = movePolicy;
		this.movePolicy.addCar(this);
		if(this.currentObjective == null){
			this.getNextObjectives();
			this.nextObjectives();
		}
	}

	public double[] getCoordinates(){
		return this.coordinates;
	}

	public void setCurrentRoad(Road road){
		this.currentRoad = road;
	}

	public Road getCurrentRoad(){
		return this.currentRoad;
	}

	public Intersection getCurrentIntersection(){
		return this.currentIntersection;
	}

	public Stop getCurrentObjective(){
		return this.currentObjective;
	}

	public Objective getFinalObjective(){
		return this.finalObjective;
	}

	public void changeFinalObjective(Objective finalObjective){
		this.finalObjective = finalObjective;
	}

	public void getNextObjectives() {
		if (!(this.currentObjective instanceof Objective)) {
			Map.Entry<Stop, Road> next = this.movePolicy.getNextObjective();
			this.nextObjective = next.getKey();
			this.nextRoad = next.getValue();
		}
	}

	//return is there is a next objective
	private boolean nextObjectives(){
		this.currentObjective = this.nextObjective;
		this.currentRoad = this.nextRoad;

		getNextObjectives();
		return (this.currentObjective!=null?true:false);
	}

	public void update(int trafficLightDistance){
		int[] objectiveCoordinate;
		if (this.move) { //if car isn't stoped
			double[] vector = this.currentRoad.getVector();
			this.coordinates[0] += vector[0] * this.speed;
			this.coordinates[1] += vector[1] * this.speed;


			objectiveCoordinate = this.currentObjective.getCoordinates();

			//objective block
			for (Intersection inter: this.currentRoad.getIntersections()) {
				if (this.currentObjective instanceof Objective || inter != (Intersection) this.currentObjective)
					if ((int) this.coordinates[0] == inter.getCoordinates()[0] - this.currentRoad.getVector()[0] * trafficLightDistance && (int) this.coordinates[1] == inter.getCoordinates()[1] - this.currentRoad.getVector()[1] * trafficLightDistance)
						this.currentIntersection = inter;

			}

			//traffic light gestion block
			if (this.currentIntersection != null) {
				int lightState = this.currentIntersection.lights.get(this.currentRoad).get(this.currentRoad).getLight();
				if (!this.trafficLightPolicy.policy(lightState)) { //ask traffic light policy
					this.move = false;
					return;
				} else {
					this.move = true;
					this.currentIntersection = null;
				}
			}

			//road changement block
			if (this.currentObjective instanceof Intersection) {
				if ((int) this.coordinates[0] == objectiveCoordinate[0] - this.currentRoad.getVector()[0] * trafficLightDistance && (int) this.coordinates[1] == objectiveCoordinate[1] - this.currentRoad.getVector()[1] * trafficLightDistance) {
					int lightState = ((Intersection) this.currentObjective).lights.get(this.currentRoad).get(this.nextRoad).getLight();
					if (!this.trafficLightPolicy.policy(lightState)) {
						this.move = false;
						this.currentIntersection = (Intersection) this.currentObjective;
					}
				} else if ((int) this.coordinates[0] == objectiveCoordinate[0] && (int) this.coordinates[1] == objectiveCoordinate[1]) {
					this.nextObjectives();
				}
			} else {
				if ((int) this.coordinates[0] == objectiveCoordinate[0] && (int) this.coordinates[1] == objectiveCoordinate[1]) {
					this.reached = true;
				}
			}
		} else {
			if (currentIntersection.lights.get(this.currentRoad).get(this.currentRoad).getLight() != 0) { //prevent from null intersection caused by stoped from accident instead of trafficlight
				this.move = true;
				this.currentIntersection = null;
				this.update(trafficLightDistance); //recall update to don't lose 1 tick on move update
			}
		}
	}

	public boolean getStopPolicy(int trafficLightDistance){
		return this.stopPolicy.move(this.coordinates, currentRoad, trafficLightDistance);
	}

	public void stop(){
		this.move = false;
	}

	public boolean isMoving(){
		return this.move;
	}

	public boolean hasFinish(){
		return this.reached;
	}

	public boolean isDamaged(){
		return this.damaged;
	}

	public void hasAccident(){
		this.damaged = true;
	}

}