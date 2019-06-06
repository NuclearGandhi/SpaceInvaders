package com.fang.spaceinvaders.activity;

import android.os.Bundle;
import android.view.View;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.fragment.GameFragment;
import com.fang.spaceinvaders.fragment.NameFragment;
import com.fang.spaceinvaders.fragment.ScoreboardFragment;
import com.fang.spaceinvaders.fragment.WelcomeFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FRAGMENT_WELCOME, FRAGMENT_GAME, FRAGMENT_CHANGE_NAME, FRAGMENT_SCOREBOARD})
    public @interface FragmentName {}

    public static final int FRAGMENT_WELCOME = 0;
    public static final int FRAGMENT_GAME = 1;
    public static final int FRAGMENT_CHANGE_NAME = 2;
    public static final int FRAGMENT_SCOREBOARD = 3;

    private static final String KEY_FRAGMENT = "fragment";

    private FragmentManager mFragmentManager;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Timber.plant(new Timber.DebugTree());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) mFragment = mFragmentManager.getFragment(savedInstanceState, KEY_FRAGMENT);
        if (mFragment == null) mFragment = new WelcomeFragment();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment, mFragment, KEY_FRAGMENT)
                .commit();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentManager.putFragment(outState, KEY_FRAGMENT, mFragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FragmentChangeEvent event) {
        switch (event.fragmentName) {
            case FRAGMENT_WELCOME:
                mFragment = new WelcomeFragment();
                break;
            case FRAGMENT_GAME:
                mFragment = new GameFragment();
                break;
            case FRAGMENT_CHANGE_NAME:
                mFragment = new NameFragment();
                break;
            case FRAGMENT_SCOREBOARD:
                mFragment = new ScoreboardFragment();
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment, mFragment, KEY_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public static class FragmentChangeEvent {
        public @FragmentName int fragmentName;

        public FragmentChangeEvent(@FragmentName int fragmentName) {
            this.fragmentName = fragmentName;
        }
    }
}
