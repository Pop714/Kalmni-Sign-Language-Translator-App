package net.alhrairyalbraa.kalmani.utils.unity;

import android.app.Activity;
import android.content.Intent;

import com.unity3d.player.UnityPlayer;

import net.alhrairyalbraa.kalmani.ui.general.HomeFragment;


public class SharedClass {

    private SharedClass() {}

    public static void showMainActivity(String setToColor) {
        showMainActivity(UnityPlayer.currentActivity, setToColor);
    }

    public static void showMainActivity(Activity activity, String setToColor) {
        Intent intent = new Intent(activity, HomeFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("setColor", setToColor);
        activity.startActivity(intent);
    }


}
