package io.github.yusukeiwaki.imadoko.etc;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ReactiveSharedPref<T> {
    private final SharedPreferences prefs;
    private Handler handler;
    private static final int DEBOUNCE_MS = 300;

    public interface ObservationPolicy<T> {
        boolean isTargetKey(String key);
        T getValueFromSharedPreference(SharedPreferences prefs);
    }

    public interface OnUpdateListener<T> {
        void onPreferenceValueUpdated(T value);
    }

    private final ObservationPolicy<T> observationPolicy;
    private OnUpdateListener<T> onUpdateListener;
    private T prevValue;

    public ReactiveSharedPref(SharedPreferences prefs, ObservationPolicy<T> observationPolicy) {
        this.prefs = prefs;
        this.observationPolicy = observationPolicy;

        this.handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (onUpdateListener != null) {
                    onUpdateListener.onPreferenceValueUpdated((T) msg.obj);
                }
            }
        };
    }

    public void setOnUpdateListener(OnUpdateListener<T> onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    private void scheduleNotifyPreferenceValueUpdated(T value) {
        unscheduleNotifyPreferenceValueUpdated();
        handler.sendMessageDelayed(handler.obtainMessage(0, value), DEBOUNCE_MS);
    }

    private void unscheduleNotifyPreferenceValueUpdated() {
        handler.removeMessages(0);
    }

    public void sub() {
        prevValue = observationPolicy.getValueFromSharedPreference(prefs);
        scheduleNotifyPreferenceValueUpdated(prevValue);

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unsub() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
        unscheduleNotifyPreferenceValueUpdated();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!observationPolicy.isTargetKey(key) || onUpdateListener == null) return;

            T newValue = observationPolicy.getValueFromSharedPreference(sharedPreferences);
            if (prevValue == null) {
                if (newValue == null) return;

                scheduleNotifyPreferenceValueUpdated(newValue);
            } else if (!prevValue.equals(newValue)) {
                scheduleNotifyPreferenceValueUpdated(newValue);
            }
            prevValue = newValue;
        }
    };
}
