package io.github.yusukeiwaki.imadoko.requester;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.yusukeiwaki.imadoko.etc.AnotherUserCache;

public class TrackingJsonHandler {
    private final Context context;

    public TrackingJsonHandler(Context context) {
        this.context = context;
    }

    public void handleNewLocationLog(JSONObject trackingJson) throws JSONException {
        JSONObject locationLogJson = trackingJson.getJSONObject("location_log");

        AnotherUserCache.get(context).edit()
                .putFloat(AnotherUserCache.KEY_LATITUDE, (float) locationLogJson.getDouble("lat"))
                .putFloat(AnotherUserCache.KEY_LONGITUDE, (float) locationLogJson.getDouble("lon"))
                .putFloat(AnotherUserCache.KEY_ACCURACY, (float) locationLogJson.getDouble("accuracy"))
                .putLong(AnotherUserCache.KEY_LAST_UPDATED_AT, locationLogJson.getLong("created_at"))
                .apply();
    }
}
