package simulator.policies.light;

import simulator.model.Road;
import simulator.model.TrafficLight;

import java.util.ArrayList;
import java.util.HashMap;

public class LightPolicyGeneral implements LightPolicy{

    @Override
    public void setUpLights(int laneNumber, ArrayList<Road> order, HashMap<Road, HashMap<Road, TrafficLight>> lights) {

        int redTime = 4*(laneNumber-1);

        int greenTime = 3;

        int yellowTime = 1;

        for (int i = 0; i<laneNumber; i++) {
            Road road = order.get(i);
            HashMap<Road, TrafficLight> lightMap = lights.get(road);
            if (lightMap != null) {
                ArrayList<TrafficLight> ls = new ArrayList<>();
                ls.addAll(lightMap.values());
                if (road.getTwoWays())
                    ls.addAll(lights.get(road.reverse).values());

                for (TrafficLight light : ls) {
                    light.setTime(new int[]{redTime, greenTime, yellowTime});
                    if (i == 0) {
                        light.setLight(1);
                        light.setCount(0);
                    } else {
                        light.setLight(0);
                        light.setCount(0 + 300 * (i - 1) * 4);
                    }
                }
            }
        }

    }

}
