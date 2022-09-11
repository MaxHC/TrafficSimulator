package simulator.model;

import simulator.utils.AbstractModeleEcoutable;

import java.util.*;

public class SimulatorMap extends AbstractModeleEcoutable {
	
	private ArrayList<Road> roads;
	public ArrayList<Intersection> intersections;
	private Set<Car> cars;
	private Set<Objective> objectives;
	private HashMap<Intersection,HashMap<Intersection,Road>> graph;
	private HashSet<TrafficLight> lights;
	private int nbAccident, timeWait; //time wait in ticks

	public SimulatorMap(ArrayList<Road> roads, Set<Car> cars, Set<Objective> objectives){
		this.roads = roads;
		this.cars = cars;
		this.objectives = objectives;
		this.intersections = new ArrayList<>();
		this.lights = new HashSet<>();
		this.nbAccident = 0;
		this.timeWait = 0;
	}

	public void initMap(){
		this.makeGraph();
		//assign the objectives on roads
		for(Objective objective: this.objectives){
			for(Road road: this.roads){
				if(road.isOnRoad(objective.getCoordinates())){
					road.addObjective(objective);
					if (road.getTwoWays())
						road.reverse.addObjective(objective);
				}
			}
		}

		//assign the cars on a road
		for(Car car: this.cars){
			for(Road road: this.roads){
				if(road.isOnRoad(car.getCoordinates())){
					car.setCurrentRoad(road);
				}
			}
		}
	}

	public void addAccident(){
		this.nbAccident ++;
	}

	public void isWaiting(){
		this.timeWait ++;
	}

	public int getNbAccident(){
		return this.nbAccident;
	}

	public int getTimeWait(){
		return this.timeWait;
	}

	public void addCar(Car car){
		this.cars.add(car);
	}

	public void removeCar(Car car){
		this.cars.remove(car);
	}

	public HashMap<Intersection,HashMap<Intersection,Road>> getGraph(){
		return this.graph;
	}

	public ArrayList<Road> getRoads(){
		return this.roads;
	}

	public Set<Car> getCars(){
		return this.cars;
	}

	public Set<Objective> getObjectives(){
		return this.objectives;
	}

	public HashSet<TrafficLight> getLights() {return this.lights; }

	public void addRoadToIntersection(Road road, Intersection intersection) {
		Road r;
		intersection.addConnectedRoad(road);
		road.addIntersection(intersection);

		if (road.getTwoWays()) {
			if (road.reverse != null)
				r = road.reverse;
			else
				r = new Road(road.getEnd()[0], road.getEnd()[1], road.getStart()[0], road.getStart()[1], true);
			road.reverse = r;
			r.reverse = road;
			intersection.addConnectedRoad(r);
			r.addIntersection(intersection);
		}
	}
	
	//create all intersection
	public void allIntersection() {
		this.intersections = new ArrayList<>();
		for (int i = 0; i<this.roads.size()-1; i++) {
			Road r0 = this.roads.get(i);
			for (int j = i+1; j<this.roads.size(); j++) {
				Road r1 = this.roads.get(j);
				int[] intersection = r0.intersection(r1);
				if (intersection != null) {
					boolean isNew = true;
					Intersection inter = null;

					for (Intersection in : this.intersections) {
						if (Arrays.equals(in.getCoordinates() ,intersection)) {
							isNew = false;
							inter = in;
							break;
						}
					}

					if (isNew) {
						inter = new Intersection(intersection[0], intersection[1]);
						this.intersections.add(inter);

						this.addRoadToIntersection(r0,inter);
						this.addRoadToIntersection(r1,inter);

					} else {
						if (!inter.getConnectedRoads().contains(r1)) {
							this.addRoadToIntersection(r1,inter);
						}
					}
				}
			}
		}
	}

	//return if the second object is in front of the first
	public boolean coordinateOnTheWay(Road road, int[] coordinate, int[] origine) {
		double[] vector = road.getVector();
		boolean x = (vector[0] >= 0) ? coordinate[0] >= origine[0] : coordinate[0] < origine[0];
		boolean y = (vector[1] >= 0) ? coordinate[1] >= origine[1] : coordinate[1] < origine[1];

		return x && y;
	}

	public boolean coordinateOnTheWay(Road road, double[] coordinate, double[] origine) {
		double[] vector = road.getVector();
		boolean x = (vector[0] >= 0) ? coordinate[0] >= origine[0] : coordinate[0] < origine[0];
		boolean y = (vector[1] >= 0) ? coordinate[1] >= origine[1] : coordinate[1] < origine[1];

		return x && y;
	}
	
	//transform map into graph to be used in planners
	public void makeGraph() {
		this.allIntersection();
		HashMap<Intersection,HashMap<Intersection,Road>> graph = new HashMap<>();
		for (Intersection in0 : this.intersections) {
			ArrayList<TrafficLight> lights = in0.plantTrafficLights();
			this.lights.addAll(lights);
			in0.getRoadOrder();
			in0.setUpLight();

			graph.put(in0, new HashMap<>());
			for (Intersection in1 : this.intersections) {
				if (in0 == in1)
					continue;
				for (Road road : in1.getConnectedRoads()) {
					if (in0.getConnectedRoads().contains(road)) {
						if (this.coordinateOnTheWay(road, in1.getCoordinates(), in0.getCoordinates())) {
							graph.get(in0).put(in1,road);
							break;
						}
					}
				}
			}
		}
		this.graph = graph;
	}

	//return the ca implies in accident (null if there is no accident)
	public Car carAccident(Car currentCar, int safetyDistance){
		for(Car car: this.cars){
			if(!car.hasFinish()){
				if(car != currentCar && currentCar.getCurrentRoad() == car.getCurrentRoad()){
					if(this.distance(currentCar.getCoordinates(), car.getCoordinates()) <= safetyDistance){
						if(this.coordinateOnTheWay(car.getCurrentRoad(), car.getCoordinates(), currentCar.getCoordinates())){
							return car;
						}
					}
				}
			}
		}
		return null;
	}

	//return if the next update will cause an accident
	public boolean incommingAccident(Car currentCar, int safetyDistance){
		double[] roadVector = currentCar.getCurrentRoad().getVector();
		double[] coordinates = new double[2];
		coordinates[0] = currentCar.getCoordinates()[0] + roadVector[0];
		coordinates[1] = currentCar.getCoordinates()[1] + roadVector[1];
		for(Car car: this.cars){
			if(!car.hasFinish()){
				if(car != currentCar && currentCar.getCurrentRoad() == car.getCurrentRoad()){
					if(this.distance(coordinates, car.getCoordinates()) <= safetyDistance){
						return true;
					}
				}
			}
		}
		return false;
	}

	//calculate the distance between 2 object
	public float distance(int[] coor1, int[] coor2) {
		return (float) Math.sqrt(Math.pow(coor1[0]-coor2[0],2)+Math.pow(coor1[1]-coor2[1],2));
	}

	public float distance(double[] coor1, double[] coor2){
		return (float) Math.sqrt(Math.pow(coor1[0]-coor2[0],2)+Math.pow(coor1[1]-coor2[1],2));
	}

}