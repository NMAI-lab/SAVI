/**
 * Internal action for getting the relative position from an absolute position.
 * 
 * Term format:
 * GetRelativePosition(targetPositionX,targetPositionY,targetPositionZ,refPositionX,refPositionY,refPositionZ,refAngle,azimuth,elevation,range)
 * 
 * Inputs: targetPositionX,targetPositionY,targetPositionZ,refPositionX,refPositionY,refPositionZ,refAngle
 * Outputs: azimuth,elevation,range
 */

package savi.UxVInternalActions;

import java.util.List;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

import processing.core.PVector;

import savi.jason_processing.*;

public class GetRelativePosition extends DefaultInternalAction {

	private static final long serialVersionUID = 2L;

	public static final int targetPositionXIndex = 0;
	public static final int targetPositionYIndex = 1;
	public static final int targetPositionZIndex = 2;
	public static final int refPositionXIndex = 3;
	public static final int refPositionYIndex = 4;
	public static final int refPositionZIndex = 5;
	public static final int refAngleIndex = 6;
	public static final int azimuthIndex = 7;
	public static final int elevationIndex = 8;
	public static final int rangeIndex = 9;

	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		// execute the internal action
		ts.getAg().getLogger().info("executing internal action 'UxVInternalActions.absolutePosition'");

		try {
			// Get the parameters
			NumberTerm targetPositionX = (NumberTerm) args[targetPositionXIndex];
			NumberTerm targetPositionY = (NumberTerm) args[targetPositionYIndex];
			NumberTerm targetPositionZ = (NumberTerm) args[targetPositionZIndex];
			NumberTerm refPositionX = (NumberTerm) args[refPositionXIndex];
			NumberTerm refPositionY = (NumberTerm) args[refPositionYIndex];
			NumberTerm refPositionZ = (NumberTerm) args[refPositionZIndex];
			NumberTerm refAngle = (NumberTerm) args[refAngleIndex];

			// Calculate the relative position
			PVector targetPosition = new PVector((float) targetPositionX.solve(), (float) targetPositionY.solve(),
					(float) targetPositionZ.solve());
			PVector refPosition = new PVector((float) refPositionX.solve(), (float) refPositionY.solve(),
					(float) refPositionZ.solve());
			List<Double> positionData = Geometry.relativePositionPolar(targetPosition, refPosition,
					(double) refAngle.solve());

			// Create the result term
			NumberTerm resultAzimuth = new NumberTermImpl(positionData.get(Geometry.AZIMUTH));
			NumberTerm resultElevation = new NumberTermImpl(positionData.get(Geometry.ELEVATION));
			NumberTerm resultRange = new NumberTermImpl(positionData.get(Geometry.DISTANCE));

			// Unify
			boolean azimuthSuccess = un.unifies(resultAzimuth, args[azimuthIndex]);
			boolean elevationSuccess = un.unifies(resultElevation, args[elevationIndex]);
			boolean rangeSuccess = un.unifies(resultRange, args[rangeIndex]);

			// Return result
			return (azimuthSuccess && elevationSuccess && rangeSuccess);
		}
		// Deal with error cases
		catch (ArrayIndexOutOfBoundsException e) {
			throw new JasonException("The internal action 'relativePosition' received the wrong number of arguements.");
		} catch (ClassCastException e) {
			throw new JasonException(
					"The internal action 'relativePosition' received arguements that are of the wrong type.");
		} catch (Exception e) {
			throw new JasonException("Error in 'relativePosition'.");
		}
	}
}
