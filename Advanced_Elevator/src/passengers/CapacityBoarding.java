package passengers;

import elevators.Elevator;

/**
 * A CapacityBoarding is a boarding strategy for a Passenger that will get on any elevator that has not reached its
 * capacity.
 */
public class CapacityBoarding implements BoardingStrategy {
    public CapacityBoarding(){
        
    }
    @Override
    public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
	return elevator.getPassengerCount() < elevator.getCapacity();
	
    }
}
