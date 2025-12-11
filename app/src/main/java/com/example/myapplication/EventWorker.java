package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myapplication.Models.Artist;
import com.example.myapplication.Network.ApiClient;
import com.example.myapplication.Network.ApiResponse;
import com.example.myapplication.Network.ApiService;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class EventWorker extends Worker {

    public EventWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        Log.d("EventWorker", "Checking for new events in the background...");
        Context context = getApplicationContext();
        ApiClient.initialize(context);
        FollowManager followManager = new FollowManager(context);
        ApiService apiService = ApiClient.getApiService();
        List<Artist> myArtists = followManager.getFollowedArtists();
        if (myArtists.isEmpty()) {
            return Result.success();
        }
        for (Artist localArtist : myArtists) {
            try {
                Response<ApiResponse<Artist>> response = apiService.getInviterDetail(localArtist.getId()).execute();

                if (response.isSuccessful() && response.body() != null) {
                    Artist serverArtist = response.body().getData();
                    if (serverArtist != null) {
                        followManager.checkForNewEvents(serverArtist);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Result.retry();
            }
        }

        Log.d("EventWorker", "COMPLETE CHECKING!");
        return Result.success();
    }
}