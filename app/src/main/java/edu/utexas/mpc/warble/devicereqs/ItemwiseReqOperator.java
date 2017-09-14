package edu.utexas.mpc.warble.devicereqs;

import edu.utexas.mpc.warble.db.InteractionHistory;
import edu.utexas.mpc.warble.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class ItemwiseReqOperator extends ReqOperator {
    public abstract boolean match(DeviceModel device, InteractionHistory history);
}
