package com.fang.spaceinvaders.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.activity.MainActivity;
import com.fang.spaceinvaders.service.HighscoreService;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeFragment extends Fragment {

    private View mRootView;
    private Button mSelectedButton;

    @BindView(R.id.button_play) Button mPlayButton;
    @BindView(R.id.button_settings) Button mSettingsButton;
    @BindView(R.id.button_scoreboard) Button mScoreboardButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, mRootView);

        Intent intent = new Intent(getContext(), HighscoreService.class);
        getContext().startService(intent);
        return mRootView;
    }

    @OnClick({R.id.button_play, R.id.button_settings, R.id.button_scoreboard})
    public void selectOption(View view) {
        Button button = (Button) view;
        button.setText(">" + button.getText());
        if (mSelectedButton != null) mSelectedButton.setText(mSelectedButton.getText().toString().substring(1));
        mSelectedButton = button;
    }

    @OnClick(R.id.button_start)
    public void startGame(View view) {
        if (mSelectedButton == mPlayButton) {
            EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_GAME));
        } else if (mSelectedButton == mSettingsButton) {
            EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_CHANGE_NAME));
        } else if (mSelectedButton == mScoreboardButton) {
            EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_SCOREBOARD));
        }
    }
}
