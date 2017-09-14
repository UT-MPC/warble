package edu.utexas.mpc.warble.device;

/**
 * Created by nathanielwendt on 4/8/16.
 */
public interface DevicePlan {
    void onBind(Device device) throws DeviceUnavailableException;
    void onUnbind(Device device) throws DeviceUnavailableException;
}
