package savi.StateSynchronization;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PositionPerceptionTest {

	@Test
	void test() {
		// Make a simple perception to start
		String perceptionName = new String("position");
		double timeStamp = 1.0;
		double x = 2.0;
		double y = 3.0;
		double z = 4.0;
		Perception testCase = new PositionPerception(timeStamp, x, y, z);
		Perception testCaseLost = testCase.clone();
		testCaseLost.perceptionLost();
		
		// Make expected String version of the perception
		String expected = new String("position(2,3,4,1)");
		String lostExpected = new String("positionlost(2,3,4,1)");
		
		// Test toString and Literal methods
		assertTrue(expected.equals(testCase.toString()));
		assertTrue(expected.equals(testCase.getLiteral().toString()));
		assertTrue(lostExpected.equals(testCaseLost.toString()));
		assertTrue(lostExpected.equals(testCaseLost.getLiteral().toString()));
		
		// Verify that the perception name and type works properly
		assertTrue(perceptionName.equals(testCase.getPerceptionName()));
		assertTrue(testCase.comparePerceptionName(testCase.clone()));
		assertFalse(testCase.comparePerceptionName(testCaseLost));
		
		// Verify that the perception lost boolean worked properly
		assertFalse(testCase.isLost());
		assertTrue(testCaseLost.isLost());
		
		// Verify the time stamp
		assertTrue(testCase.getTimeStamp() == timeStamp);
		
		// Check equals method
		assertTrue(testCase.equals(testCase.clone()));
		assertFalse(testCase.equals(testCaseLost));
		
		// Check perception type
		assertTrue(testCase.comparePerceptionType(testCaseLost));
		assertTrue(testCase.comparePerceptionType(testCase.clone()));
		
		// Check getDifference and isSimilar
		assertTrue(testCase.isSimilar(testCase.clone()));
		assertTrue(testCase.getDifference(testCase.clone()) == 0);
		assertFalse(testCase.isSimilar(testCaseLost));
		assertTrue(testCase.getDifference(testCaseLost) == 1);
		assertFalse(testCase.isSimilar(null));
		assertTrue(testCase.getDifference(null) == 1);
		Perception similarTestCase = new PositionPerception(timeStamp, x + 0.001, y, z);
		assertTrue(similarTestCase.isSimilar(testCase));
		Perception differentTestCase = new PositionPerception(timeStamp, x + 10000, y, z);
		assertFalse(differentTestCase.isSimilar(testCase));
	}
}
