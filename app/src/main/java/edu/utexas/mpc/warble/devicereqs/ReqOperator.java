package edu.utexas.mpc.warble.devicereqs;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class ReqOperator {
    public static ReqOperator newInstance(DeviceReq req) {
        return req.operator();
    }
}
