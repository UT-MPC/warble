package edu.utexas.mpc.warble.service;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import edu.utexas.mpc.warble.comm.APIRequest;
import edu.utexas.mpc.warble.db.LocalActionDB;
import edu.utexas.mpc.warble.device.DeviceUnavailableException;
import edu.utexas.mpc.warble.device.Light;
import edu.utexas.mpc.warble.devicereqs.TypeReq;
import edu.utexas.mpc.warble.models.DeviceModel;
import edu.utexas.mpc.warble.models.LightModel;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public class Wink implements Service {
    private final String clientId;
    private final String clientSecret;
    private final String userName;
    private final String passPhrase;
    private String accessToken;
    private String refreshToken;
    private boolean async = true;

    private class WinkRequest extends APIRequest {
        public WinkRequest(boolean async){
            super(async);
        }

        @Override
        public void get(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.get(url, responseHandler);
        }

        @Override
        public void post(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.post(url, responseHandler);
        }

        @Override
        public void put(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.put(url, responseHandler);
        }

        @Override public String getBaseUrl(){
            return "https://api.wink.com";
        }
    }

    public interface SignInCallback {
        void signedIn();
    }

    public String id(){
        return this.clientId;
    }

    public void signIn(final SignInCallback callback){
        WinkRequest req = new WinkRequest(async);
        req.param("client_id", clientId);
        req.param("client_secret", clientSecret);
        req.param("username", userName);
        req.param("password", passPhrase);
        req.param("grant_type", "password");

        req.post("/oauth2/token", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println(statusCode);
                    JSONObject data = (JSONObject) response.get("data");
                    accessToken = (String) data.get("access_token");
                    refreshToken = (String) data.get("refresh_token");
                    System.out.println(accessToken);
                    callback.signedIn();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean signedIn(){
        return accessToken != null;
    }

    public Wink(String clientId, String clientSecret, String userName, String passPhrase){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userName = userName;
        this.passPhrase = passPhrase;
    }

    public void fetchDevicesHelper(final FetchDevicesCallback callback){
        MockWinkRequest req = new MockWinkRequest(async);
        //req.get("/users/me/wink_devices", new JsonHttpResponseHandler() {
        req.get("?n=2", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<DeviceModel> devices = new ArrayList<DeviceModel>();
                try {
                    JSONArray data = (JSONArray) response.get("data");
                    System.out.println(data.length());
                    for(int i = 0; i < data.length(); i++){
                        JSONObject deviceData = data.getJSONObject(i);
                        if(deviceData.has("light_bulb_id")){
                            LightModel light = new LightModel();
                            light.id = (String) deviceData.get("light_bulb_id");
                            light.hubId = (String) deviceData.get("hub_id");
                            light.manufacturer = (String) deviceData.get("device_manufacturer");
                            light.model = (String) deviceData.get("model_name");
                            light.radioType = (String) deviceData.get("radio_type");
                            light.hubManufacturer = "Wink";
                            light.name = (String) deviceData.get("name");
                            light.createdAt = Long.valueOf((Integer) deviceData.get("created_at"));
                            light.type = TypeReq.Type.LIGHT;
                            //location is stored in array of size two (lat,lng) with key of "lat_lng";

                            //JSONObject desiredState = (JSONObject) deviceData.get("desired_state");
                            //light.powered = (Boolean) desiredState.get("powered");
                            //light.brightness = (Double) desiredState.get("brightness");
                            light.service = Wink.this;
                            //System.out.println(light);
                            devices.add(light);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onFetch(devices);
            }
        });
    }

    public void fetchDevices(final FetchDevicesCallback callback) {
        if (!signedIn()) {
            signIn(new Wink.SignInCallback() {
                @Override
                public void signedIn() {
                    fetchDevicesHelper(callback);
                }
            });
        } else {
            fetchDevicesHelper(callback);
        }
    }

    public Light light(final String deviceId, final String requestId, LocalActionDB localActionDB){
        final JsonHttpResponseHandler lightHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
            }
        };


        return new Light(deviceId, requestId, localActionDB){
            @Override
            public void brightness(int level) throws DeviceUnavailableException {
                //TODO: support resetting previous brightness
                //query for previous brightness
                //super.brightness(prevLevel, nextLevel);
                super.brightness(level);
                WinkRequest req = new WinkRequest(true);
                req.param("light_bulb_id", deviceId);
                Map<String,Object> desiredState = new HashMap<String,Object>();
                desiredState.put("powered", true);
                desiredState.put("brightness", level);
                req.param("desired_state", desiredState);
                req.param("powered", false);
                req.put("/light_bulbs/" + deviceId, lightHandler);
            }

            @Override
            public void off() throws DeviceUnavailableException {
                super.off();
                WinkRequest req = new WinkRequest(true);
                req.param("light_bulb_id", deviceId);
                Map<String,Object> desiredState = new HashMap<String,Object>();
                desiredState.put("powered", false);
                req.param("desired_state", desiredState);
                req.param("powered", false);
                req.put("/light_bulbs/" + deviceId, lightHandler);
            }

            @Override
            public void on() throws DeviceUnavailableException {
                super.on();
                WinkRequest req = new WinkRequest(true);
                req.param("light_bulb_id", deviceId);
                Map<String,Object> desiredState = new HashMap<String,Object>();
                desiredState.put("powered", true);
                req.param("desired_state", desiredState);
                req.param("powered", false);
                req.put("/light_bulbs/" + deviceId, lightHandler);
            }
        };
    }

    public void sendReq(final FetchDevicesCallback callback){
        final JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode);
                callback.onFetch(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
            }
        };


        MockWinkRequest req = new MockWinkRequest(true);
        //req.param("light_bulb_id", "1451815");
//        Map<String,Object> desiredState = new HashMap<String,Object>();
//        desiredState.put("powered", true);
//        req.param("desired_state", desiredState);
//        req.param("powered", false);
        req.get("?n=3", handler);
    }

    private class MockWinkRequest extends APIRequest {
        public MockWinkRequest(boolean async){
            super(async);
        }

        @Override
        public void get(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.get(url, responseHandler);
        }

        @Override
        public void post(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.post(url, responseHandler);
        }

        @Override
        public void put(String url, AsyncHttpResponseHandler responseHandler) {
            this.header("Authorization", "Bearer " + accessToken);
            super.put(url, responseHandler);
        }

        @Override public String getBaseUrl(){
            return "http://pacobackend.appspot.com/";
        }
    }
}
