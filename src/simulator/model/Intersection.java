/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulator.model;

import simulator.policies.light.LightPolicy;
import simulator.policies.light.LightPolicyGeneral;
import simulator.policies.light.TurnRightDecorator;

import java.util.*;

/**
 *
 * @author 21811412
 */
public class Intersection extends Stop{
    private ArrayList<Road> connectedRoads;
    public HashMap<Road,HashMap<Road,TrafficLight>> lights;
    public ArrayList<Road> order;
    public int laneNumber;
    public LightPolicy lp;

    public Intersection(int x, int y) {
        super(x, y);
        this.connectedRoads = new ArrayList<>();
        this.lights = new HashMap<>();
        this.order = new ArrayList<>();
        this.lp = new TurnRightDecorator(new LightPolicyGeneral());
    }

    public void addConnectedRoad(Road road) {
        this.connectedRoads.add(road);
    }

    public ArrayList<Road> getConnectedRoads() {
        return this.connectedRoads;
    }

    public ArrayList<TrafficLight> plantTrafficLights() {
        ArrayList<TrafficLight> lights = new ArrayList<>();
        for (Road r0: this.connectedRoads) {
            if (Arrays.equals(r0.getStart(),this.coordinates)) {
                continue;
            }
            this.lights.put(r0,new HashMap<>());
            for (Road r1: this.connectedRoads) {
                if (r1 != r0.reverse) {
                    if (!Arrays.equals(r1.getEnd(),this.coordinates)) {
                        TrafficLight light = new TrafficLight(true);
                        this.lights.get(r0).put(r1,light);
                        lights.add(light);
                    }
                }
            }
        }
        return lights;
    }

    public void getRoadOrder() {
        double x = 1;
        double y = 0;

        TreeMap<Double,Road> cos = new TreeMap<>();
        ArrayList<Road> order = new ArrayList<>();

        for (Road road: this.connectedRoads) {
            if (!(cos.containsValue(road) || cos.containsValue(road.reverse))) {
                double[] vector = road.getVector();
                if (vector[1] < 0 || (vector[0] == 1 && vector[1] == 0))
                    if (road.getTwoWays()) {
                        vector = road.reverse.getVector();
                        road = road.reverse;
                    }
                double c = (vector[0] * x + vector[1] * y) / (Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]) * Math.sqrt(x * x + y * y));
                cos.put(c, road);
            }

        }
        Collection<Road> cosValues = cos.values();

        for (Road road: cosValues) {
            order.add(road);
        }

        this.laneNumber = order.size();

        for (Road road: cosValues) {
            if (road.getTwoWays())
                order.add(road.reverse);
            else
                order.add(null);
        }

        this.order = order;
        System.out.println(order);
    }

    public void setUpLight() {
        this.lp.setUpLights(this.laneNumber,this.order,this.lights);
    }

    @Override
    public String toString() {
        return "Intersection at " + this.coordinates[0] + " , " + this.coordinates[1] + " ";
    }
}
