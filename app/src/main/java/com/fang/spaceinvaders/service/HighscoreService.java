package com.fang.spaceinvaders.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.util.PreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HighscoreService extends IntentService {

    public HighscoreService() {
        super("HighscoreService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String playerName = PreferenceUtils.getInstance(getApplicationContext()).getString(R.string.pref_player_name_key);
        database.getReference().child("ScoreList").orderByValue().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(playerName)) {
                        sendNotification(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String highestPlayer) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("misc", "Misc", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "misc")
                .setContentTitle("Someone has a higher score than you")
                .setContentText(highestPlayer + " has surpassed your score in SpaceInvaders")
                .setSmallIcon(R.mipmap.ic_launcher_round);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(500, builder.build());
    }
}
