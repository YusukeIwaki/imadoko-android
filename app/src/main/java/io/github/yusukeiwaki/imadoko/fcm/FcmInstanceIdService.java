package io.github.yusukeiwaki.imadoko.fcm;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = FcmInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        startService(FcmRegistrationService.newIntent(getApplicationContext()));
    }
}
