package edu.utexas.mpc.warble.devicereqs;

import java.util.List;

import edu.utexas.mpc.warble.db.InteractionHistory;
import edu.utexas.mpc.warble.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class AggregateReqOperator extends ReqOperator {
    public abstract List<DeviceModel> resolve(List<DeviceModel> candidates, InteractionHistory history);
}
