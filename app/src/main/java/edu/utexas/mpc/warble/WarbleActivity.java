package edu.utexas.mpc.warble;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.mpc.warble.device.DeviceUnavailableException;
import edu.utexas.mpc.warble.device.Light;
import edu.utexas.mpc.warble.devicereqs.DeviceReq;
import edu.utexas.mpc.warble.devicereqs.SpatialReq;
import edu.utexas.mpc.warble.devicereqs.TypeReq;
import edu.utexas.mpc.warble.misc.Location;
import edu.utexas.mpc.warble.service.DiscoverCallback;

public class WarbleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, new Location(0,0)));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);
        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                Light light = snapshot.retrieve(Light.class, reqs);
                try {
                    light.on();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
