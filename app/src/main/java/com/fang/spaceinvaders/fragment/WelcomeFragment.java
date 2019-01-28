package com.fang.spaceinvaders.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeFragment extends Fragment {

    private View mRootView;
    @BindView(R.id.button_start) Button mStartButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @OnClick(R.id.button_start)
    public void onGameStart(View view) {
        EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_GAME));
    }
}
