package io.github.yusukeiwaki.imadoko.etc;

import android.content.Context;
import android.content.SharedPreferences;

public class AnotherUserCache {
    private static final String PREF_NAME = "another_user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGITUDE = "lon";
    public static final String KEY_ACCURACY = "acc";
    public static final String KEY_LAST_UPDATED_AT = "updated_at";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
