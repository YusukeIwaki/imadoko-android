package io.github.yusukeiwaki.imadoko.api;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import bolts.Task;
import io.github.yusukeiwaki.imadoko.BuildConfig;
import io.github.yusukeiwaki.imadoko.ImadokoApplication;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ImadokoAPI extends APIBase {
    private static final String TAG = ImadokoAPI.class.getSimpleName();

    private String getHostname() {
        return ImadokoApplication.ENV.API_HOSTNAME;
    }

    public Task<JSONObject> createOrUpdateTracking(String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("name", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    private HttpUrl buildTrackingUrl(String trackingId) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .build();
    }

    public String getTrackingURLForShare(String trackingId) {
        String targetURL = buildTrackingUrl(trackingId).toString() + "/location.png";

        String dynamicLinkUrl = new Uri.Builder()
                .scheme("https")
                .authority(ImadokoApplication.ENV.FIREBASE_DYNAMIC_LINK_DOMAIN)
                .path("/")
                .appendQueryParameter("link", targetURL)
                .appendQueryParameter("apn", BuildConfig.APPLICATION_ID)
                .toString();

        return dynamicLinkUrl;
    }

    public Task<JSONObject> getTracking(String trackingId) {
        Request req = new Request.Builder()
                .url(buildTrackingUrl(trackingId))
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> updateLocationLog(String trackingId, double lat, double lon, double accuracy) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("location_logs")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("location_log", new JSONObject()
                    .put("lat", lat)
                    .put("lon", lon)
                    .put("accuracy", accuracy > 0 ? accuracy : JSONObject.NULL));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> observeTracking(String trackingId, String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("observe")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("username", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> unobserveTracking(String trackingId, String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("observe")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("username", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        return baseJsonRequest(req);
    }
}
