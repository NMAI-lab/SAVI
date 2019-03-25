package savi.jason_processing.behaviour;

import processing.core.*;


public class UaVBehavior extends UxVBehavior {
    private static final float SPEED = 0.1f; // 0.1 pixels (whatever real-life distance this corresponds to)
    private static final float VERTICAL_SPEED = 0.1f; // 0.1 something (whatever real-life distance this corresponds to)
    //***********************************************************//
    //I THINK IS BETTER TO HAVE THE ROBOTS ITS DATA AND THE SYNCAGENTSTATE ITS OWN.
    //IF WE WANT TO IMPLEMENTE MALFUNCTION OF SENSORS, THE INFO RECEIVED IN
    //SYNCAGENTSTATE IS NOT THE REAL ONE
    //***********************************************************//

    private double verticalSpeedVal;

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
    public UaVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod, double sensorsErrorProb, double sensorsErrorStdDev) {
        // Initialize data values
        super(id, type, SPEED, initialPosition, reasoningCyclePeriod, sensorsErrorProb, sensorsErrorStdDev);
        verticalSpeedVal = 0;
    }

    @Override
    protected PVector updatePosition(PVector currentPosition, double timeElapsed)
    {
        PVector newPos = super.updatePosition(currentPosition, timeElapsed);

        //Calculate new altitude
        newPos.z += verticalSpeedVal * timeElapsed;

        if (newPos.z < 0)
            newPos.z = 0;

        return newPos;
    }

    /**
     * Process the action in the queue to update the speedVector and compassAngle
     */
    @Override
    protected void createActionMap() {
        // Create default left / right / thrust actions
        super.createActionMap();

        // Add additional UAV actions
        actionMap.put("thrust(up)", () -> this.verticalSpeedVal = VERTICAL_SPEED);
        actionMap.put("thrust(down)", () -> this.verticalSpeedVal = -VERTICAL_SPEED);
        actionMap.put("hover", () -> this.verticalSpeedVal = 0);
    }
}