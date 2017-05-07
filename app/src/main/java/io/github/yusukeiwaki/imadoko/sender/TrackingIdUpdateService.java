package io.github.yusukeiwaki.imadoko.sender;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import bolts.Task;
import io.github.yusukeiwaki.imadoko.api.GoogleAPI;
import io.github.yusukeiwaki.imadoko.api.ImadokoAPI;
import io.github.yusukeiwaki.imadoko.etc.CurrentUserCache;
import io.github.yusukeiwaki.imadoko.etc.LocationLogCache;

public class TrackingIdUpdateService extends IntentService {
    private static final String TAG = TrackingIdUpdateService.class.getSimpleName();
    private static final int API_TIMEOUT_MS = 4500;
    private final ImadokoAPI imadokoAPI;
    private final GoogleAPI googleAPI;

    public static void start(Context context) {
        Intent intent = new Intent(context, TrackingIdUpdateService.class);
        context.startService(intent);
    }

    public TrackingIdUpdateService() {
        super(TAG);
        imadokoAPI = new ImadokoAPI();
        googleAPI = new GoogleAPI();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferences prefs = CurrentUserCache.get(this);
        String username = prefs.getString(CurrentUserCache.KEY_USERNAME, null);
        String gcmToken = prefs.getString(CurrentUserCache.KEY_FCM_TOKEN, null);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(gcmToken)) {
            getTrackingIdFromAPI(username, gcmToken);
        }
    }

    private void getTrackingIdFromAPI(String username, String gcmToken) {
        try {
            imadokoAPI.createOrUpdateTracking(username, gcmToken).onSuccessTask(task -> {
                JSONObject response = task.getResult();
                String trackingId = response.getString("id");

                String origTrackingId = LocationLogCache.get(this).getString(LocationLogCache.KEY_TRACKING_ID, null);
                if (TextUtils.isEmpty(origTrackingId) || !origTrackingId.equals(trackingId)) {
                    LocationLogCache.get(this).edit()
                            .putString(LocationLogCache.KEY_TRACKING_ID, trackingId)
                            .apply();

                    return getShortUrl(trackingId);
                }

                return Task.forResult(null);
            }).waitForCompletion(API_TIMEOUT_MS, TimeUnit.MILLISECONDS); //IntentServiceなので、ブロックしておかないとstopSelfされてしまう
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private Task<Void> getShortUrl(String trackingId) {
        String trackingUrl = imadokoAPI.getTrackingURLForShare(trackingId);
        return googleAPI.shortenUrl(trackingUrl).onSuccess(task -> {
            String shortUrl = task.getResult();

            LocationLogCache.get(this).edit()
                    .putString(LocationLogCache.KEY_SHORT_URL, shortUrl)
                    .apply();

            return null;
        });
    }
}
