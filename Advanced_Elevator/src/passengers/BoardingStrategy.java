package passengers;

import elevators.Elevator;

/**
 * A BoardingStrategy specifies rules for deciding when a Passenger will board an Elevator that has opened
 * its doors.
 */
public interface BoardingStrategy {
	/**
	 * Returns true if the given passenger will board the given elevator.
         * @param passenger
         * @param elevator
         * @return 
	 */
	boolean willBoardElevator(Passenger passenger, Elevator elevator);
}
