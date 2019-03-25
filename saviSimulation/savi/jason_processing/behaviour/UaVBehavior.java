package savi.jason_processing.behaviour;

import processing.core.*;


public class UaVBehavior extends UxVBehavior {
    private static final float VERTICAL_SPEED = 0.1f; // 0.1 something (whatever real-life distance this corresponds to)
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
    public UaVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {
        // Initialize data values
        super(id, type, initialPosition, reasoningCyclePeriod);
    }

    /**
     * Process the action in the queue to update the speedVector and compassAngle
     */
    @Override
    protected void createActionMap() {
        // Create default left / right / thrust actions
        super.createActionMap();

        // Add additional UAV actions
        actionMap.put("thrust(up)", () -> this.speedVector.z = VERTICAL_SPEED);
        actionMap.put("thrust(down)", () -> this.speedVector.z = -VERTICAL_SPEED);
        actionMap.put("hover", () -> this.speedVector.z = 0);
    }
}