package aim4.map;

import java.awt.geom.Point2D;
import java.util.List;

import aim4.pedestrian.Pedestrian;

public class PedestrianSpawnPoint {
	
	// Location of the spawning point
	private Point2D location;
	// Destination of this spawning point
	private PedestrianSpawnPoint destinationSpawnPoint;
	private String crosswalk;
	
	public PedestrianSpawnPoint(Point2D location, PedestrianSpawnPoint destinationSpawnPoint) {
		this.location = location;
		this.destinationSpawnPoint = destinationSpawnPoint;
	}
	
	public Point2D getLocation() {
		return location;
	}
	
	public Point2D getDestination() {
		return location;
	}

	public List<Pedestrian> act(double timeStep) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
