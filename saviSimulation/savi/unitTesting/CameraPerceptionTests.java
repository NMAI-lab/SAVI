package savi.unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import processing.core.PVector;
import savi.StateSynchronization.CameraPerception;
import savi.StateSynchronization.Perception;
import savi.StateSynchronization.TimePerception;

class CameraPerceptionTests {

	@Test
	void test() {
		// Make a simple perception to start
		String perceptionName = new String("threat");
		String type = new String("car");
		double timeStamp = 1.0;
		double azumuth = 2.0;
		double elevation = 3.0;
		double range = 4.0;
		double radius = 0.0;
		Perception testCase = new CameraPerception(perceptionName, timeStamp, azumuth, elevation, range, radius);
		Perception testCaseWithType = new CameraPerception(perceptionName, type, timeStamp, azumuth, elevation, range, radius);
		Perception testCaseLost = testCase.clone();
		testCaseLost.perceptionLost();
		
		// Make expected String version of the perception
		String expected = new String("threat(2,3,4,0,1,unknown)");
		String expectedWithType = new String("threat(2,3,4,0,1,car)");
		String lostExpected = new String("threatlost(2,3,4,0,1,unknown)");
		
		// Test toString and Literal methods
		assertTrue(expected.equals(testCase.toString()));
		assertTrue(expected.equals(testCase.getLiteral().toString()));
		assertTrue(expectedWithType.equals(testCaseWithType.toString()));
		assertTrue(expectedWithType.equals(testCaseWithType.getLiteral().toString()));
		assertFalse(expected.equals(testCaseWithType.toString()));
		assertFalse(expected.equals(testCaseWithType.getLiteral().toString()));
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
		assertFalse(testCase.equals(testCaseWithType));
		
		// Check perception type
		assertTrue(testCase.comparePerceptionType(testCaseLost));
		assertTrue(testCase.comparePerceptionType(testCase.clone()));
		assertTrue(testCaseWithType.comparePerceptionType(testCaseWithType.clone()));
		assertFalse(testCase.comparePerceptionType(testCaseWithType));
		assertTrue(testCaseWithType.getPerceptionType().equals(type));
		
		
		// Check getDifference and isSimilar
		assertTrue(testCase.isSimilar(testCase.clone()));
		assertTrue(testCase.getDifference(testCase.clone()) == 0);
		assertFalse(testCase.isSimilar(testCaseWithType));
		assertTrue(testCase.getDifference(testCaseWithType) == 1);
		assertFalse(testCase.isSimilar(testCaseLost));
		assertTrue(testCase.getDifference(testCaseLost) == 1);
		assertFalse(testCase.isSimilar(null));
		assertTrue(testCase.getDifference(null) == 1);
		Perception similarTestCase = new CameraPerception(perceptionName, timeStamp, azumuth + 0.001, elevation, range, radius);
		assertTrue(similarTestCase.isSimilar(testCase));
		Perception differentTestCase = new CameraPerception(perceptionName, timeStamp, azumuth + 10000, elevation, range, radius);
		assertFalse(differentTestCase.isSimilar(testCase));
		
		Perception veryDifferent = new TimePerception(timeStamp);
		assertFalse(testCase.isSimilar(veryDifferent));
		assertTrue(testCase.getDifference(veryDifferent) == 1);
	}
}
