package simulator.planner;

import java.util.*;

import simulator.model.*;

public class DijkstraPlanner{

	public SimulatorMap map;

	public DijkstraPlanner(SimulatorMap map){
		this.map = map;
	}

	//plan the traveling of the selected car (Dijsktra's algo)
	public LinkedHashMap<Stop, Road> plan(Car car){
		LinkedHashMap<Stop,Road> objectivesList = new LinkedHashMap<>();
		Objective finalObjective = car.getFinalObjective();

		HashMap<Intersection,HashMap<Intersection,Road>> graph = this.map.getGraph();

		if (car.getCurrentRoad().getObjectives().contains(finalObjective)) {
			objectivesList.put(finalObjective,car.getCurrentRoad());
			if (!this.map.coordinateOnTheWay(car.getCurrentRoad(), finalObjective.getCoordinates(), new int[] {(int)car.getCoordinates()[0], (int)car.getCoordinates()[1]})) {
				car.setCurrentRoad(car.getCurrentRoad().reverse);
				objectivesList.replace(finalObjective,car.getCurrentRoad().reverse);
			}
			return objectivesList;
		}

		HashMap<Intersection, Float> initialStates = this.getNextIntersections(car);

		Map<Intersection, Intersection> father = new HashMap<Intersection, Intersection>();
		Map<Intersection, Float> distance = new HashMap<Intersection, Float>();
		LinkedList<Intersection> goals = new LinkedList<Intersection>();
		LinkedList<Intersection> open = new LinkedList<Intersection>();
		LinkedList<Intersection> close = new LinkedList<Intersection>(); //to prevent from infinite loop (caused by loop in the map)
		initialStates.forEach( (k,v) -> {
			father.put(k, null);
			distance.put(k, v);
			open.add(k);
		});

		//explore all the graph
		while(open.size() != 0){
			Intersection instantiation = this.getArgmin(open, distance); //get closest node from the opens
			open.remove(instantiation);
			close.add(instantiation);
			if(this.isOnIntersectionWays(instantiation, finalObjective)){
				goals.add(instantiation); //if satisfied by the current nodes way add to potential plan
			}
			for(Intersection intersection: graph.get(instantiation).keySet()){
				if(!close.contains(intersection)){ //open neighbor nodes
					int[] coordInstantiation = instantiation.getCoordinates();
					int[] coordIntersection = intersection.getCoordinates();
					float distanceBetween = this.map.distance(coordInstantiation,coordIntersection);
					distance.put(intersection, distance.get(instantiation) + distanceBetween);
					father.put(intersection, instantiation);
					open.add(intersection);
				}
			}
		}

		if(goals.size() > 0){
			List<Intersection> intersectionList = this.getDijkstraPlan(father, goals, distance); //get shortest plan
			for(int i=0; i<intersectionList.size(); i++) {
				if (i == 0) { //init car with the first value
					Road currentRoad = car.getCurrentRoad();
					double[] doubleCarCoord = car.getCoordinates();

					int[] carCoord = new int[2];
					carCoord[0] = (int) doubleCarCoord[0];
					carCoord[1] = (int) doubleCarCoord[1];

					if (this.map.coordinateOnTheWay(currentRoad, intersectionList.get(0).getCoordinates(), carCoord))
						objectivesList.put(intersectionList.get(0),currentRoad);
					else
						objectivesList.put(intersectionList.get(0),currentRoad.reverse);
				} else {

					Road extractedRoad = graph.get(intersectionList.get(i-1)).get(intersectionList.get(i));
					objectivesList.put(intersectionList.get(i),extractedRoad);
				}
				if (i == intersectionList.size() - 1) {
					for (Road road : intersectionList.get(i).getConnectedRoads()) {
						if (road.getObjectives().contains(finalObjective)) {
							if (this.map.coordinateOnTheWay(road, finalObjective.getCoordinates(), intersectionList.get(i).getCoordinates())) {
								objectivesList.put(finalObjective, road);
							}
						}
					}
				}
			}
		}

		return objectivesList;
	}

	//get the closest node from the graph
	public Intersection getArgmin(LinkedList<Intersection> open, Map<Intersection, Float> distance){
		Intersection selected = null;
		Float mapDistance = null;
		for(Intersection intersection: open){
			if(selected == null){
				selected = intersection;
				mapDistance = distance.get(selected);
			} else {
				if(distance.get(intersection) < mapDistance){
					selected = intersection;
					mapDistance = distance.get(selected);
				}
			}
		}

		return selected;
	}

	//return if the intersection is on the car's ways
	public boolean isOnIntersectionWays(Intersection intersection, Objective finalObjective){
		ArrayList<Road> roads = intersection.getConnectedRoads();

		for(Road road: roads){
			if(road.getObjectives().contains(finalObjective)){
				if(this.map.coordinateOnTheWay(road, finalObjective.getCoordinates(), intersection.getCoordinates())){
					return true;
				}
				
			}
		}

		return false;
	}

	//make the shortest plan
	public List<Intersection> getDijkstraPlan(Map<Intersection, Intersection> father, LinkedList<Intersection> goals, Map<Intersection, Float> distance){
		List<Intersection> dijkstraPlan = new LinkedList<Intersection>();
		Intersection goal = this.getArgmin(goals, distance);
		while(goal != null){
			dijkstraPlan.add(goal);
			goal = father.get(goal);
		}
		Collections.reverse(dijkstraPlan);
		return dijkstraPlan;
	}

	public HashMap<Intersection, Float> getNextIntersections(Car car){
		HashMap<Intersection, Float> potentialIntersection = new HashMap<>();
		Road currentRoad = car.getCurrentRoad();
		double[] doubleCarCoord = car.getCoordinates();
		
		int[] carCoord = new int[2];
		carCoord[0] = (int) doubleCarCoord[0];
		carCoord[1] = (int) doubleCarCoord[1];

		for(Intersection intersection: this.map.intersections){
			if(intersection.getConnectedRoads().contains(currentRoad) && this.map.coordinateOnTheWay(currentRoad, intersection.getCoordinates(), carCoord)){
				potentialIntersection.put(intersection, this.map.distance(intersection.getCoordinates(), carCoord));
			}
			if (currentRoad.getTwoWays()) {
				if(intersection.getConnectedRoads().contains(currentRoad.reverse) && this.map.coordinateOnTheWay(currentRoad.reverse, intersection.getCoordinates(), carCoord)){
					potentialIntersection.put(intersection, this.map.distance(intersection.getCoordinates(), carCoord));
				}
			}
		}
		return potentialIntersection;
	}

}