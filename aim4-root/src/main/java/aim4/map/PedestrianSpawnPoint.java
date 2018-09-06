package aim4.map;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aim4.im.v2i.RequestHandler.PedestrianRequestHandler;
import aim4.pedestrian.Pedestrian;

public class PedestrianSpawnPoint {
	
	/*
	int id;
	double p; // pedestrian level
	double maxWaitTime;
	ArrayList<Pedestrian> pedestrians = new ArrayList<Pedestrian>();
	PedestrianRequestHandler requestHandler;
	
	public PedestrianSpawnPoint(int id, double p, double maxWaitTime, PedestrianRequestHandler requestHandler){
		this.id = id;
		this.p = p;
		this.maxWaitTime = maxWaitTime;
		this.pedestrians = new ArrayList<Pedestrian>();
		this.requestHandler = requestHandler;
	}
	
	public void act(double timeStep) {
		
		if 
		
	}
	
	public boolean pedestriansWaiting() {
		return !pedestrians.isEmpty();
	}
	*/
	
	private int id;
	private double p; // pedestrianLevel
	private ArrayList<Pedestrian> pedestrians;
	
	private double maxWaitTime; // Time between first spawn and crossing. Slider.
	private double timeRemaining;
	
	private double waitCarTime; // Yellow
	
	private double crossTimeRemaining;
	private double crossTime = 5;
	
	public PedestrianSpawnPoint(int id, double p, double maxWaitTime, PedestrianRequestHandler requestHandler){
		this.id = id;
		this.p = p;
		this.maxWaitTime = maxWaitTime;
		this.pedestrians = new ArrayList<Pedestrian>();
		this.timeRemaining = timeRemaining;
	}
	
	public int getID() {
		return id;
	}
	public int getNumberOfPedestrians() {
		return pedestrians.size();
	}
	public boolean pedestriansWaiting() {
		// Determines color of the spawn point
		return !pedestrians.isEmpty();
	}
	public void act(double timeStep) {
		
		if (crossTimeRemaining > 0) {
			// Pedestrians are currently crossing
			crossTimeRemaining -= timeStep;
			if (crossTimeRemaining <= 0) {
				// Pedestrians have crossed
				setCrossWalk();
			}
		}
		
		else { // crossmaxWaitTime <=0
			
			if (timeRemaining <= 0) {
				// Time is up
				pedestrians.clear();
				
			}
			
			else {
				
				// p = 100: pedestrian spawns every time step
				// p = 0: pedestrian never spawns
				Random r = new Random();
				// Return random double between 0 and 100
				double randomValue =  100000 * r.nextDouble();

				if (randomValue <= p) {
					Pedestrian p = new Pedestrian();
					pedestrians.add(p);
				}
				else {
					// Do nothing
				}
				
				if (pedestriansWaiting()) {
					maxWaitTime -= timeStep;
				}
			}
		}
	}
	
	private void setCrossWalk() {
		// ?
		// Change booleans
	}
	
	public void setCrossTime(double c) {
		crossTime = c;
	}
}
