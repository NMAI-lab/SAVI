/**
package UxVInternalActions;
public class GetAbsolutePosition extends DefaultInternalAction {
    @Override
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'UxVInternalActions.absolutePosition'");
        
		try {
			// Get the parameters
			NumberTerm refPositionX = (NumberTerm)args[refPositionXIndex];
			// Calculate the absolute position
			// Create the result term
			NumberTerm resultX = new NumberTermImpl(position.x);
			NumberTerm resultY = new NumberTermImpl(position.y);
			NumberTerm resultZ = new NumberTermImpl(position.z);
			
			// Unify
			boolean xSuccess = un.unifies(resultX,args[targetPositionXIndex]);
			boolean ySuccess = un.unifies(resultY,args[targetPositionYIndex]);
			boolean zSuccess = un.unifies(resultZ,args[targetPositionZIndex]);
			
			// Return result
			return (xSuccess && ySuccess && zSuccess);
		}	
		// Deal with error cases
		catch(ArrayIndexOutOfBoundsException e) {
			throw new JasonException("The internal action 'absolutePosition' received the wrong number of arguements.");
		}
		catch(ClassCastException e) {
			throw new JasonException("The internal action 'absolutePosition' received arguements that are of the wrong type.");
		}
		catch (Exception e) {
			throw new JasonException("Error in 'absolutePosition'.");
		}
    }
}
