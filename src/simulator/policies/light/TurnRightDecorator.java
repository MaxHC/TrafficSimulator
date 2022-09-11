package simulator.policies.light;

import simulator.model.Road;
import simulator.model.TrafficLight;

import java.util.ArrayList;
import java.util.HashMap;

public class TurnRightDecorator extends LightPolicyDecorator{
    public TurnRightDecorator(LightPolicy policy) {
        super(policy);
    }

    @Override
    public void setUpLights(int laneNumber, ArrayList<Road> order, HashMap<Road, HashMap<Road, TrafficLight>> lights) {
        super.setUpLights(laneNumber, order, lights);

        int n = order.size();

        for (int i = 0; i<n; i++) {

            Road road = order.get(i);

            if (road != null) {
                Road rightRoad = order.get((i + laneNumber + 1) % n);
                if (rightRoad != null) {
                    HashMap<Road, TrafficLight> lightMap = lights.get(road);
                    if (lightMap != null) {
                        if (lightMap.get(rightRoad) != null) {
                            TrafficLight light = lightMap.get(rightRoad);
                            light.setCountable(false);
                            light.setLight(1);
                        }
                    }
                }
            }
        }
    }
}
