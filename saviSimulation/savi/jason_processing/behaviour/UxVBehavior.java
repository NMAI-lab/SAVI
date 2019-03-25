package savi.jason_processing.behaviour;

import java.util.*;

import processing.core.*;

import savi.StateSynchronization.*;
import savi.jason_processing.AgentModel;
import savi.jason_processing.Geometry;
import savi.jason_processing.UxV;
import savi.jason_processing.WorldObject;


public abstract class UxVBehavior extends AgentModel {
    //-----------------------------------------
    // DATA (or state variables)
    //-----------------------------------------
    //String ID; -- Note: moved to superclass
    //String type; -- same
    //SyncAgentState agentState; --same
    protected PVector initialPosition;
    protected double speedVal;
    protected double compasAngle;
    protected ArrayList<CameraPerception> visibleItems;
    protected double time;
    protected Map<String, AgentAction> actionMap;
    protected final double maxSpeed;
    protected double sensorsErrorProb;
    protected double sensorsErrorStdDev;
    protected static Random rand = new Random();

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
    public UxVBehavior(String id, String type, double maxSpeed, PVector initialPosition, double reasoningCyclePeriod, double sensorsErrorProb, double sensorsErrorStdDev) {
        // Initialize data values
        super(reasoningCyclePeriod);
        this.maxSpeed = maxSpeed;
        this.ID = id;
        this.type = type;
        this.initialPosition = initialPosition;
        this.time = 0;
        this.compasAngle = 0;
        this.speedVal = 0;
        this.agentState = new SyncAgentState();
        this.visibleItems = new ArrayList<CameraPerception>();
        this.actionMap = new HashMap<String, AgentAction>();
        this.sensorsErrorProb = sensorsErrorProb;
        this.sensorsErrorStdDev = sensorsErrorStdDev;
        this.createActionMap();
        updatePercepts(initialPosition);
    }

    public ArrayList<CameraPerception> getVisibleItems() {
        return visibleItems;
    }

    public double getCompassAngle() {
        return this.compasAngle;
    }

    /**
     * update
     * process actions from the queue, update the UAS state variable and set the new perceptions
     */
    public void update(UxV uxv, double simTime, int perceptionDistance, List<WorldObject> worldObjects) {
        List<String> actionsToExecute = agentState.getAllActions();

        //Process actions to update speedVector & compassAngle
        processAgentActions(actionsToExecute);

        //Update simTime
        double timeElapsed = simTime - this.time; //elapsed time since last update
        this.time = simTime;

        PVector newPos = this.updatePosition(uxv.getPosition(), timeElapsed);
        uxv.setPosition(newPos);

        //Calculate visible items
        this.visibleItems.clear();

        //Calculate objects detected with camera
        visibleItems.addAll(objectDetection(uxv.getPosition(), worldObjects, perceptionDistance));

        //Update percepts
        updatePercepts(uxv.getPosition());
    }

    protected PVector updatePosition(PVector currentPosition, double timeElapsed) {
        //Calculate new x,y position
        double cosv = Math.cos(this.compasAngle);
        double sinv = Math.sin(this.compasAngle);
        PVector deltaPos = new PVector((float) (cosv * this.speedVal * timeElapsed), (float) (sinv * this.speedVal * timeElapsed), 0);
        return currentPosition.add(deltaPos);
    }

    /**
     * Detect world objects & threats with the camera
     */
    protected ArrayList<CameraPerception> objectDetection(PVector mypos, List<WorldObject> obj, int perceptionDistance) {
        ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
        ArrayList<CameraPerception> detectedItems = new ArrayList<CameraPerception>();
        double distance, oposite, tan, angle;

        for (WorldObject wo : obj) {
            //shouldn't detect itself. if not (UxV and himself)
            if (!((wo instanceof UxV) && this.ID.equals(((UxV) wo).getBehavior().getID()))) {

                List<Double> polar = Geometry.relativePositionPolar(wo.getPosition(), mypos, this.compasAngle);

                //calculate distance
                double azimuth = polar.get(Geometry.AZIMUTH);
                double elevation = polar.get(Geometry.ELEVATION);
                double dist = polar.get(Geometry.DISTANCE);
                if ((azimuth < Math.PI / 2. || azimuth > 3 * Math.PI / 2.) && (dist < perceptionDistance)) {
                    //it's visible
                    detectedItems.add(new CameraPerception(wo.getType(), this.time, azimuth, elevation, dist, wo.getPixels() / 2));
                    visibleItems.add(new CameraPerception(wo.getType(), this.time, azimuth, elevation, dist, wo.getPixels() / 2));
                }
            }
        }

        //remove objects covered by others on the visualization
        for (CameraPerception di : detectedItems) {
            for (int i = 0; i < visibleItems.size(); i++) {
                //to calculate visual angle covered by the object
                distance = di.getParameters().get(2);
                //angle deviation from centroid = radius
                oposite = di.getParameters().get(3);
                //math to calculate angle cover
                tan = oposite / distance;
                angle = Math.abs(Math.atan(tan));

                //if object is covered by di remove
                //if is covered by azimuth angle
                if (di.getParameters().get(0) + angle > visibleItems.get(i).getParameters().get(0) && di.getParameters().get(0) - angle < visibleItems.get(i).getParameters().get(0)) {
                    //if it is covered by elevation angle
                    if (di.getParameters().get(1) + angle > visibleItems.get(i).getParameters().get(1) && di.getParameters().get(1) - angle < visibleItems.get(i).getParameters().get(1)) {
                        //if it's at a mayor distance
                        if (di.getParameters().get(2) < visibleItems.get(i).getParameters().get(2)) {
                            visibleItems.remove(visibleItems.get(i));
                        }
                    }
                }
            }
        }

        return visibleItems;
    }

