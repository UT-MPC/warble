package edu.utexas.mpc.warble.models;


import edu.utexas.mpc.warble.db.LocalActionDB;
import edu.utexas.mpc.warble.device.Device;
import edu.utexas.mpc.warble.device.Light;
import edu.utexas.mpc.warble.service.Service;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public class LightModel extends DeviceModel {
    public String name;
    public double brightness;

    public Device abs(String requestId, LocalActionDB localActionDB){
        return service.light(this.id, requestId, localActionDB);
    }

    public Device proxy(Service proxyService, String requestId, String proxyId, LocalActionDB localActionDB){
        return proxyService.light(proxyId, requestId, localActionDB);
    }

    @Override
    public Class<? extends Device> type(){
        return Light.class;
    }

    @Override public String toString(){
        return super.toString() + " service: " + service + " name: " + name
                                + " brightness: " + brightness;
    }
}
