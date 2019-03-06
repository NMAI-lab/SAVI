package savi.unitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import savi.StateSynchronization.CameraPerception;
import savi.StateSynchronization.Perception;
import savi.StateSynchronization.PerceptionSnapshot;
import savi.StateSynchronization.TimePerception;

public class PerceptionSnapshotUnitTest {
	
	/**
	 * Test the case where there is an empty PerceptionSnapshot
	 * @param verbose
	 * @return
	 */
	private void emptyTest() {
		// Constructor
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();

		// addPerception
		testSnapshot.addPerception(null);
		
		// Check the empty perception list
		assertTrue(testSnapshot.getPerceptionList().size() == 0);
		
		// Check the empty perception list - latest version
		assertTrue(testSnapshot.getLatestTimeStamp() == -1);
		
		// Check the empty perception list - list of literals
		assertTrue(testSnapshot.getLiterals().isEmpty());
		
		// Check the empty perception list - toString
		assertTrue(testSnapshot.toString().equals("[]"));
				
		// Check for similar perception in an empty snapshot
		double timeStamp = 8.0;
		Perception testPerception1 = new TimePerception(timeStamp);
		assertTrue(testSnapshot.pullSimilarPerception(testPerception1) == null);
		
		// Use the copy constructor and check the list of perceptions
		PerceptionSnapshot copiedSnahpshot = new PerceptionSnapshot(testSnapshot);
		assertTrue(copiedSnahpshot.getPerceptionList().size() == 0);
	}
	
	

	private void contentTest() {
		// Constructor
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();
		
		// Make some Perceptions for testing with
		double timeStamp = 1.0;
		String perceptionName = new String("threat");
		double azumuth = 2.0;
		double elevation = 3.0;
		double range = 4.0;
		double moreRecentTimeStep = 2.0;
		
		double radius=0.0;
		
		Perception testPerception1 = new TimePerception(timeStamp);
		Perception testPerception2 = new CameraPerception(perceptionName, moreRecentTimeStep, azumuth, elevation, range, radius);
		Perception similarPerception = new CameraPerception(perceptionName, timeStamp, azumuth + 0.001, elevation, range, radius);
		Perception differentPerception = new CameraPerception(perceptionName, timeStamp, azumuth + 10000, elevation, range, radius);
		Perception samePerception = testPerception2.clone();
		
		// addPerceptions to the snapshot
		testSnapshot.addPerception(testPerception1);
		testSnapshot.addPerception(testPerception2);
		
		// Check the size of the perception list
		assertTrue(testSnapshot.getPerceptionList().size() == 2);
		
		// Check the perception list - latest version
		assertTrue(testSnapshot.getLatestTimeStamp() == moreRecentTimeStep);
		
		// Check the perception list - list of literals
		assertTrue(testSnapshot.getLiterals().contains(testPerception1.getLiteral()));
		assertTrue(testSnapshot.getLiterals().contains(testPerception2.getLiteral()));
		assertFalse(testSnapshot.getLiterals().contains(similarPerception.getLiteral()));
		
		// Check the empty perception list - toString
		assertTrue(testSnapshot.toString().equals(new String("[" + testPerception1.toString() + ", " + testPerception2.toString() + "]")));
				
		// Check if the same perception is there
		assertTrue(testSnapshot.pullSimilarPerception(samePerception).equals(samePerception));
		
		// Check if a similar perception is there (will fail, it was removed by previous step)
		assertTrue(testSnapshot.pullSimilarPerception(similarPerception) == null);
		
		// Put it back, try again
		testSnapshot.addPerception(testPerception2);
		assertTrue(testSnapshot.pullSimilarPerception(similarPerception).equals(testPerception2));
		// Make sure it was removed
		assertTrue(testSnapshot.pullSimilarPerception(similarPerception) == null);
		
		// Check if a completely different perception is in the list
		testSnapshot.addPerception(testPerception2);
		assertTrue(testSnapshot.pullSimilarPerception(differentPerception) == null);
		
		// Use the copy constructor and check the list of perceptions
		PerceptionSnapshot copiedSnapshot = new PerceptionSnapshot(testSnapshot);
		assertTrue(copiedSnapshot.getPerceptionList().size() == 2);
		assertTrue(copiedSnapshot.getLiterals().contains(testPerception1.getLiteral()));
		assertTrue(copiedSnapshot.getLiterals().contains(testPerception2.getLiteral()));
		assertFalse(copiedSnapshot.getLiterals().contains(similarPerception.getLiteral()));
		
		// Make a new Perception, put something different in it, then addPerceptionsFromSnapshot()
		PerceptionSnapshot snapshotToAdd = new PerceptionSnapshot();
		snapshotToAdd.addPerception(differentPerception);
		copiedSnapshot.addPerceptionsFromSnapshot(snapshotToAdd);
		assertTrue(copiedSnapshot.getPerceptionList().size() == 3);
		assertTrue(copiedSnapshot.getLiterals().contains(testPerception1.getLiteral()));
		assertTrue(copiedSnapshot.getLiterals().contains(testPerception2.getLiteral()));
		assertTrue(copiedSnapshot.getLiterals().contains(differentPerception.getLiteral()));
		assertFalse(copiedSnapshot.getLiterals().contains(similarPerception.getLiteral()));
	}
	
	
	@Test
	void test() {
		
		System.out.println(new String());
		
		// Test the case where there is an empty PerceptionSnapshot involved.
		this.emptyTest();
		
		// Test a case where the PerceptionSnapshot is not empty
		this.contentTest();
	}
}
