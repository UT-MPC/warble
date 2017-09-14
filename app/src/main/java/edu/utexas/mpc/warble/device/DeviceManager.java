package edu.utexas.mpc.warble.device;

import java.util.List;

import edu.utexas.mpc.warble.models.DeviceModel;
import edu.utexas.mpc.warble.util.InitializedCallback;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public interface DeviceManager {

    public void scan();
    public void scan(InitializedCallback callback);
    public boolean initialized();

    public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz);
    public List<DeviceModel> fetchDevices();

}
