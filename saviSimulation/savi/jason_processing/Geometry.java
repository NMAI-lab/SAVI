package savi.jason_processing;

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
	 */
	public static List<Double> relativePositionPolar(PVector targetPosition, PVector refPosition, double refAngle){
		
		//calculate distance                                                                                 
		double dist  = 	refPosition.dist(targetPosition);//Math.sqrt(deltax*deltax + deltay*deltay);          
		PVector myXY = new PVector(refPosition.x,refPosition.y, 0);                                  
		PVector objXY = new PVector(targetPosition.x,targetPosition.y, 0);                                     
			                                                                                                 
		double theta1 = objXY.sub(myXY).heading();   // Math.atan2(deltay, deltax);                      
		double azimuth = theta1 - refAngle ;// % 2* Math.PI; //(adjust to 0, 2pi) interval       
			// to normalize between 0 to 2 Pi                                                                
		while(azimuth<0)                                                                                    
			azimuth+=2*Math.PI;  
		
		PVector difference = targetPosition.copy().sub(refPosition); 

		PVector projection = new PVector(difference.x,difference.z, 0);

		double elevation = PVector.angleBetween(projection, difference);
		if (elevation > Math.PI *0.5) {
			elevation = Math.PI *0.5 - elevation;
		}

		List<Double> toreturn = new ArrayList<Double>();
		toreturn.add(azimuth);
		toreturn.add(elevation);
		toreturn.add(dist);
		return toreturn;
	}
}
