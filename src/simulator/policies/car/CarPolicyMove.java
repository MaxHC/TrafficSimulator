package simulator.policies.car;

import java.util.ArrayList;
import java.util.Map;

import simulator.model.*;

public abstract class CarPolicyMove{

	public SimulatorMap map;
	public Car car;

	public CarPolicyMove(SimulatorMap map){
		this.map = map;
	}

	public void addCar(Car car){
		this.car = car;
	}

	public abstract Map.Entry<Stop,Road> getNextObjective();


}