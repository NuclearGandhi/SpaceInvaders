package com.fang.spaceinvaders.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.activity.MainActivity;
import com.fang.spaceinvaders.game.GameView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameFragment extends Fragment {

    private View mRootView;
    private GameView mGameView;

    private ViewGroup mGameOverOverlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, mRootView);
        mGameView = mRootView.findViewById(R.id.game_view);

        mGameOverOverlay = mRootView.findViewById(R.id.game_over_overlay);

        hideOverlays();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameView.resume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameView.pause();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.button_retry)
    public void resetGame(View view) {
        mGameView.reset(getContext());
        hideOverlays();
    }

    @OnClick(R.id.button_main_menu)
    public void returnToMainMenu(View view) {
        EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_WELCOME));
        mGameView.resume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameOver(GameOverEvent event) {
        showOverlay(mGameOverOverlay);
        mGameView.pause();
    }

    private void hideOverlays() {
        hideOverlay(mGameOverOverlay);
    }

    private void hideOverlay(ViewGroup overlay) {
        overlay.setAlpha(0);
        for (int i = 0; i < overlay.getChildCount(); i++) {
            View v = overlay.getChildAt(i);
            v.setClickable(false);
            v.setFocusable(false);
        }
    }

    private void showOverlay(ViewGroup overlay) {
        overlay.animate().alpha(1f);
        for (int i = 0; i < overlay.getChildCount(); i++) {
            View v = overlay.getChildAt(i);
            v.setClickable(true);
            v.setFocusable(true);
        }
    }

    public static class GameOverEvent {}
}
