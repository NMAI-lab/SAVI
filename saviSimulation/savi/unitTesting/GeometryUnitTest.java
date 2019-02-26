package savi.unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import processing.core.PVector;
import savi.jason_processing.*;

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
		List<Double> resultList = Geometry.relativePositionPolar(targetPosition, refPosition, refAngle);
		double azimuth = resultList.get(Geometry.AZIMUTH);
		double elevation = resultList.get(Geometry.ELEVATION);
		double range = resultList.get(Geometry.DISTANCE);
		PVector targetPositionPrime = Geometry.absolutePositionFromPolar(azimuth, elevation, range, refPosition, refAngle);
		assertTrue(targetPosition.equals(targetPositionPrime));
	}
}