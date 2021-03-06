package savi.util;

import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

public class Geometry {

	public static int AZIMUTH = 0;
	public static int ELEVATION = 1;
	public static int DISTANCE = 2;
	
	/**
	 * returns azimuth, elevation, distance
	 * @param targetPosition
	 * @param refPosition
	 * @param refAngle
	 * @return
	 * 
	 * Should implement the equations at the bottom of the first page of this:
	 * https://computitos.files.wordpress.com/2008/03/cartesian_spherical_transformation.pdf
	 */
	public static List<Double> relativePositionPolar(PVector targetPosition, PVector refPosition, double refAngle){
		
		// Calculate distance                                                                                 
		double dist  = 	refPosition.dist(targetPosition);          
		
		// Calculate the azimuth
		PVector myXY = new PVector(refPosition.x,refPosition.y, 0);
		PVector objXY = new PVector(targetPosition.x,targetPosition.y, 0);
		double theta1 = objXY.sub(myXY).heading();
		double azimuth = theta1 - refAngle;

		// to normalize between 0 to 2 Pi                                                                
		azimuth = Geometry.normalize02PI(azimuth);
		
		// Calculate the elevation and deal with NaN case
		PVector difference = targetPosition.copy().sub(refPosition);
		double elevation = Math.asin(difference.z/dist);
		
		if (Double.isNaN(elevation)) {
			elevation = 0;
		}

		// Return the result
		List<Double> toreturn = new ArrayList<Double>();
		toreturn.add(azimuth);
		toreturn.add(elevation);
		toreturn.add(dist);
		return toreturn;
	}
	
	
	/**
	 * Returns x,y,z position based on a relative (spherical) position
	 * @return
	 * 
	 * Should implement the equations at the bottom of the first page of this:
	 * https://computitos.files.wordpress.com/2008/03/cartesian_spherical_transformation.pdf
	 */
	public static PVector absolutePositionFromPolar(double azimuth, double elevation, double range, PVector refPosition, double refAngle) {
		
		// Account for refAngle
		double trueAzimuth = azimuth + refAngle;
		
		// Calculate relative Cartesian position
		double targetX = range * Math.cos(elevation) * Math.cos(trueAzimuth);
		double targetY = range * Math.cos(elevation) * Math.sin(trueAzimuth);
		double targetZ = range * Math.sin(elevation);
		
		// Shift to be an absolute Cartesian position
		float x = refPosition.x + (float)targetX;
		float y = refPosition.y + (float)targetY;
		float z = refPosition.z + (float)targetZ;
		
		// Return the result
		PVector absolutePosition = new PVector(x,y,z);
		return absolutePosition;
	}

	public static double normalize02PI(double angle) {
		while (angle < 0)                                                                                    
			angle += (2 * Math.PI);
		
		while (angle > (2 * Math.PI))
			angle -= (2 * Math.PI);
	
		return angle;
	}

	public static double normalizeMinusPIPI(double angle) {
		while (angle < -Math.PI)                                                                                    
			angle += 2* (Math.PI);
		
		while (angle > Math.PI)
			angle -= 2* (Math.PI);
	
		return angle;
	}


	//normalize polar angles elevation / azimuth to account for case where object is "behind" agent
	public static double[] normalizePolar(double azimuth, double elevation) {
		double azimuth2 = normalize02PI(azimuth);
		double elevation2 = normalizeMinusPIPI(elevation);
		if (elevation2 > 0.5 * Math.PI) {
			elevation2 = Math.PI - elevation2;
			azimuth2 = normalize02PI(azimuth2 +Math.PI);
		} else if (elevation2 < -0.5 * Math.PI){
			elevation2 = -Math.PI - elevation2;
			azimuth2 = normalize02PI(azimuth2 +Math.PI);
		}
		return new double [] {azimuth2, elevation2};
	}
	
}