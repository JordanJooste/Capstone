package aim4.im.v2i.RequestHandler;

public class CrossWalk {

    
        int noOfPedestriansWaiting;
        int noOfPedestriansCrossing;
        
	boolean crossing; // Red
	boolean waiting; // Yellow. This waiting is only after max wait has elapsed
	
        //These next are fixed
        double maxWaitTime; //Fixed, from input slider.
        double waitCarTime; //Fixed but will be different for diagonals
        double crossTime; //Fixed, slightly longer for diags.
        
        //These measure the remaining time of above
        double rmwt;
        double rwct;
        double rct;
        
	// The two related spawn points on either side of the cross walk
	int a;
	int b;
        
        int id;
	
	// The PedestrianRequestHandler of this intersection
	PedestrianRequestHandler r;
	
	public CrossWalk(int id, int a, int b, PedestrianRequestHandler r,  double maxWaitTime) {
		this.a = a;
		this.b = b;
                this.id = id;
		this.crossing = false;
		this.waiting = false;
		this.r = r;
                
                noOfPedestriansWaiting = 0;
                noOfPedestriansCrossing = 0;
                
                
                if(id == 4 || id == 5){
                    //Specific time for diagonals
                    this.maxWaitTime = maxWaitTime*1.5;
                    crossTime = 7;
                    waitCarTime = 3;
                }else{
                    this.maxWaitTime = maxWaitTime;
                    crossTime=5;
                    waitCarTime = 5;
                }
	}
        
        public void addPedestrian(){
            
            if(noOfPedestriansWaiting == 0 && noOfPedestriansCrossing==0){
                //No one crossing and no one waiting
                //reset all timers
                rmwt = maxWaitTime;
                rwct = waitCarTime;
                rct = crossTime;  
                waiting = false;
                crossing = false;
            }else if(noOfPedestriansWaiting == 0 && noOfPedestriansCrossing>0){
                //No one waiting but people crossing.
                //Don't reset crossing timer
                rmwt = maxWaitTime;
                rwct = waitCarTime; 
                crossing = true;
                waiting = false;
            }
            noOfPedestriansWaiting++;
        }
        
        public int act(double timestep){
            if (waiting == false && crossing == false && noOfPedestriansWaiting > 0){
                rmwt-=timestep;
            }
            if(waiting == true&&noOfPedestriansWaiting>0){
                //Pedestrians waiting
                rwct -= timestep;
            }
            if(crossing == true){
                //Pedestrians crossing
                rct -= timestep;
            }
            
            if(rmwt<=0){
                //Turning boolean to true
                if(id == 0){
                    r.setLeft();
                }else if(id == 1){
                    r.setTop();
                }else if(id == 2){
                    r.setRight();
                }else if(id == 3){
                    r.setBottom();
                }else if(id == 4){
                    r.setTopLeftToBottomRight();
                }else if(id == 5){
                    r.setTopRightToBottomLeft();
                }
                rmwt = maxWaitTime;
                waiting = true;
            }
            if(rwct<=0){
                //People now going to cross
                crossing = true;
                waiting = false;
                noOfPedestriansCrossing = noOfPedestriansWaiting;
                noOfPedestriansWaiting = 0;
                rwct=waitCarTime;
            }
            if(rct<=0){
                //People finished crossing
                crossing = false;
                //turning boolean to false
                if(id == 0){
                    r.setLeft();
                }else if(id == 1){
                    r.setTop();
                }else if(id == 2){
                    r.setRight();
                }else if(id == 3){
                    r.setBottom();
                }else if(id == 4){
                    r.setTopLeftToBottomRight();
                }else if(id == 5){
                    r.setTopRightToBottomLeft();
                }
                rct = crossTime;
                //Store this in some grand total of pedestrians crossed...
                int result = noOfPedestriansCrossing;
                noOfPedestriansCrossing = 0;
                return result;
            }
            return 0;
        }
	
	public void setCrossing(){
		if(crossing==true)
			crossing=false;
	    else{
	    	crossing=true;
		    //r.sendRejects();
		}
	}
	
	public void setWaiting(){
		if(waiting==true)
			waiting=false;
	    else{
	    	waiting=true;
		    //r.sendRejects();
		}
	}
	
	public boolean getCrossing(){
		return crossing;
	}
	
	public boolean getWaiting(){
		return waiting;
	}
	
	public int getID() {
		return id;
	}
}

