package aim4.im.v2i.RequestHandler;

import aim4.config.Constants.CardinalDirection;
import aim4.config.Constants.TurnDirection;
import aim4.im.Intersection;
import aim4.im.IntersectionManager;
import java.util.Iterator;
import java.util.List;

import aim4.im.v2i.policy.BasePolicy;
import aim4.im.v2i.policy.BasePolicyCallback;
import aim4.map.PedestrianSpawnPoint;
import aim4.im.v2i.policy.BasePolicy.ProposalFilterResult;
import aim4.im.v2i.policy.BasePolicy.ReserveParam;
import aim4.msg.i2v.Reject;
import aim4.msg.v2i.Request;
import aim4.pedestrian.Pedestrian;
import aim4.sim.StatCollector;
import java.util.ArrayList;

public class PedestrianRequestHandler implements RequestHandler{
  
  /////////////////////////////////
  // CONSTANTS
  /////////////////////////////////

  /** The switch time interval */
  private static final double SWITCH_TIME_INTERVAL = 20.0;

  /////////////////////////////////
  // PRIVATE FIELDS
  /////////////////////////////////

  /** The base policy */
  private BasePolicyCallback basePolicy;
  /** The next switch time */
  private double nextSwitchTime;
  
 
  /** Whether pedestrians are crossing the left road*/
  private boolean left;
  /** Whether pedestrians are crossing the right road*/
  private boolean right;
  /** Whether pedestrians are crossing the top road*/
  private boolean top;
  /** Whether pedestrians are crossing the bottom road*/
  private boolean bottom;
  /** Whether pedestrians are crossing the intersection diagonally from top left to bottom right road*/
  private boolean topLeftToBottomRight;
  /** Whether pedestrians are crossing the intersection diagonally from top right to bottom left road*/
  private boolean topRightToBottomLeft;
  /** Whether pedestrians are crossing all roads and across the intersection*/
  private boolean stopAll;
 
  //private boolean leftWaiting;
  //private boolean rightWaiting;
  //private boolean topWaiting;
  //private boolean bottomWaiting;
  //private boolean topLeftToBottomRightWaiting;
  //private boolean topRightToBottomLeftWaiting;
  
  
  // Spawn point variables
  private ArrayList<PedestrianSpawnPoint> psps = new ArrayList<PedestrianSpawnPoint>();
  
  //Crosswalks
  private ArrayList<CrossWalk> walks = new ArrayList<CrossWalk>();
  
  private double pedestrianLevel;
  private double maxWaitTime;
  
  /** A copy of the intersection for this RequestHandler, so can determine turning directions. */
  private Intersection intersection;
  /** A copy of the intersection manager for this RequestHandler, so can communicate with vehicles. */
  private IntersectionManager im;
  
  private ArrayList<Integer> tracking;
  
  /////////////////////////////////
  // CONSTRUCTOR
  /////////////////////////////////
  public PedestrianRequestHandler(Intersection i, IntersectionManager im, double pedestrianLevel, double maxWaitTime){
      intersection = i;
      this.im = im;
      left=right=top=bottom=topLeftToBottomRight=topRightToBottomLeft=stopAll=false;
      tracking=new ArrayList<Integer>();
      this.pedestrianLevel = pedestrianLevel;
      this.maxWaitTime = maxWaitTime;
      for (int x=0; x<=11; x++) {
    	  psps.add(new PedestrianSpawnPoint(x, pedestrianLevel, maxWaitTime, this));
      }
      walks.add(new CrossWalk(0,0,11,this)); //CrossWalk 0
      walks.add(new CrossWalk(1,2,3,this)); //CrossWalk 1
      walks.add(new CrossWalk(2,5,6,this));// ...
      walks.add(new CrossWalk(3,8,9,this));
      walks.add(new CrossWalk(4,1,7,this));
      walks.add(new CrossWalk(5,4,10,this));
  } 
  
  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * Set the base policy call-back.
   *
   * @param basePolicy  the base policy's call-back
   */
  @Override
  public void setBasePolicyCallback(BasePolicyCallback basePolicy) {
    this.basePolicy = basePolicy;
    this.nextSwitchTime = basePolicy.getCurrentTime() + SWITCH_TIME_INTERVAL;
  }

