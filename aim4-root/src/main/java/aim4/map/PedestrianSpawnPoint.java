package aim4.map;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aim4.im.v2i.RequestHandler.PedestrianRequestHandler;
import aim4.pedestrian.Pedestrian;

public class PedestrianSpawnPoint {
	
	private int id;
	private double p; // pedestrianLevel
	private ArrayList<Pedestrian> pedestrians;
	
	private double timeRemaining;
	private double maxWaitTime;
	private double crossTimeRemaining = 0;
	private double crossTime = 5;
	
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
	public void pedestriansLeaveSpawnPoint(int i) {
		pedestrians.clear();
		timeRemaining = maxWaitTime;
		crossTimeRemaining = crossTime;
	}
	public boolean pedestriansWaiting() {
		// Determines color of the spawn point
		return !pedestrians.isEmpty();
	}
	
	public void act(double timeStep, PedestrianRequestHandler requestHandler, int i) {
		
		if (crossTimeRemaining > 0) {
			// Pedestrians are currently crossing
			crossTimeRemaining -= timeStep;
			if (crossTimeRemaining <= 0) {
				
			}
		}
		
		else { // crossTimeRemaining <=0
			
			if (getTimeRemaining() <= 0) {
				// Time is up
				requestHandler.setCrossWalk(i);
				pedestriansLeaveSpawnPoint(i);
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
					timeRemaining -= timeStep;
				}
			}
		}
	}
	
	public double getTimeRemaining() {
		return timeRemaining;
	}
	
	public double getCrossTimeRemaining() {
		return crossTimeRemaining;
	}
	
	private void setCrossWalk(int i) {
		
	}
	
	public void setCrossTime(double c) {
		crossTime = c;
	}
	
}
