package io.github.yusukeiwaki.imadoko.setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import io.github.yusukeiwaki.imadoko.R;
import io.github.yusukeiwaki.imadoko.base.BaseActivity;
import io.github.yusukeiwaki.imadoko.etc.CurrentUserCache;
import io.github.yusukeiwaki.imadoko.etc.SimpleTextWatcher;
import io.github.yusukeiwaki.imadoko.sender.SenderActivity;

public class SetupActivity extends BaseActivity {
    public static Intent newIntent(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, SetupActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, nextIntent);
        return intent;
    }

    private Intent nextIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        handleIntent();

        final EditText editorUsername = findViewById2(R.id.editor_username);
        final Button btnProceed = findViewById2(R.id.btn_proceed);

        editorUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                btnProceed.setEnabled(s.length() >= 4);
            }
        });
        editorUsername.setOnEditorActionListener((v, actionId, event) -> {
            proceed(editorUsername.getText().toString());
            return true;
        });

        btnProceed.setOnClickListener(v -> {
            proceed(editorUsername.getText().toString());
        });
    }

    private void proceed(String username) {
        CurrentUserCache.get(this).edit()
                .putString(CurrentUserCache.KEY_USERNAME, username)
                .apply();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Intent nextIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (nextIntent != null) {
                this.nextIntent = nextIntent;
                return;
            }
        }
        this.nextIntent = SenderActivity.newIntent(this);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener usernameListener = (prefs, key) -> {
        if (CurrentUserCache.KEY_USERNAME.equals(key)) {
            String username = prefs.getString(CurrentUserCache.KEY_USERNAME, null);
            if (!TextUtils.isEmpty(username)) {
                startActivity(nextIntent);
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        CurrentUserCache.get(this).registerOnSharedPreferenceChangeListener(usernameListener);
    }

    @Override
    protected void onPause() {
        CurrentUserCache.get(this).unregisterOnSharedPreferenceChangeListener(usernameListener);

        super.onPause();
    }
}
