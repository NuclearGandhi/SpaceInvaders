package com.fang.spaceinvaders.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.activity.MainActivity;
import com.fang.spaceinvaders.game.GameData;
import com.fang.spaceinvaders.game.GameView;
import com.fang.spaceinvaders.game.SpaceInvaders;
import com.fang.spaceinvaders.util.PreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameFragment extends Fragment {

    private View mRootView;
    private GameView mGameView;

    private ViewGroup mGameOverOverlay;

    private int mHighscore = 0;

    private BroadcastReceiver mScoreBroadcastReceiver = new ScoreUpdatedReceiver();
    private FirebaseDatabase mDatabase;

    @BindView(R.id.game_score) TextView mGameScore;
    @BindView(R.id.game_high_score) TextView mGameHighscore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, mRootView);
        mGameView = mRootView.findViewById(R.id.game_view);

        mGameOverOverlay = mRootView.findViewById(R.id.game_over_overlay);

        hideOverlays();
        mDatabase = FirebaseDatabase.getInstance();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameView.resume();
        EventBus.getDefault().register(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mScoreBroadcastReceiver, new IntentFilter(SpaceInvaders.ACTION_SCORE_UPDATED));
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameView.pause();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mScoreBroadcastReceiver);
    }

    @OnClick(R.id.button_retry)
    public void resetGame(View view) {
        mGameView.reset(getContext());
        updateScore(0);
        hideOverlays();
    }

    @OnClick(R.id.button_main_menu)
    public void returnToMainMenu(View view) {
        EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_WELCOME));
        mGameView.resume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameOver(GameOverEvent event) {
        String playerName = PreferenceUtils.getInstance(getContext()).getString(R.string.pref_player_name_key);
        mDatabase.getReference("ScoreList").child(playerName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long highscore = (long) dataSnapshot.getValue();
                if (GameData.sScore > highscore) {
                    mDatabase.getReference("ScoreList").child(playerName).setValue(GameData.sScore);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showOverlay(mGameOverOverlay);
        mGameView.pause();
    }

    @SuppressLint("DefaultLocale")
    private void updateScore(int score) {
        mGameScore.setText(String.format("SCORE:\n%4d", score));

        if (score > mHighscore) {
            mHighscore = score;
            mGameHighscore.setText(String.format("HI-SCORE:\n%4d", score));
        }
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

    public class ScoreUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int score = intent.getIntExtra(SpaceInvaders.EXTRA_UPDATED_SCORE, 0);
            updateScore(score);
        }
    }

    public static class GameOverEvent {}
}