  /**
   * Let the request handler to act for a given time period.
   *
   * @param timeStep  the time period
   */
  @Override
  public void act(double timeStep) {
    if (basePolicy.getCurrentTime() > nextSwitchTime) {

      nextSwitchTime = basePolicy.getCurrentTime() + SWITCH_TIME_INTERVAL;
    }
  }
  
  public void addPedestrianToWalk(int spawnID){
      if (spawnID == 0 || spawnID == 11)
          walks.get(0).addPedestrian();
      if (spawnID == 2 || spawnID == 3)
          walks.get(1).addPedestrian();
      if (spawnID == 5 || spawnID == 6)
          walks.get(2).addPedestrian();
      if (spawnID == 8 || spawnID == 9)
          walks.get(3).addPedestrian();
      if (spawnID ==1 || spawnID == 7)
          walks.get(4).addPedestrian();
      if (spawnID == 4 || spawnID == 10)
          walks.get(5).addPedestrian();
      
  }

  /**
   * Process the request message.
   *
   * @param msg the request message
   *//*
  @Override
  public void processRequestMsg(Request msg) {
    int vin = msg.getVin();
    
    //If pedestrian crossing over all crossings, reject any request.
    if (stopAll == true){
        basePolicy.sendRejectMsg(vin,msg.getRequestId(), Reject.Reason.NO_CLEAR_PATH);
        return;
    }
    
    // If the vehicle has got a reservation already, reject it.
    if (basePolicy.hasReservation(vin)) {
      basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                               Reject.Reason.CONFIRMED_ANOTHER_REQUEST);
      return;
    }

    // filter the proposals
    BasePolicy.ProposalFilterResult filterResult =
      BasePolicy.standardProposalsFilter(msg.getProposals(),
                                         basePolicy.getCurrentTime());
    if (filterResult.isNoProposalLeft()) {
      basePolicy.sendRejectMsg(vin,
                               msg.getRequestId(),
                               filterResult.getReason());
    }
    
    List<Request.Proposal> proposals = filterResult.getProposals();

    // remove proposals that is not in the correct turn direction.
    removeProposalWithIncorrectTurnDirection(proposals);
    if (proposals.isEmpty()) {
      basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                               Reject.Reason.NO_CLEAR_PATH);
      return;
    }

    // try to see if reservation is possible for the remaining proposals.
    BasePolicy.ReserveParam reserveParam = basePolicy.findReserveParam(msg, proposals);
    if (reserveParam != null) {
      basePolicy.sendComfirmMsg(msg.getRequestId(), reserveParam);
    } else {
      basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                               Reject.Reason.NO_CLEAR_PATH);
    }
  }

  /**
   * Remove proposals whose arrival time is prohibited from entering
   * the intersection according to {@code canEnterAtArrivalTime()}.
   *
   * @param proposals  a set of proposals
   *//*
  private void removeProposalWithIncorrectTurnDirection(
                                             List<Request.Proposal> proposals) {
    for (Iterator<Request.Proposal> tpIter = proposals.listIterator();
         tpIter.hasNext();) {
      Request.Proposal p = tpIter.next();
      TurnDirection turnDirection = intersection.calcTurnDirection(p.getArrivalLaneID(), p.getDepartureLaneID());
      if (topRightToBottomLeft || topLeftToBottomRight) {
        
        if (turnDirection.equals(turnDirection.STRAIGHT)) {
          tpIter.remove();
        }
        
        
      } else {
        if (p.getArrivalLaneID() != p.getDepartureLaneID()) {
          //tpIter.remove();
        }
      }
    }
  } 
  
  /**
   * Process the request message.
   *
   * @param msg the request message
   */
  @Override
  public void processRequestMsg(Request msg) {
    int vin = msg.getVin();
    
    if(!tracking.contains((Integer)vin))
        tracking.add((Integer)vin);
    
    // If the vehicle has got a reservation already, reject it.
    if (basePolicy.hasReservation(vin)) {
      basePolicy.sendRejectMsg(vin,
                               msg.getRequestId(),
                               Reject.Reason.CONFIRMED_ANOTHER_REQUEST);
      return;
    }
    //If pedestrian crossing over all crossings, reject any request.
    if (stopAll == true){
        basePolicy.sendRejectMsg(vin,msg.getRequestId(), Reject.Reason.NO_CLEAR_PATH);
        return;
    }
    // filter the proposals
    ProposalFilterResult filterResult =
      BasePolicy.standardProposalsFilter(msg.getProposals(),
                                         basePolicy.getCurrentTime());
    if (filterResult.isNoProposalLeft()) {
      basePolicy.sendRejectMsg(vin,
                               msg.getRequestId(),
                               filterResult.getReason());
    }
    
    List<Request.Proposal> proposals = filterResult.getProposals();

    // remove proposals that is not in the correct turn direction.
    removeProposalWithIncorrectTurnDirection(proposals);
    if (proposals.isEmpty()) {
      basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                               Reject.Reason.NO_CLEAR_PATH);
      return;
    }

