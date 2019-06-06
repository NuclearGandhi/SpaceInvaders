package com.fang.spaceinvaders.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.Preference;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.activity.MainActivity;
import com.fang.spaceinvaders.util.PreferenceUtils;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameFragment extends Fragment {

    @BindView(R.id.edit_name) TextInputEditText mEditName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_name, container, false);
        ButterKnife.bind(this, rootView);

        String name = PreferenceUtils.getInstance(getContext()).getString(R.string.pref_player_name_key);
        mEditName.setText(name);
        return rootView;
    }

    @OnClick(R.id.button_save)
    public void save(View view) {
        PreferenceUtils.getInstance(getContext()).putString(R.string.pref_player_name_key, mEditName.getText().toString());
        EventBus.getDefault().post(new MainActivity.FragmentChangeEvent(MainActivity.FRAGMENT_WELCOME));
    }
}
