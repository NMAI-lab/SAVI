package savi.StateSynchronization;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TimePerceptionTest {

	@Test
	void test() {
		// Make a simple perception to start
		double timeStamp = 1.0;
		Perception testCase = new TimePerception(timeStamp);
		Perception testCaseLost = testCase.clone();
		testCaseLost.perceptionLost();
		
		// Make expected String version of the perception
		String expected = new String("time(1)");
		String lostExpected = new String("timelost(1)");
		
		// Test toString and Literal methods
		assertTrue(expected.equals(testCase.toString()));
		assertTrue(expected.equals(testCase.getLiteral().toString()));
		assertTrue(lostExpected.equals(testCaseLost.toString()));
		assertTrue(lostExpected.equals(testCaseLost.getLiteral().toString()));
		
		// Verify that the perception name and type works properly
		String perceptionName = new String("time");
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
		Perception similarTestCase = new TimePerception(timeStamp + 0.001);
		assertTrue(similarTestCase.isSimilar(testCase));
		Perception differentTestCase = new TimePerception(timeStamp + 10000);
		assertFalse(differentTestCase.isSimilar(testCase));
	}
}