    /**
     * Adds to the current compass angle and normalizes the value between 0 and 2 * PI
     *
     * @param val
     */
    protected void addNormalizedCompassAngle(double val) {
        this.compasAngle += val;
        double twoPi = 2 * Math.PI;

        if (this.compasAngle < 0)
            this.compasAngle += twoPi;
        if (this.compasAngle > twoPi)
            this.compasAngle -= twoPi;
    }

    /**
     * Creates the default actions for a UxV agent
     */
    protected void createActionMap() {
        actionMap.put("turn(left)", (() -> this.addNormalizedCompassAngle(-Math.PI / 16.0)));
        actionMap.put("turn(right)", (() -> this.addNormalizedCompassAngle(Math.PI / 16.0)));
        actionMap.put("thrust(on)", (() -> this.speedVal = maxSpeed));
        actionMap.put("thrust(off)", (() -> this.speedVal = 0));
    }

    /**
     * Process the actions in the queue, and run the corresponding action handler
     */
    protected void processAgentActions(List<String> actionsToExecute) {
        for (String action : actionsToExecute) {
            System.out.println("[ process actions] UAS id=" + this.ID + " doing: " + action);
            AgentAction agentAction = actionMap.get(action);
            if (agentAction == null) {
                System.out.println("No Action defined for '" + action + "'");
                continue;
            }
            agentAction.performAction();
        }
    }

    /**
     * Update perception Snapshot in agent state
     */
    protected void updatePercepts(PVector mypos) {
        PerceptionSnapshot P = new PerceptionSnapshot();
        PVector posPercept = mypos.copy();

        //if position sensor is failing, we overwrite the position values with bad values
        if (isSensorFailing(sensorsErrorProb)) {
            posPercept.x = (float) calculateFailureValue((double) mypos.x, this.sensorsErrorStdDev);
            posPercept.y = (float) calculateFailureValue((double) mypos.y, this.sensorsErrorStdDev);
            posPercept.z = (float) calculateFailureValue((double) mypos.z, this.sensorsErrorStdDev);

        }

        //add position
        P.addPerception(new PositionPerception(this.time, (double) posPercept.x, (double) posPercept.y, (double) posPercept.z));

        //Add velocity
        P.addPerception(new VelocityPerception(this.time, Math.atan(posPercept.x / posPercept.y), 0, this.speedVal));

        //Add time
        P.addPerception(new TimePerception(this.time));

        //Add Visible items
        for (CameraPerception cpi : this.visibleItems) {
            for (int i = 0; i < cpi.getParameters().size(); i++) {
                if (isSensorFailing(sensorsErrorProb)) {
                    cpi.getParameters().set(i, calculateFailureValue(cpi.getParameters().get(i), this.sensorsErrorStdDev));
                }
            }
            P.addPerception(cpi);
        }

        agentState.setPerceptions(P);
    }


    // takes probability parameter between 0 and 1
    protected boolean isSensorFailing(double probability) {
        if (rand.nextDouble() < probability) {
            return true;
        } else {
            return false;
        }
    }


    // generate random value for a normal distribution (mean, stdDev)
    protected double calculateFailureValue(double mean, double stdDev) {
        return ((rand.nextGaussian() * stdDev) + mean);
    }

    public static void setSeed(int seed) {
        if (seed != -1) {
            rand = new Random(seed);
        }
    }

    /**
     * Get UAS id
     *
     * @return
     */
    public String getID() {
        return ID;
    }

    /**
     * Get UAS type
     *
     * @return
     */
    public String getType() {
        return type;
    }
}