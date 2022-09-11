package simulator.controller;

import simulator.model.*;
import simulator.view.Panel;
import simulator.planner.DijkstraPlanner;

public class Simulation {

	public SimulatorMap map;
	public DijkstraPlanner planner;
	public TrafficLightController control;
	public boolean stop = false;
	private int safetyDistance; //distance between 2 car to trigger an accident, must be smaller than trafficLightDistance to avoid accident between a car in intersection and one waiting to the traffic light
	private int trafficLightDistance; //distance to setup the light before intersection
	private static int i=0;

	public Simulation(SimulatorMap map, int safetyDistance, int trafficLightDistance) {
		this.map = map;
		this.safetyDistance = safetyDistance;
		this.trafficLightDistance = trafficLightDistance;
		this.planner = new DijkstraPlanner(this.map);
	}

	public void init() {
		this.map.initMap();

		for (Car car : this.map.getCars()) {
			this.planner.plan(car);
		}
		this.control = new TrafficLightController();
	}

	//at each launch, update the simulation for 1 tick
	public void update(int timer, int fps) {
		i ++;
		if (!stop) {
			try {
				Thread.sleep(timer);
			} catch (InterruptedException e) {
				//pass
			}
			for (Car car : this.map.getCars()) {
				if (!car.hasFinish() && !car.isDamaged()) {
					Car damagedCar = this.map.carAccident(car, this.safetyDistance);
					if(damagedCar == null){
						if(!map.incommingAccident(car, this.safetyDistance)){
							//all is safe
							car.update(this.trafficLightDistance);
								if(!car.isMoving()){
									this.map.isWaiting();
								}
						} else {
							//dangerous situation, ask policy
							if(car.getStopPolicy(this.trafficLightDistance)){
								car.update(this.trafficLightDistance);
							} else {
								this.map.isWaiting();
							}
						}
					} else {
						//accident
						car.hasAccident();
						car.stop();
						damagedCar.hasAccident();
						damagedCar.stop();
						this.map.addAccident();
					}
				}
			}
			for (TrafficLight light : this.map.getLights())
				this.control.counting(light);

			if(1000/fps <= i){
				i = 0;
				this.map.fireChangement();
			}
		}
	}

	public void changeState() {
		this.stop = !this.stop;
	}

	//return true when nothing can be updated = simulation is finished
	public boolean finished(){
		for(Car car: this.map.getCars()){
			if(!car.hasFinish() && !car.isDamaged()){
				return false;
			}
		}
		return true;
	}

}