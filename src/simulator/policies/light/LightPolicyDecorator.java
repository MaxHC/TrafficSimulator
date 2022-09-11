package simulator.policies.light;

import simulator.model.Road;
import simulator.model.TrafficLight;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class LightPolicyDecorator implements LightPolicy{
    private final LightPolicy policy;

    public LightPolicyDecorator(LightPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void setUpLights(int laneNumber, ArrayList<Road> order, HashMap<Road, HashMap<Road, TrafficLight>> lights) {
        policy.setUpLights(laneNumber, order, lights);
    }
}
