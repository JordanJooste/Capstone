package aim4.im.v2i.RequestHandler;

public class CrossWalk {

	boolean crossing; // Red
	boolean waiting; // Yellow
	
	// The two related spawn points on either side of the cross walk
	int a;
	int b;
	
	// The PedestrianRequestHandler of this intersection
	PedestrianRequestHandler r;
	
	public CrossWalk(int a, int b, PedestrianRequestHandler r) {
		this.a = a;
		this.b = b;
		this.crossing = false;
		this.waiting = false;
		this.r = r;
	}
	
	public void setCrossing(){
		if(crossing==true)
			crossing=false;
	    else{
	    	crossing=true;
		    r.sendRejects();
		}
	}
	
	public void setWaiting(){
		if(waiting==true)
			waiting=false;
	    else{
	    	waiting=true;
		    r.sendRejects();
		}
	}
	
	public boolean getCrossing(){
		return crossing;
	}
	
	public boolean getWaiting(){
		return waiting;
	}
}

