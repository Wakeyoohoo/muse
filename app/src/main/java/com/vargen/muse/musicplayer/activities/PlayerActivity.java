/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vargen.muse.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.vargen.muse.musicplayer.R;
import com.vargen.muse.musicplayer.music.PlayerService;
import com.vargen.muse.musicplayer.view.ProgressView;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public abstract class PlayerActivity extends AppCompatActivity {

    private PlayerService mService;
    private boolean mBound = false;
    private TextView mTimeView;
    private TextView mDurationView;
    private ProgressView mProgressView;

    private static class UpdateProgressHandler extends Handler {
        private final WeakReference<PlayerService> mPlayerService;
        private final WeakReference<PlayerActivity> mPlayerActivity;

        public UpdateProgressHandler(PlayerService service, PlayerActivity activity) {
            mPlayerService = new WeakReference<>(service);
            mPlayerActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final int position = mPlayerService.get().getPosition();
            final int duration = mPlayerService.get().getDuration();
            mPlayerActivity.get().onUpdateProgress(position, duration);
            sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
        }
    }

    // Handler隐式的持有它的外部类的引用，这个引用会一直存在直到这个消息被处理，可能会产生内存泄漏

//    private final Handler mUpdateProgressHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            final int position = mService.getPosition();
//            final int duration = mService.getDuration();
//            onUpdateProgress(position, duration);
//            sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
//        }
//    };

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to PlayerService, cast the IBinder and get PlayerService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            onBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBound = false;
            onUnbind();
        }
    };

    private UpdateProgressHandler mUpdateProgressHandler = new UpdateProgressHandler(mService, this);

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setProgress(position);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to PlayerService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    private void initView() {
        mTimeView = (TextView) findViewById(R.id.time);
        mDurationView = (TextView) findViewById(R.id.duration);
        mProgressView = (ProgressView) findViewById(R.id.progress);
    }

    @Override
    protected void onDestroy() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (mUpdateProgressHandler != null) {
            mUpdateProgressHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void onBind() {
        mUpdateProgressHandler.sendEmptyMessage(0);
    }

    private void onUnbind() {
        mUpdateProgressHandler.removeMessages(0);
    }

    public void play() {
        mService.play();
    }

    public void pause() {
        mService.pause();
    }

}
