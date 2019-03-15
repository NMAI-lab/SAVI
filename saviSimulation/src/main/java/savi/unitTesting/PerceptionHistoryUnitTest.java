package savi.unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import savi.StateSynchronization.Perception;
import savi.StateSynchronization.PerceptionHistory;
import savi.StateSynchronization.PerceptionSnapshot;
import savi.StateSynchronization.PositionPerception;

public class PerceptionHistoryUnitTest {
	
	@Test
	void test() {
		testUpdatePerceptionsChangesOnly();
		updatePerceptionsPassThrough();
	}
	
	private void testUpdatePerceptionsChangesOnly() {
		// Make a PerceptionHistory
		PerceptionHistory history = new PerceptionHistory();
		
		// Setup the first update.
		double timeStamp = 1;
		double x = 2;
		double y = 3;
		double z = 4;
		Perception testPerception1 = new PositionPerception(timeStamp,x, y, z);
		Perception similarPerception = new PositionPerception(1,x + 0.001, y, z);
		Perception differentPerception = new PositionPerception(50,x + 1000, y, z);
	
		timeStamp = 1;
		x = 5;
		y = 6;
		z = 7;
		Perception testPerception2 = new PositionPerception(timeStamp,x, y, z);

		PerceptionSnapshot firstUpdate = new PerceptionSnapshot();
		firstUpdate.addPerception(testPerception1);
		firstUpdate.addPerception(testPerception2);
		
		String firstUpdateString = history.updatePerceptionsChangesOnly(firstUpdate).toString();
		assertTrue(firstUpdateString.equals(firstUpdate.toString()));
		
		// Make the update, will have similar and different
		PerceptionSnapshot secondUpdate = new PerceptionSnapshot();
		secondUpdate.addPerception(similarPerception);
		secondUpdate.addPerception(differentPerception);
		
		String secondUpdateString = history.updatePerceptionsChangesOnly(secondUpdate).toString();
		assertTrue(secondUpdateString.equals("[positionlost(5,6,7,1), position(1002,3,4,50)]"));
	}
	
	
	private void updatePerceptionsPassThrough() {
		// Make a PerceptionHistory
		PerceptionHistory history = new PerceptionHistory();
		
		// Setup the first update.
		double timeStamp = 1;
		double x = 2;
		double y = 3;
		double z = 4;
		Perception testPerception1 = new PositionPerception(timeStamp,x, y, z);
		Perception similarPerception = new PositionPerception(1,x + 0.001, y, z);
		Perception differentPerception = new PositionPerception(50,x + 1000, y, z);
	
		timeStamp = 1;
		x = 5;
		y = 6;
		z = 7;
		Perception testPerception2 = new PositionPerception(timeStamp,x, y, z);

		PerceptionSnapshot firstUpdate = new PerceptionSnapshot();
		firstUpdate.addPerception(testPerception1);
		firstUpdate.addPerception(testPerception2);
		
		String firstUpdateString = history.updatePerceptionsPassThrough(firstUpdate).toString();
		assertTrue(firstUpdateString.equals(firstUpdate.toString()));
		
		// Make the update, will have similar and different
		PerceptionSnapshot secondUpdate = new PerceptionSnapshot();
		secondUpdate.addPerception(similarPerception);
		secondUpdate.addPerception(differentPerception);
		
		String secondUpdateString = history.updatePerceptionsPassThrough(secondUpdate).toString();
		assertTrue(secondUpdateString.equals("[position(2.001,3,4,1), position(1002,3,4,50)]"));
	}
}
