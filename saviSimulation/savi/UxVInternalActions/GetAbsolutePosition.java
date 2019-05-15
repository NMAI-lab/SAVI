/**
 * Internal action for getting the absolute position from a relative position
 */

package savi.UxVInternalActions;


import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

import processing.core.PVector;

import savi.jason_processing.*;

public class GetAbsolutePosition extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;
	
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

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		// execute the internal action

		ts.getAg().getLogger().fine("executing internal action 'UxVInternalActions.absolutePosition'");

		try {
			// Get the parameters
			NumberTerm refPositionX = (NumberTerm) args[refPositionXIndex];
			NumberTerm refPositionY = (NumberTerm) args[refPositionYIndex];
			NumberTerm refPositionZ = (NumberTerm) args[refPositionZIndex];
			NumberTerm refAngle = (NumberTerm) args[refAngleIndex];
			NumberTerm azimuth = (NumberTerm) args[azimuthIndex];
			NumberTerm elevation = (NumberTerm) args[elevationIndex];
			NumberTerm range = (NumberTerm) args[rangeIndex];

			// Calculate the absolute position
			PVector refPosition = new PVector((float) refPositionX.solve(), (float) refPositionY.solve(),
					(float) refPositionZ.solve());
			PVector position = Geometry.absolutePositionFromPolar(azimuth.solve(), elevation.solve(), range.solve(),
					refPosition, refAngle.solve());

			// Create the result term
			NumberTerm resultX = new NumberTermImpl(position.x);
			NumberTerm resultY = new NumberTermImpl(position.y);
			NumberTerm resultZ = new NumberTermImpl(position.z);

			// Unify
			boolean xSuccess = un.unifies(resultX, args[targetPositionXIndex]);
			boolean ySuccess = un.unifies(resultY, args[targetPositionYIndex]);
			boolean zSuccess = un.unifies(resultZ, args[targetPositionZIndex]);

			// Return result
			return (xSuccess && ySuccess && zSuccess);
		}
		// Deal with error cases
		catch (ArrayIndexOutOfBoundsException e) {
			throw new JasonException("The internal action 'absolutePosition' received the wrong number of arguements.");
		} catch (ClassCastException e) {
			throw new JasonException(
					"The internal action 'absolutePosition' received arguements that are of the wrong type.");
		} catch (Exception e) {
			throw new JasonException("Error in 'absolutePosition'.");
		}
	}
}
