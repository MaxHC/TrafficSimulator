package simulator.policies.car;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import simulator.model.*;

public class MoveRandomPolicy extends CarPolicyMove{

	public MoveRandomPolicy(SimulatorMap map){
		super(map);
	}

	@Override
	public Map.Entry<Stop,Road> getNextObjective(){
		int n;
		int[] coordinates = new int[] {(int) this.car.getCoordinates()[0],(int) this.car.getCoordinates()[1]};
		Road nextRoad = null;
		if (this.car.getCurrentObjective() == null) {
			nextRoad = this.car.getCurrentRoad();
		} else {
			Intersection intersection = (Intersection) this.car.getCurrentObjective();

			n = intersection.getConnectedRoads().size();
			while (nextRoad == null || nextRoad == car.getCurrentRoad().reverse)
				nextRoad = intersection.getConnectedRoads().get(this.randomIndex(n));
		}

		ArrayList<Stop> possibleStops = new ArrayList<>();
		for (Stop stop : nextRoad.getStops()) {
			if (this.car.getCurrentObjective() != stop && this.map.coordinateOnTheWay(nextRoad, stop.getCoordinates(), coordinates))
				possibleStops.add(stop);
		}
		n = possibleStops.size();

		Stop nextStop = (n==0) ? new Objective("end of road",nextRoad.getEnd()[0],nextRoad.getEnd()[1]) : possibleStops.get(randomIndex(n));

		Road finalNextRoad = nextRoad;
		return new Map.Entry<Stop, Road>() {
			@Override
			public Stop getKey() {
				return nextStop;
			}

			@Override
			public Road getValue() {
				return finalNextRoad;
			}

			@Override
			public Road setValue(Road value) {
				return null;
			}
		};
	}

	public int randomIndex(int size){
		int index = (int)(Math.random()*size);
		return index;
	}

}