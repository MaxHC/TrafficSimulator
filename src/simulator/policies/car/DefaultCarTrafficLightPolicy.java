package simulator.policies.car;

public class DefaultCarTrafficLightPolicy extends CarTrafficLightPolicy{
	
	public boolean policy(int lightState){
		if(lightState == 0){
			return false;
		} 

		return true;
	}

}