package edu.utexas.mpc.warble.device;

import java.util.Observable;

import edu.utexas.mpc.warble.misc.Location;

/**
 * Created by nathanielwendt on 3/10/16.
 */
public class Observables {
    public static class SpatialObservable extends Observable {
        private Location loc;

        public void update(Location loc){
            this.loc = loc;
            this.notifyObservers(loc);
        }
    }
}
