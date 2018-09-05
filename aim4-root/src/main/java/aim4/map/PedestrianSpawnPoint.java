package aim4.map;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aim4.pedestrian.Pedestrian;

public class PedestrianSpawnPoint {
	
	private int id;
	private double p; // pedestrianLevel
	private ArrayList<Pedestrian> pedestrians;
	
	private double timeRemaining;
	private double maxWaitTime;
	
	public PedestrianSpawnPoint(int id, double p, double maxWaitTime){
		this.id = id;
		this.p = p;
		this.timeRemaining = maxWaitTime;
		this.pedestrians = new ArrayList<Pedestrian>();
		this.maxWaitTime = maxWaitTime;
		this.timeRemaining = maxWaitTime;
	}
	
	public int getID() {
		return id;
	}
	public int getNumberOfPedestrians() {
		return pedestrians.size();
	}
	public void clearPedestrians() {
		pedestrians.clear();
	}
	public boolean pedestriansWaiting() {
		return !pedestrians.isEmpty();
	}
	
	public void spawn () {
		Pedestrian p = new Pedestrian();
		pedestrians.add(p);
	}
	
	public void act(double timeStep) {
		// p = 100: pedestrian spawns every time step
		// p = 0: pedestrian never spawns
		Random r = new Random();
		
		// Return random double between 0 and 100
		double randomValue =  10000 * r.nextDouble();

		if (getTimeRemaining() <= 0) {
			// Time is up
			pedestrians.clear();
			timeRemaining = maxWaitTime;
		}
		
		else {
			if (randomValue <= p) {
				Pedestrian p = new Pedestrian();
				pedestrians.add(p);
			}
			else {
				// Do nothing
			}
			
			if (pedestriansWaiting()) {
				timeRemaining -= timeStep;
			}
		}
	}
	
	public double getTimeRemaining() {
		return timeRemaining;
	}
}
