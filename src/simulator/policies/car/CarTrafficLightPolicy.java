package simulator.policies.car;

public abstract class CarTrafficLightPolicy{
	
	/*default policy. Red : stop, green/orange : continue
	 *return false to stop, true to continue
	 */
	public abstract boolean policy(int lightState);

}