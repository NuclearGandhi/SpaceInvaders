package com.fang.spaceinvaders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.spaceinvaders.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreHolder> {

    private List<Score> mData;
    private Context mContext;

    public ScoreAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ScoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_score, parent, false);
        return new ScoreHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreHolder holder, int position) {
        Score score = mData.get(position);
        holder.name.setText(score.name);
        holder.score.setText(score.score + "");
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    public void setData(List<Score> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public class ScoreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name) TextView name;
        @BindView(R.id.score) TextView score;

        public ScoreHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Score implements Comparable<Score> {
        String name;
        long score;

        public Score(String name, long score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(Score o) {
            if (score > o.score) return -1;
            else if (score < o.score) return 1;
            return 0;
        }
    }
}
