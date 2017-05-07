package io.github.yusukeiwaki.imadoko;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;

import io.github.yusukeiwaki.imadoko.etc.OkHttpHelper;

public class ImadokoApplication extends Application {
    public static class ENV {
        public static String API_HOSTNAME;
        public static String FIREBASE_DYNAMIC_LINK_DOMAIN;
        public static String URL_SHORTENER_API_KEY;

        public static void initializeWith(Context context) {
            Resources res = context.getResources();
            API_HOSTNAME = res.getString(R.string.api_hostname);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        ENV.initializeWith(this);
        OkHttpHelper.initializeHttpClient();
        Stetho.initializeWithDefaults(this);
    }
}
