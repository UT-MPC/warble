package edu.utexas.mpc.warble;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.mpc.warble.R;
import edu.utexas.mpc.warble.device.DeviceUnavailableException;
import edu.utexas.mpc.warble.device.Light;
import edu.utexas.mpc.warble.devicereqs.DeviceReq;
import edu.utexas.mpc.warble.devicereqs.SpatialReq;
import edu.utexas.mpc.warble.devicereqs.TypeReq;
import edu.utexas.mpc.warble.misc.Location;
import edu.utexas.mpc.warble.service.DiscoverCallback;
import edu.utexas.mpc.warble.service.Service;
import edu.utexas.mpc.warble.service.ServiceManager;

public class BLEActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            //use checkSelfPermission()
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 112);
        }


        final List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        Location currLoc = new Location(0,0);
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, currLoc));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));

        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);

        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                try {
                    Light light = snapshot.retrieve(Light.class, reqs);
                    light.off();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




//        final List<DeviceReq> reqs = new ArrayList<>();
//        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE,
//                new Location(0,0)));
//        //reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
//
//        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);
//        snapshot.discover();
////        snapshot.discover(new InitializedCallback() {
////            @Override
////            public void onInit() {
////                Light light = snapshot.retrieve(Light.class, reqs);
////                try {
////                    if (light != null) {
////                        System.out.println("turning light on!!");
////                        light.on();
////                    }
////                } catch (DeviceUnavailableException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//
//
//        snapshot.whenDiscovered(new DiscoverCallback() {
//            @Override
//            public void onDiscover() {
//                Light light = snapshot.retrieve(Light.class, reqs);
//                try {
//                    if (light != null) {
//                        System.out.println("turning light on!!");
//                        light.on();
//                    }
//                } catch (DeviceUnavailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        System.out.println("here here");

    }

//    public void setCommand(String plan){
//        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(BLE_UUID) );
//        data = new AdvertiseData.Builder()
//                .addServiceUuid(pUuid)
//                .addServiceData(pUuid, plan.getBytes(Charset.forName("UTF-8")))
//                .build();
//        advertiser.startAdvertising(settings, data, advertisingCallback);
//    }

    public void onClickScan(View v){
        ServiceManager manager = new ServiceManager(this);
        manager.scan(new ServiceManager.FindServiceCallback() {
            @Override
            public void onService(Service service) {
                System.out.println("Service received");
            }

            @Override
            public void done() {
                System.out.println("Done scanning");
            }
        });
    }

}
