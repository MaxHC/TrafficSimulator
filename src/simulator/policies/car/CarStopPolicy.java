package simulator.policies.car;

import simulator.model.Road;
import simulator.model.Intersection;

public abstract class CarStopPolicy{

	/* function called while there is an possibility of accident
	 * return false to stop, true to continue
	 */
	public abstract boolean move(double[] coordinates, Road currentRoad, int trafficLightDistance);

	//coordinates is the coord of car
	public boolean isOnIntersection(double[] coordinates, Road currentRoad, int trafficLightDistance){
		for(Intersection intersection: currentRoad.getIntersections()){
			if(this.distance(coordinates, intersection.getCoordinates()) <= trafficLightDistance){
				return true;
			}
		}
		return false;
	}
	
	public float distance(double[] coor1, int[] coor2){
		return (float) Math.sqrt(Math.pow(coor1[0]-coor2[0],2)+Math.pow(coor1[1]-coor2[1],2));
	}

}