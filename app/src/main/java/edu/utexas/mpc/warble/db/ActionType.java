package edu.utexas.mpc.warble.db;

import edu.utexas.mpc.warble.device.DeviceUnavailableException;

/**
 * Created by nathanielwendt on 4/30/16.
 */
public interface ActionType extends DBStorable {
    String val();
    void undo() throws DeviceUnavailableException;
}
