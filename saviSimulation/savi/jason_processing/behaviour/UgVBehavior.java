package savi.jason_processing.behaviour;

import java.util.*;

import processing.core.*;


public class UgVBehavior extends UxVBehavior {
    private static final float SPEED = 0.1f; // 0.1 pixels (whatever real-life distance this corresponds to)

    //-----------------------------------------
    // DATA (or state variables)
    //-----------------------------------------
    //String ID; -- Note: moved to superclass
    //String type; -- same
    //SyncAgentState agentState; --same
    //***********************************************************//
    //I THINK IS BETTER TO HAVE THE ROBOTS ITS DATA AND THE SYNCAGENTSTATE ITS OWN.
    //IF WE WANT TO IMPLEMENTE MALFUNCTION OF SENSORS, THE INFO RECEIVED IN
    //SYNCAGENTSTATE IS NOT THE REAL ONE
    //***********************************************************//

    //-----------------------------------------
    // METHODS (functions that act on the data)
    //-----------------------------------------

    /**
     * Constructor
     *
     * @param id
     * @param type
     * @param initialPosition
     */
    public UgVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {
        // Initialize data values
        super(id, type, initialPosition, reasoningCyclePeriod);
    }
}
