package simulator.view;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JLabel;

import java.util.Set;
import simulator.model.*;
import simulator.utils.EcouteurModele;

public class Panel extends JPanel implements EcouteurModele {

	private SimulatorMap map;
	private int trafficLightDistance;
	public final static Color[] COLOR = {Color.red, Color.green, Color.orange};
	static final long serialVersionUID = -1234567890123456789L; //fix warning
	private JLabel accidents, timeWait;

	public Panel(SimulatorMap map, int trafficLightDistance){
		this.map = map;
		this.trafficLightDistance = trafficLightDistance;
		map.ajoutEcouteur(this);
		this.accidents = new JLabel("Accidents number : 0");
		this.add(accidents);
		this.timeWait = new JLabel("Cumulated time wait : 0 ticks");
		this.add(timeWait);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int[] start, end;
		double[] coordinates;
		int radius = 20;

		this.accidents.setText("Accidents Number : " + this.map.getNbAccident());
		this.timeWait.setText("Cumulated time wait : " + this.map.getTimeWait() + " ticks");

		//display roads
		g.setColor(Color.black);
		for(Road road: this.map.getRoads()){
			start = road.getStart();
			end = road.getEnd();
			g.drawLine(start[0], start[1], end[0], end[1]);
		}

		//display cars
		for(Car car: this.map.getCars()){
			if(!car.hasFinish()){
				if(car.isDamaged()){
					g.setColor(Color.red);
				} else {
					g.setColor(Color.blue);
				}
				coordinates = car.getCoordinates();
				g.fillOval((int)coordinates[0]-radius/2, (int)coordinates[1]-radius/2, radius, radius);
			}
		}

		//display objectives
		g.setColor(Color.pink);
		for(Objective objective: this.map.getObjectives()){
			int[] oCoordinates = objective.getCoordinates();
			g.fillOval(oCoordinates[0]-radius/2, oCoordinates[1]-radius/2, radius, radius);
		}

		//display traffic light
		for (Intersection inter : this.map.intersections) {
			int[] oCoordinates = inter.getCoordinates();
			inter.lights.forEach( (road,light) -> {
				int mainX = oCoordinates[0] - (int) (road.getVector()[0] * this.trafficLightDistance) - radius/6;
				int mainY = oCoordinates[1] - (int) (road.getVector()[1] * this.trafficLightDistance) - radius/6;
				g.setColor(Color.RED);
				if (!light.isEmpty())
					light.forEach( (r,l) -> {
						g.setColor(COLOR[l.getLight()]);
						if (r != road)
							g.fillOval(mainX + (int) (r.getVector()[0] * 8), mainY + (int) (r.getVector()[1] * 8), radius / 3, radius / 3);
						else
							g.fillOval(mainX, mainY, radius / 3, radius / 3);
					});
			});

		}
		
	}

	@Override
	public void modeleMisAJour(Object source) {
		this.repaint();
	}
}