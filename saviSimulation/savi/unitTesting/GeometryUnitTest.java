package savi.unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import processing.core.PVector;
import savi.jason_processing.*;

class GeometryUnitTest {

	@Test
	void test() {
		selfTestCase(new PVector(6,5,5), new PVector(5,5,5), 0);
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