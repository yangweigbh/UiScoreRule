package com.github.yangweigbh.uiscore;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.FrameMetrics;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public final class CollectorThread extends HandlerThread {
    private FrameStatsCollector mCollector;
    private Window mAttachedWindow;
    private List<FrameMetrics> mFrameTimingStats;
    private WeakReference<CollectorListener> mListener;

    private volatile boolean mCollecting;


    public interface CollectorListener {
        void onCollectorThreadReady();
    }

    static boolean tripleBuffered = false;
    static int janks = 0;
    static int total = 0;
    @TargetApi(24)
    private class FrameStatsCollector implements Window.OnFrameMetricsAvailableListener {
        @Override
        public void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics, int dropCount) {
            if (!mCollecting) {
                return;
            }

            mFrameTimingStats.add(new FrameMetrics(frameMetrics));
        }
    }

    public CollectorThread(CollectorListener listener) {
        super("FrameStatsCollectorThread", Process.THREAD_PRIORITY_BACKGROUND);
        mFrameTimingStats = new CopyOnWriteArrayList<>();
        mListener = new WeakReference<>(listener);
    }

    @TargetApi(24)
    public void attachToWindow(Window window) {
        if (mAttachedWindow != null) {
            mAttachedWindow.removeOnFrameMetricsAvailableListener(mCollector);
        }

        mAttachedWindow = window;
        window.addOnFrameMetricsAvailableListener(mCollector, new Handler(getLooper()));
    }

    @TargetApi(24)
    public synchronized void detachFromWindow() {
        if (mAttachedWindow != null) {
            mAttachedWindow.removeOnFrameMetricsAvailableListener(mCollector);
        }

        mAttachedWindow = null;
    }

    @TargetApi(24)
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mCollector = new FrameStatsCollector();

        CollectorListener listener = mListener.get();
        if (listener != null) {
            listener.onCollectorThreadReady();
        }
    }

    public boolean quitCollector() {
        stopCollecting();
        detachFromWindow();
        tripleBuffered = false;
        total = 0;
        janks = 0;
        return quit();
    }

    void stopCollecting() {
        if (!mCollecting) {
            return;
        }

        mCollecting = false;
    }

    public void markInteractionStart() {
        mFrameTimingStats.clear();
        mCollecting = true;
    }

    public List<FrameMetrics> getFrameTimingStats() {
        return mFrameTimingStats;
    }
}
