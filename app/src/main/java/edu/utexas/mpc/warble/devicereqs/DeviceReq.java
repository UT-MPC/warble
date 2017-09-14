package edu.utexas.mpc.warble.devicereqs;

import edu.utexas.mpc.warble.db.DBStorable;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public abstract class DeviceReq implements DBStorable {
    abstract ReqOperator operator();
}
