package edu.utexas.mpc.warble.etch;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

import edu.utexas.mpc.warble.service.Service;
import edu.utexas.mpc.warble.service.ServiceManager;

/**
 * Created by nathanielwendt on 5/4/16.
 */
public class BLEScanEval implements Eval {
    int iterations;
    int i;
    boolean stop;
    Object lock = new Object();
    boolean done = false;
    long initTime;

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        Stabilizer stabFunc = new Stabilizer(){
            Object obj = new Object();

            @Override
            public void task(Object data) {
//                ServiceManager serviceManager = new ServiceManager(new Activity());
//                serviceManager.scan(new ServiceManager.FindServiceCallback() {
//                    @Override
//                    public void onService(Service service) {
//
//                    }
//
//                    @Override
//                    public void done() {
//                        obj.notify();
//                    }
//                });
//                try {
//                    obj.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        };

        String dbName = options.getString("dbName");
        iterations = Integer.valueOf(options.getString("iterations"));

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "First");

        i = 0;
        initTime = System.nanoTime();
        scan(ctx);
    }

    public void scan(final Context ctx){
        ServiceManager serviceManager = new ServiceManager(ctx);
        serviceManager.scan(new ServiceManager.FindServiceCallback() {
            @Override
            public void onService(Service service) {

            }

            @Override
            public void done() {
                System.out.println("scan complete");
                if(i == iterations - 1){
                    MultiProfiler.endMark("First");
                    MultiProfiler.stopProfiling();
                    System.out.println("all scans complete");
                    System.out.println((System.nanoTime() - initTime) / 1000000);
                } else {
                    scan(ctx);
                }
                i++;
            }
        });
    }
}
