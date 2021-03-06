package edu.utexas.mpc.warble.device;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.mpc.warble.models.DeviceModel;
import edu.utexas.mpc.warble.service.Service;
import edu.utexas.mpc.warble.service.ServiceManager;
import edu.utexas.mpc.warble.util.InitializedCallback;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public class LocalDeviceManager implements DeviceManager {
    List<DeviceModel> devices = new ArrayList<>();
    //maintains a list of UUIDs existing within the device manager to not add duplicates
    List<String> deviceUUIDs = new ArrayList<>();
    List<Service> services = new ArrayList<>();
    Context ctx;
    ServiceManager serviceManager;
    volatile boolean initialized = false;
    private final Object lock = new Object();

    public LocalDeviceManager(Context ctx){
        this.ctx = ctx;
        serviceManager = new ServiceManager(ctx);
    }

    public boolean initialized(){
        synchronized(lock){
            return initialized;
        }
    }

    public void setInitialized(boolean initialized){
        synchronized(lock){
            this.initialized = initialized;
        }
    }

//    private class ScanTask extends AsyncTask<InitializedCallback, Void, Void> {
//
//        @Override
//        protected Void doInBackground(final InitializedCallback... params) {
//            serviceManager.scan(new ServiceManager.FindServiceCallback(){
//                @Override public void onService(Service service){
//                    services.add(service);
//                    service.fetchDevices(new Service.FetchDevicesCallback(){
//                        @Override public void onFetch(List<DeviceModel> fetchedDevices){
//                            retrieve.addAll(fetchedDevices);
//                        }
//                    });
//                }
//
//                @Override public void done(){
//                    setInitialized(true);
//                    //should be single param, but need loop to access varargs
//                    for(InitializedCallback cb : params){ cb.onInit(); }
//                }
//            });
//            return null;
//        }
//    }

    @Override
    public void scan() {
        throw new RuntimeException("not implemented yet");
    }

    private void addDevices(List<DeviceModel> newDevices){
        for(DeviceModel newDevice : newDevices){
            //BLE Services will have a uuid that is a repeated deviceId (this should be OK)
            String uuid = newDevice.service.id() + newDevice.id;
            if(!deviceUUIDs.contains(uuid)){
                devices.add(newDevice);
                deviceUUIDs.add(uuid);
            }
        }
    }

    @Override
    public void scan(final InitializedCallback callback){
        //ToDo: perform cloud update scans as well

        serviceManager.scan(new ServiceManager.FindServiceCallback(){
            @Override public void onService(Service service){
                services.add(service);
                service.fetchDevices(new Service.FetchDevicesCallback(){
                    @Override public void onFetch(List<DeviceModel> fetchedDevices){
                        addDevices(fetchedDevices);
                    }
                });
            }

            @Override public void done(){
                setInitialized(true);
                callback.onInit();
            }
        });

        //Update existing services
        for(Service service: services){
            service.fetchDevices(new Service.FetchDevicesCallback() {
                @Override
                public void onFetch(List<DeviceModel> fetchedDevices) {
                    addDevices(fetchedDevices);
                }
            });
        }
    }

    @Override
    public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz) {
        List<DeviceModel> retList = new ArrayList<>();

        for(DeviceModel device : devices){
            if(device.type() == clazz){
                retList.add(device);
            }
        }
        return retList;
    }

    @Override
    public List<DeviceModel> fetchDevices() {
        return devices;
    }
}
