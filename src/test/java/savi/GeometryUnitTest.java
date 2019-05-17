package savi;

import org.junit.jupiter.api.Test;
import processing.core.PVector;
import savi.simulation.Geometry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryUnitTest {

	@Test
	void test() {
		// Test cases. refPosition is constant, try different relative target positions at different refAngles
		PVector refPosition = new PVector(5,5,5);
		for (double refAngle = 0; refAngle <= 2*Math.PI; refAngle += (Math.PI/36)) {
			selfTestCase(new PVector(5,5,5), refPosition, refAngle);

			selfTestCase(new PVector(5,5,6), refPosition, refAngle);
			selfTestCase(new PVector(5,6,5), refPosition, refAngle);
			selfTestCase(new PVector(5,6,6), refPosition, refAngle);
			selfTestCase(new PVector(6,5,5), refPosition, refAngle);
			selfTestCase(new PVector(6,5,6), refPosition, refAngle);
			selfTestCase(new PVector(6,6,5), refPosition, refAngle);
			selfTestCase(new PVector(6,6,6), refPosition, refAngle);
			
			selfTestCase(new PVector(5,5,4), refPosition, refAngle);
			selfTestCase(new PVector(5,4,5), refPosition, refAngle);
			selfTestCase(new PVector(5,4,4), refPosition, refAngle);
			selfTestCase(new PVector(4,5,5), refPosition, refAngle);
			selfTestCase(new PVector(4,5,4), refPosition, refAngle);
			selfTestCase(new PVector(4,4,5), refPosition, refAngle);
			selfTestCase(new PVector(4,4,4), refPosition, refAngle);
		}
	}
	
	/**
	 * Run an individual test case
	 * @param targetPosition
	 * @param refPosition
	 * @param refAngle
	 */
	void selfTestCase(PVector targetPosition, PVector refPosition, double refAngle) {
		// Run both methods under test
		List<Double> resultList = Geometry.relativePositionPolar(targetPosition, refPosition, refAngle);
		double azimuth = resultList.get(Geometry.AZIMUTH);
		double elevation = resultList.get(Geometry.ELEVATION);
		double range = resultList.get(Geometry.DISTANCE);
		PVector targetPositionPrime = Geometry.absolutePositionFromPolar(azimuth, elevation, range, refPosition, refAngle);
		
		// Set up result check
		float tolerance = (float) 0.1;
		String messageSuffix = new String(" component check for target at " + targetPosition.toString() + " and refAngle " + refAngle);
		String xMessage = "X" + messageSuffix;
		String yMessage = "Y" + messageSuffix;
		String zMessage = "Z" + messageSuffix;
				
		// Check the results
		assertEquals(targetPosition.x, targetPositionPrime.x, tolerance, xMessage);
		assertEquals(targetPosition.y, targetPositionPrime.y, tolerance, yMessage);
		assertEquals(targetPosition.z, targetPositionPrime.z, tolerance, zMessage);
		
		// Check the ranges
		assertTrue((0 <= azimuth) && (azimuth <= (2 * Math.PI)));				// azimuth must be between 0 and 2 PI
		assertTrue(((-Math.PI/2) <= elevation) && (elevation <= (Math.PI/2)));	// elevation must be between -PI/2 and PI/2
	}
}