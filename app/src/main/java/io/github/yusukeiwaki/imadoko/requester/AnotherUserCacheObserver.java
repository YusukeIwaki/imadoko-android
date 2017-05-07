package io.github.yusukeiwaki.imadoko.requester;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.yusukeiwaki.imadoko.etc.AnotherUserCache;
import io.github.yusukeiwaki.imadoko.etc.LocationLogCache;
import io.github.yusukeiwaki.imadoko.etc.ReactiveSharedPref;
import io.github.yusukeiwaki.imadoko.sender.LocationCacheItem;

public class AnotherUserCacheObserver extends ReactiveSharedPref<LocationCacheItem> {
    public AnotherUserCacheObserver(Context context) {
        super(LocationLogCache.get(context), new ObservationPolicy<LocationCacheItem>() {
            @Override
            public boolean isTargetKey(String key) {
                return AnotherUserCache.KEY_LATITUDE.equals(key) ||
                        AnotherUserCache.KEY_LONGITUDE.equals(key) ||
                        AnotherUserCache.KEY_ACCURACY.equals(key) ||
                        AnotherUserCache.KEY_LAST_UPDATED_AT.equals(key);
            }

            @Override
            public LocationCacheItem getValueFromSharedPreference(SharedPreferences prefs) {
                return new LocationCacheItem.Builder()
                        .lat(prefs.getFloat(AnotherUserCache.KEY_LATITUDE, 0))
                        .lon(prefs.getFloat(AnotherUserCache.KEY_LONGITUDE, 0))
                        .accuracy(prefs.getFloat(AnotherUserCache.KEY_ACCURACY, 0))
                        .timestamp(prefs.getLong(AnotherUserCache.KEY_LAST_UPDATED_AT, 0))
                        .build();
            }
        });
    }
}