    // try to see if reservation is possible for the remaining proposals.
    ReserveParam reserveParam =
      basePolicy.findReserveParam(msg, filterResult.getProposals());
    if (reserveParam != null) {
      basePolicy.sendComfirmMsg(msg.getRequestId(), reserveParam);
    } else {
      basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                               Reject.Reason.NO_CLEAR_PATH);
    }
  }
  
  private void removeProposalWithIncorrectTurnDirection(
                                             List<Request.Proposal> proposals) {
    for (Iterator<Request.Proposal> tpIter = proposals.listIterator();
         tpIter.hasNext();) {
      Request.Proposal p = tpIter.next();
      
      /*int arrivalLaneID = p.getArrivalLaneID(); //for debugging
      int departureLaneId = p.getDepartureLaneID();
      boolean lef = intersection.getLane(p.getArrivalLaneID()).hasLeftNeighbor();
      boolean rig = intersection.getLane(p.getArrivalLaneID()).hasRightNeighbor();
      
      boolean lefD = intersection.getLane(p.getDepartureLaneID()).hasLeftNeighbor();
      boolean rigD = intersection.getLane(p.getDepartureLaneID()).hasRightNeighbor();*/
      
      //Get turn direction and from where it will be turning from.
      TurnDirection turnDirection = intersection.calcTurnDirection(p.getArrivalLaneID(), p.getDepartureLaneID());
      CardinalDirection cd = intersection.getLaneCardinalDirection(p.getArrivalLaneID());
      
      //No vehicles can go straight when there are people crossing diagonally across the intersection.
      if((topRightToBottomLeft||topLeftToBottomRight)&&turnDirection==TurnDirection.STRAIGHT){
          tpIter.remove();
          break;
      }
      
      if(topRightToBottomLeft){
          //North and South
          if((cd==CardinalDirection.SOUTH||cd==CardinalDirection.NORTH)&&turnDirection==TurnDirection.LEFT){
            tpIter.remove();
            break;
          }//East and West can't turn right
          if((cd==CardinalDirection.EAST||cd==CardinalDirection.WEST)&&turnDirection==TurnDirection.RIGHT){
            tpIter.remove();
            break;  
          }//N and W can't turn if people crossing on top or left
          if((cd==CardinalDirection.NORTH||cd==CardinalDirection.WEST)&&(top||left)){
            tpIter.remove();
            break;   
          }//S and E can't turn if people crossing on bottom or right
          if((cd==CardinalDirection.SOUTH||cd==CardinalDirection.EAST)&&(right||bottom)){
            tpIter.remove();
            break;   
          }//If comming from W or E, only closest lane can turn.
          if(cd==CardinalDirection.WEST||cd==CardinalDirection.EAST){
            if(intersection.getLane(p.getArrivalLaneID()).hasLeftNeighbor()){
                tpIter.remove();
                break;
            }
          }
      }
      if(topLeftToBottomRight){
          //S and N can't turn right
          if((cd==CardinalDirection.SOUTH||cd==CardinalDirection.NORTH)&&turnDirection==TurnDirection.RIGHT){
            tpIter.remove();
            break;
          }//E and W can't turn left
          if((cd==CardinalDirection.EAST||cd==CardinalDirection.WEST)&&turnDirection==TurnDirection.LEFT){
            tpIter.remove();
            break;  
          }//N and E can't turn if people crossing on top or right
          if((cd==CardinalDirection.NORTH||cd==CardinalDirection.EAST)&&(top||right)){
            tpIter.remove();
            break;   
          }//S and W can't turn if people crossing on bottom or right
          if((cd==CardinalDirection.SOUTH||cd==CardinalDirection.WEST)&&(left||bottom)){
            tpIter.remove();
            break;   
          }//If comming from N or S, only closest lane can turn.
          if(cd==CardinalDirection.NORTH||cd==CardinalDirection.SOUTH){
            if(intersection.getLane(p.getArrivalLaneID()).hasLeftNeighbor()){
                tpIter.remove();
                break;
            }
          }
      }
      
      //No one is crossing diagonally accross now...
      
      if(top){
          //Reject all coming form the North
          if(cd==CardinalDirection.NORTH){
            tpIter.remove();
            break; 
          }//Handle people coming from W
          if(cd==CardinalDirection.WEST&&(left==true||turnDirection==TurnDirection.LEFT)){
            tpIter.remove();
            break;   
          }//Handle people coming from E
          if(cd==CardinalDirection.EAST&&(right==true||turnDirection==TurnDirection.RIGHT)){
            tpIter.remove();
            break;   
          }//Handle people coming from S
          if(cd==CardinalDirection.SOUTH&&(bottom==true||turnDirection==TurnDirection.STRAIGHT)){
            tpIter.remove();
            break;   
          }
      }
      if(right){
          //Reject all coming form the EAST
          if(cd==CardinalDirection.EAST){
            tpIter.remove();
            break; 
          }//Handle people coming from W
          if(cd==CardinalDirection.WEST&&(left==true||turnDirection==TurnDirection.STRAIGHT)){
            tpIter.remove();
            break;   
          }//Handle people coming from N
          if(cd==CardinalDirection.NORTH&&(top==true||turnDirection==TurnDirection.LEFT)){
            tpIter.remove();
            break;   
          }//Handle people coming from S
          if(cd==CardinalDirection.SOUTH&&(bottom==true||turnDirection==TurnDirection.RIGHT)){
            tpIter.remove();
            break;   
          }
      }
      if(bottom){
          //Reject all coming form the SOUTH
          if(cd==CardinalDirection.SOUTH){
            tpIter.remove();
            break; 
          }//Handle people coming from W
          if(cd==CardinalDirection.WEST&&(left==true||turnDirection==TurnDirection.RIGHT)){
            tpIter.remove();
            break;   
          }//Handle people coming from E
          if(cd==CardinalDirection.EAST&&(right==true||turnDirection==TurnDirection.LEFT)){
            tpIter.remove();
            break;   
          }//Handle people coming from N
          if(cd==CardinalDirection.NORTH&&(top==true||turnDirection==TurnDirection.STRAIGHT)){
            tpIter.remove();
            break;   
          }
      }
      if(left){
          //Reject all coming form the W
          if(cd==CardinalDirection.WEST){
            tpIter.remove();
            break; 
          }//Handle people coming from N
          if(cd==CardinalDirection.NORTH&&(top==true||turnDirection==TurnDirection.RIGHT)){
            tpIter.remove();
            break;   
          }//Handle people coming from E
          if(cd==CardinalDirection.EAST&&(right==true||turnDirection==TurnDirection.STRAIGHT)){
            tpIter.remove();
            break;   
          }//Handle people coming from S
          if(cd==CardinalDirection.SOUTH&&(bottom==true||turnDirection==TurnDirection.LEFT)){
            tpIter.remove();
            break;   
          }
      }
      
    }
  }
     
  void sendRejects(){
      for(Integer v : tracking){
          try{
            //notify vehicles that next step must revaluate path
            basePolicy.notifyVehicles(v);
          }catch(Exception e){
              //Vehicle couln't slow down
          }
      }
  }
  
  public void removeTracking(int vin){
      tracking.remove((Integer)vin);
  }

  /**
   * Get the statistic collector.
   *
   * @return the statistic collector
   */
  @Override
  public StatCollector<?> getStatCollector() {
    return null;
  }
  
  // BOOLEAN SETTERS //
  
  public void setLeft(){
      if(left==true)
        left=false;
      else{
        left=true;
        sendRejects();
      }
  }
  
  public void setRight(){
      if(right==true)
        right=false;
      else{
        right=true;
        sendRejects();
      }
  }
  
  public void setTop(){
      if(top==true)
        top=false;
      else{
        top=true;
        sendRejects();
      }
  }
  
  public void setBottom(){
      if(bottom==true)
        bottom=false;
      else{
        bottom=true;
        sendRejects();
      }
  }
  
  public void setTopLeftToBottomRight(){
      if(topLeftToBottomRight==true)
        topLeftToBottomRight=false;
      else{
        topLeftToBottomRight=true;
        sendRejects();
      }
  }
  
  public void setTopRightToBottomLeft(){
      if(topRightToBottomLeft==true)
        topRightToBottomLeft=false;
      else{
        topRightToBottomLeft=true;
        sendRejects();
      }
  }
  
  public void setStopAll(){
      if(stopAll==true)
        stopAll=false;
      else{
        stopAll=true;
        sendRejects();
      }
  }
  
  // SET WAITING (YELLOW)
  
  /*
  public void setLeftWaiting(){
      if(leftWaiting==true)
        leftWaiting=false;
      else{
        leftWaiting=true;
        sendRejects();
      }
  }
  
  public void setRightWaiting(){
      if(rightWaiting==true)
        rightWaiting=false;
      else{
        rightWaiting=true;
        sendRejects();
      }
  }
  
  public void setTopWaiting(){
      if(topWaiting==true)
        topWaiting=false;
      else{
        topWaiting=true;
        sendRejects();
      }
  }
  
  public void setBottomWaiting(){
      if(bottomWaiting==true)
        bottomWaiting=false;
      else{
        bottomWaiting=true;
        sendRejects();
      }
  }
  
  public void setTopLeftToBottomRightWaiting(){
      if(topLeftToBottomRightWaiting==true)
        topLeftToBottomRightWaiting=false;
      else{
        topLeftToBottomRightWaiting=true;
        sendRejects();
      }
  }
  
  public void setTopRightToBottomLeftWaiting(){
      if(topRightToBottomLeftWaiting==true)
        topRightToBottomLeftWaiting=false;
      else{
        topRightToBottomLeftWaiting=true;
        sendRejects();
      }
  }
  */
  
  /* public void setCrossWalk(int i) {
	  
	  if (i == 0 || i == 11) {
		  setLeft();
	  }
	  if (i == 1 || i == 7) {
		  setTopLeftToBottomRight();
	  }
	  if (i == 2 || i == 3) {
		  setTop();
	  }
	  if (i == 4 || i == 10) {
		  setTopRightToBottomLeft();
	  }
	  if (i == 5 || i == 6) {
		  setRight();
	  }
	  if (i == 8 || i == 9) {
		  setBottom();
	  }
  }
  */
  
  // BOOLEAN GETTERS //
  
  // getX() == RED
  
  public boolean getLeft() {
	  return left;
  }
  
  public boolean getRight() {
	  return right;
  }
  
  public boolean getTop() {
	  return top;
  }
  
  public boolean getBottom() {
	  return bottom;
  }
  
  public boolean getTopLeftToBottomRight() {
	  return topLeftToBottomRight;
  }
  
  public boolean getTopRightToBottomLeft() {
	  return topRightToBottomLeft;
  }
  
  public boolean getStopAll() {
	  return stopAll;
  }
  
  public ArrayList<PedestrianSpawnPoint> getPedestrianSpawnPoints(){
	  return psps;
  }
  
  public double getPedestrianLevel(){
	  return pedestrianLevel;
  }
  
  public double getMaxWaitTime(){
	  return maxWaitTime;
  }
  
  // getXWaiting() == YELLOW

  /*
  public boolean getLeftWaiting() {
	  return leftWaiting;
  }

  public boolean getTopWaiting() {
	  // TODO Auto-generated method stub
	  return topWaiting;
  }	

  public boolean getRightWaiting() {
	  // TODO Auto-generated method stub
	  return rightWaiting;
  }

  public boolean getBottomWaiting() {
	  // TODO Auto-generated method stub
	  return bottomWaiting;
  }

  public boolean getTopLeftToBottomRightWaiting() {
	  // TODO Auto-generated method stub
	  return topLeftToBottomRightWaiting;
  }

  public boolean getTopRightToBottomLeftWaiting() {
	  // TODO Auto-generated method stub
	  return topRightToBottomLeftWaiting;
  }
  */
}
