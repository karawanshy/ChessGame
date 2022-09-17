package com.workshop.chess;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class Singleton{

    private static StompClient mStompClient;

    private static Singleton instance = null;

    public static Singleton getInstance(String username) {
        if (instance == null)
            initInstance(username);

        return instance;
    }

    private static void initInstance(String username) {
        instance = new Singleton();
        String TAG = "Chess Websocket";

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("username", username));

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/chess-websocket/websocket");
        mStompClient.connect(headers);


        Disposable dispLifecycle = mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e(TAG, "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d(TAG, "Stomp connection closed");
                    break;
            }
        });
    }

    public static StompClient getmStompClient() {
        return mStompClient;
    }
    
    public static void disconnectStomp() {
        if (mStompClient != null) {
            Log.d("Chess", "disconnected through drop out button");
            mStompClient.disconnect();
            instance = null;
        }
    }
}
