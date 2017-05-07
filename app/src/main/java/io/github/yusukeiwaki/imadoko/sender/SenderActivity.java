package io.github.yusukeiwaki.imadoko.sender;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import io.github.yusukeiwaki.imadoko.R;
import io.github.yusukeiwaki.imadoko.base.BaseActivity;

public class SenderActivity extends BaseActivity {
    private SenderServiceBindingManager bindingManager;
    private ShortUrlObserver shortUrlObserver;

    public static Intent newIntent(Context context) {
        return new Intent(context, SenderActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        if (savedInstanceState == null) {
            TrackingIdUpdateService.start(this);
        }

        bindingManager = new SenderServiceBindingManager(this);
        bindingManager.setOnStateChangedListener(activated -> {
        });

        shortUrlObserver = new ShortUrlObserver(this);
        shortUrlObserver.setOnUpdateListener(shortUrl -> {
            TextView urlText = findViewById2(R.id.url_text);
            urlText.setText(TextUtils.isEmpty(shortUrl) ? "" : shortUrl);
        });

        findViewById(R.id.btn_copy_to_clipboard).setOnClickListener(v -> {
            TextView urlText = findViewById2(R.id.url_text);

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, urlText.getText()));
            Toast.makeText(v.getContext(), "クリップボードにコピーされました", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            TextView urlText = findViewById2(R.id.url_text);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, urlText.getText().toString());

            Intent chooserIntent = Intent.createChooser(intent, "共有");
            startActivity(chooserIntent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindingManager.start();
        shortUrlObserver.sub();
    }

    @Override
    protected void onStop() {
        shortUrlObserver.unsub();
        bindingManager.stop();
        super.onStop();
    }

    private void startPositioning() {
        Intent intent = PositioningRequirementCheckAndStartPositioningActivity.newIntent(this);
        startActivity(intent);
    }
}
