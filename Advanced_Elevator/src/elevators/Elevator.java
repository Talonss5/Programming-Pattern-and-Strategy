package elevators;


import events.ElevatorModeEvent;
import buildings.Building;
import buildings.Floor;
import buildings.FloorObserver;
import events.ElevatorStateEvent;
import passengers.Passenger;

import java.util.*;
import java.util.stream.Collectors;
import logging.Logger;
import logging.StandardOutLogger;

public class Elevator implements FloorObserver {
	
	public enum ElevatorState {
		IDLE_STATE,
		DOORS_OPENING,
		DOORS_CLOSING,
		DOORS_OPEN,
		ACCELERATING,
		DECELERATING,
		MOVING
	}
	
	public enum Direction {
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	
	private int mNumber;
	private Building mBuilding;
        private OperationMode mOperation;

	private ElevatorState mCurrentState;
	private Direction mCurrentDirection;
	private Floor mCurrentFloor;
	private List<Passenger> mPassengers = new ArrayList<>();
	private List<ElevatorObserver> mObservers = new ArrayList<>();
    // TODO: declare a field to keep track of which floors have been requested by passengers.
	private boolean[] RequestedFloor;
        private boolean isDispatching=false;
        
	public Elevator(int number, Building bld) {
                mBuilding = bld;
                this.RequestedFloor = new boolean[mBuilding.getFloorCount()];
		mNumber = number;
                for(int i=0;i<RequestedFloor.length;i++){
                    RequestedFloor[i]=false;
                }
		mCurrentFloor = bld.getFloor(1);
                mOperation=new IdleMode();
                this.setCurrentDirection(Elevator.Direction.NOT_MOVING);
                this.scheduleStateChange(Elevator.ElevatorState.IDLE_STATE, 0);
                mCurrentFloor.addObserver(this);
                
	}
        
        public void setIsdispatching(boolean o){
            isDispatching=o;
        }
        
        public void tick(){
            getOperation().tick(this);
        }
	
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	public void scheduleStateChange(ElevatorState state, long timeFromNow) {
            Simulation sim = mBuilding.getSimulation();
            sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, this));
	}
        
        public void scheduleModeChange(OperationMode newMode,Elevator.ElevatorState newState,long TimeFromNow){
            Simulation sim = mBuilding.getSimulation();
            sim.scheduleEvent(new ElevatorModeEvent(sim.currentTime() + TimeFromNow, newMode, newState, this));
        }
	public OperationMode getOperation(){
            return mOperation;
        }
	/**
	 * Adds the given passenger to the elevator's list of passengers, and requests the passenger's destination floor.
	 */
	public void addPassenger(Passenger passenger) {
		// TODO: add the passenger's destination to the set of requested floors.
                StandardOutLogger log=new StandardOutLogger(this.getBuilding().getSimulation());
                Logger.setInstance(log);
		mPassengers.add(passenger);
	}
	
	public void removePassenger(Passenger passenger) {
		mPassengers.remove(passenger);
                //System.out.print("Elevator: remove passenger");
	}
	
	public int nextRequestUp(int FromFloor){
            int smallestmax=-1;
            for (int i=0;i<RequestedFloor.length;i++){
                if(RequestedFloor[i]==true && (i+1)>FromFloor){
                    smallestmax=i+1;
                }
                
            }
            return smallestmax;
        }
        
        public int nextRequestDown(int FromFloor){
            int biggestmin=-1;
            for (int i=0;i<RequestedFloor.length;i++){
                if(RequestedFloor[i]==true && (i+1)<FromFloor){
                    biggestmin=i+1;
                }  
            }
            return biggestmin;
        }
	public void announceElevatorIdle(){
            ArrayList<ElevatorObserver> temp=new ArrayList<>(mObservers);
            for (ElevatorObserver m:temp){
                m.elevatorWentIdle(this);
            }
        }
        
        public void announceElevatorDecelerating(){
            ArrayList<ElevatorObserver> temp=new ArrayList<>(mObservers);
            for (ElevatorObserver m:temp){
                m.elevatorDecelerating(this);
            }
        }
            
            
        public boolean[] getRequestedFloor(){
            return RequestedFloor;
        }
        public void requestFloor(Floor floor){
            if(floor!=mCurrentFloor){
                RequestedFloor[floor.getNumber()-1]=true;
            }
        }
        public void unrequestFloor(Floor floor){
            RequestedFloor[floor.getNumber()-1]=false;
        }
        
        public ArrayList<Integer> getRequestFloor(){
            ArrayList<Integer> output=new ArrayList<>();
            if(!this.isIdle() && isDispatching==false){
                for(int i=0;i<RequestedFloor.length;i++){
                    if(RequestedFloor[i]==true){
                        output.add(i+1);
                    }
                }
            }
            return output;
    

        }
	
	// Simple accessors
	public Floor getCurrentFloor() {
		return mCurrentFloor;
	}
	
	public Direction getCurrentDirection() {
		return mCurrentDirection;
	}
        public ElevatorState getCurrentState(){
            return mCurrentState;
        }
	
	public Building getBuilding() {
		return mBuilding;
	}
	
	/**
	 * Returns true if this elevator is in the idle state.
	 * @return
	 */
	public boolean isIdle() {
		// TODO: complete this method.
                if(mCurrentState==ElevatorState.IDLE_STATE){
                    return true;
                }
		return false;
	}
        
        public List<Passenger> getPassenger(){
            return mPassengers;
        }
	
	// All elevators have a capacity of 10, for now.
	public int getCapacity() {
		return 10;
	}
	
	public int getPassengerCount() {
		return mPassengers.size();
	}
        
	
	// Simple mutators
	public void setState(ElevatorState newState) {
		mCurrentState = newState;
	}
        
        public void setOperationMode(OperationMode newOperation){
            mOperation=newOperation;
        }
	
	public void setCurrentDirection(Direction direction) {
		mCurrentDirection = direction;
	}
	
	public void setCurrentFloor(Floor floor) {
		mCurrentFloor = floor;
	}
	public List<ElevatorObserver> getObserver(){
            return mObservers;
        }
	// Observers
	public void addObserver(ElevatorObserver observer) {
		mObservers.add(observer);
	}
	
	public void removeObserver(ElevatorObserver observer) {
		mObservers.remove(observer);
	}
	
        public int getNumber(){
            return mNumber;
        }
        
        public boolean isDispatchingto(Floor floor){
            return RequestedFloor[floor.getNumber()-1];
        }
	
	// FloorObserver methods
	
	
        @Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// Not used.
	}
	
	/**
	 * Triggered when our current floor receives a direction request.
	 */
	
        @Override
	public void directionRequested(Floor sender, Direction direction) {
		// TODO: if we are currently idle, change direction to match the request. Then alert all our observers that we are decelerating,
		// TODO: then schedule an immediate state change to DOORS_OPENING.
                this.getOperation().directionRequested(this, sender, direction);

	}
	
	// Voodoo magic.
	@Override
	public String toString() {
		return "Elevator " + mNumber + "["+this.getOperation()+"]" +" - " + mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
		 + "[" + mPassengers.stream().map(p -> p.getShortName()+p.getId()).collect(Collectors.joining(", "))
		 + "]"+this.getRequestFloor();
	}
	
}
