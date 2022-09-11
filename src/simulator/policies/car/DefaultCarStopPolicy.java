package simulator.policies.car;

import simulator.model.Road;

public class DefaultCarStopPolicy extends CarStopPolicy{
	
	public boolean move(double[] coordinates, Road currentRoad, int trafficLightDistance){
		return false;
	}

}