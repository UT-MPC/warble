package edu.utexas.mpc.warble.service;

import java.util.List;

import edu.utexas.mpc.warble.db.LocalActionDB;
import edu.utexas.mpc.warble.device.Light;
import edu.utexas.mpc.warble.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public interface Service {
    Light light(String deviceId, String requestId, LocalActionDB localActionDB);
    public void fetchDevices(FetchDevicesCallback callback);


    public interface FetchDevicesCallback {
        void onFetch(List<DeviceModel> fetchedDevices);
    }

    public String id();
}
