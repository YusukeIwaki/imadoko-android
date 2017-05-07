package io.github.yusukeiwaki.imadoko.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.github.yusukeiwaki.imadoko.etc.AnotherUserCache;
import io.github.yusukeiwaki.imadoko.etc.CurrentUserCache;
import io.github.yusukeiwaki.imadoko.requester.TrackingJsonHandler;
import io.github.yusukeiwaki.imadoko.sender.PositioningRequirementCheckAndStartPositioningActivity;

public class FcmMessagingService extends FirebaseMessagingService {
    private static final String TAG = FcmMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        String pushType = data.get("push_type");
        if ("update_location_log".equals(pushType)) {
            if (isPermittedUser(data.get("username"))) {
                // 問答無用で測位する
                startActivity(PositioningRequirementCheckAndStartPositioningActivity.newIntent(this));
            }
        } else if ("new_location_log".equals(pushType)) {
            try {
                new TrackingJsonHandler(this).handleNewLocationLog(new JSONObject(data.get("tracking")));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private boolean isPermittedUser(String username) {
        return CurrentUserCache.get(this).getString(CurrentUserCache.KEY_PERMIT_USERNAME, "").equals(username);
    }

    private void handleNewLocationLog(String trackingJsonStr) throws JSONException {
        JSONObject trackingJson = new JSONObject(trackingJsonStr);
        JSONObject locationLogJson = trackingJson.getJSONObject("location_log");

        AnotherUserCache.get(this).edit()
                .putFloat(AnotherUserCache.KEY_LATITUDE, (float) locationLogJson.getDouble("lat"))
                .putFloat(AnotherUserCache.KEY_LONGITUDE, (float) locationLogJson.getDouble("lon"))
                .putFloat(AnotherUserCache.KEY_ACCURACY, (float) locationLogJson.getDouble("accuracy"))
                .putLong(AnotherUserCache.KEY_LAST_UPDATED_AT, locationLogJson.getLong("created_at"))
                .apply();
    }
}
