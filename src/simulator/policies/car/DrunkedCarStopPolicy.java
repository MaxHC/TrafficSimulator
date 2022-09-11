package simulator.policies.car;

import simulator.model.Road;

public class DrunkedCarStopPolicy extends CarStopPolicy{

	public int probability = 75; //probability to forward in dangerous situation in % at EACH tick

	public DrunkedCarStopPolicy(int probability){
		this.probability = probability;
	}
	
	public boolean move(double[] coordinates, Road currentRoad, int trafficLightDistance){
		int rand = (int)(Math.random()*100);
		return rand<=this.probability;
	}

}