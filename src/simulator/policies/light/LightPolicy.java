package simulator.policies.light;

import simulator.model.Road;
import simulator.model.TrafficLight;

import java.util.ArrayList;
import java.util.HashMap;

public interface LightPolicy {
    void setUpLights(int laneNumber, ArrayList<Road> order, HashMap<Road, HashMap<Road, TrafficLight>> lights);
}
