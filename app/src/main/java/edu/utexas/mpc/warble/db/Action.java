package edu.utexas.mpc.warble.db;

import java.util.List;

import edu.utexas.mpc.warble.devicereqs.DeviceReq;
import edu.utexas.mpc.warble.misc.Location;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class Action {
    public String id;
    public Location refLocation;
    public Location devLocation;
    public List<DeviceReq> reqs;
    public String deviceId;
    public ActionType type; //e.q. Light-On, Key-Unlock, Video-Pan
    public boolean successful;

    public static Action newDefault(String deviceId, Location ref, Location dev, boolean succ){
        Action action = new Action();
        action.deviceId = deviceId;
        action.refLocation = ref;
        action.devLocation = dev;
        action.successful = succ;
        return action;
    }
}
