package net.alhrairyalbraa.kalmani.utils.unity;

import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;

public class MainUnityActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        handleIntent(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        if (intent.getExtras().containsKey("doQuit") && (mUnityPlayer != null)) {
                finish();

        }
    }

    @Override
    public void onUnityPlayerUnloaded() {
        SharedClass.showMainActivity("");
    }

}
