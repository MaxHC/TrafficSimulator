package simulator.policies.car;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import simulator.planner.DijkstraPlanner;
import simulator.model.*;

public class MovePlannerPolicy extends CarPolicyMove{

	private DijkstraPlanner	planner;
	private LinkedHashMap<Stop,Road> objectives;

	public MovePlannerPolicy(SimulatorMap map){
		super(map);
		this.planner = new DijkstraPlanner(this.map);	
	}

	@Override
	public Map.Entry<Stop,Road> getNextObjective(){
		if(this.objectives == null){
			this.objectives = this.planner.plan(this.car);
		}
		if(this.objectives.size() > 0) {
			Iterator<Map.Entry<Stop,Road>> it = objectives.entrySet().iterator();
			Map.Entry<Stop,Road> next = null;
			if (it.hasNext()) {
				next = it.next();
			}
			this.objectives.remove(next.getKey());
			return next;
		}
		return null;
	}

}