package simulator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.HashSet;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

import simulator.model.*;
import simulator.view.*;
import simulator.controller.*;
import simulator.view.Panel;
import simulator.policies.car.*;

public class Main{

	public static void main(String[] args){

		JFrame frame = new JFrame("Prod");

		Road road1 = new Road(100, 100, 100, 800, true);
		Road road2 = new Road(100, 450, 1000, 450, false);
		Road road3 = new Road(850, 850,850, 100, true);
		Road road4 = new Road(1000, 250, 245, 250, false);
		Road road5 = new Road(400, 150, 400, 850, true);
		Road road6 = new Road(250, 100, 750, 600, false);

		Objective objective1 = new Objective("maison", 400, 350);
		Objective objective2 = new Objective("travail", 950, 450);

		ArrayList<Road> roads = new ArrayList<Road>();
		roads.add(road1);
		roads.add(road2);
		roads.add(road3);
		roads.add(road4);
		roads.add(road5);
		roads.add(road6);

		Set<Objective> objectives = new HashSet<Objective>();
		objectives.add(objective1);
		objectives.add(objective2);

		Car car1 = new Car(100, 100, objective1, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car2 = new Car(100, 150, objective2, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car3 = new Car(100, 200, objective1, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car4 = new Car(400, 150, objective2, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car5 = new Car(100, 800, objective2, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car6 = new Car(600, 250, objective1, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car7 = new Car(850, 650, objective2, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Car car8 = new Car(400, 250, objective1, 0.2, new DefaultCarTrafficLightPolicy(), new DefaultCarStopPolicy());
		Set<Car> cars = new HashSet<>();
		cars.add(car1);
		cars.add(car2);
		cars.add(car3);
		cars.add(car4);
		cars.add(car5);
		cars.add(car6);
		cars.add(car7);
		cars.add(car8);

		int trafficLightDistance = 30;
		int safetyDistance = 25;
				
		SimulatorMap map = new SimulatorMap(roads, cars, objectives);
		Simulation simulation = new Simulation(map, safetyDistance, trafficLightDistance);
		simulation.init();
		for(Car car: map.getCars()){
			if(car.getFinalObjective() == null){
				car.addPolicy(new MoveRandomPolicy(map));
			} else {
				car.addPolicy(new MovePlannerPolicy(map));
			}
		}
		Panel panel = new Panel(map, trafficLightDistance);

		frame.setLayout(new BorderLayout());
		JButton button1 = new JButton("Stop/Resume");
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				simulation.changeState();
			}
		});

		frame.add(panel, BorderLayout.CENTER);
		frame.add(button1, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1200, 900));
		frame.pack();
		frame.setVisible(true);	

		do{
			simulation.update(1, 60);
		}while(!simulation.finished());
		System.out.println("finished !!!");
		System.out.println(map.getNbAccident() + " accident(s) occured during the simulation");
		System.out.println(map.getTimeWait() + " ticks waited during the simulation");
	}

}