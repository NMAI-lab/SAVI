// Internal action code for project debugging.mas2j
package UxVInternalActions;
import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
public class absolutePosition extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'UxVInternalActions.absolutePosition'");
        
		try {
			// Get the parameters
			NumberTerm positionX = (NumberTerm)args[0];
			NumberTerm positionY = (NumberTerm)args[1];
			NumberTerm positionZ = (NumberTerm)args[2];
			NumberTerm azimuth = (NumberTerm)args[3];
			NumberTerm elevation = (NumberTerm)args[4];
			NumberTerm range = (NumberTerm)args[5];
			
			// Calculate the absoulte position
			double x = positionX.solve() + (range.solve() * Math.sin(azimuth.solve()));
			double y = positionY.solve() + (range.solve() * Math.cos(azimuth.solve()));
			double z = positionZ.solve() + (range.solve() * Math.sin(elevation.solve()));
			
			// Create the result term
			NumberTerm resultX = new NumberTermImpl(x);
			NumberTerm resultY = new NumberTermImpl(y);
			NumberTerm resultZ = new NumberTermImpl(z);
			
			// Unify
			boolean xSuccess = un.unifies(resultX,args[6]);
			boolean ySuccess = un.unifies(resultY,args[7]);
			boolean zSuccess = un.unifies(resultZ,args[8]);
			
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

