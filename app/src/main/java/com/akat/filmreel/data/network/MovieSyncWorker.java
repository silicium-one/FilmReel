package com.akat.filmreel.data.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.akat.filmreel.R;
import com.akat.filmreel.data.db.AppDatabase;
import com.akat.filmreel.data.model.ApiResponse;
import com.akat.filmreel.data.model.Movie;
import com.akat.filmreel.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class MovieSyncWorker extends Worker {

    private static final String CHANNEL_ID = "sync_channel";

    public MovieSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(context);
        ApiManager manager = ApiManager.getInstance();

        // Make request - update first page
        Call<ApiResponse> call = manager.getApiService().getTopRatedMovies(1);

        try {
            ApiResponse apiResponse = call.execute().body();
            if (apiResponse != null) {
                ArrayList<Movie> result = (ArrayList<Movie>) apiResponse.getResults();
                database.topRatedDao().bulkInsert(result);

                showNotification(
                        context,
                        context.getString(R.string.sync_title),
                        context.getString(R.string.sync_desc)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success();
    }

    private void showNotification(Context context, String title, String desc) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = context.getString(R.string.sync_channel_name);

            NotificationChannel channel = new
                    NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        manager.notify(1, builder.build());

    }
}