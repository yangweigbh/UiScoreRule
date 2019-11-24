package com.github.yangweigbh.uiscore;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.FrameMetrics;

import androidx.test.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.List;

public class DumpGfxinfoRule implements TestRule, Application.ActivityLifecycleCallbacks, CollectorThread.CollectorListener {
    private String mTestName;
    private boolean mIsPerfTest;
    private Activity mActivity;
    private CollectorThread mCollectorThread;
    private Application mAppContext;

    @Override
    public Statement apply(final Statement base, Description description) {
        mTestName = description.getTestClass().getSimpleName() + "." + description.getMethodName();
        mIsPerfTest = description.getAnnotation(PerfTest.class) != null;

        if (mIsPerfTest) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    preTest();
                    base.evaluate();
                    postTest();
                }
            };
        } else {
            return base;
        }
    }

    private void preTest() {
        mAppContext = (Application)
            InstrumentationRegistry.getTargetContext().getApplicationContext();
        mAppContext.registerActivityLifecycleCallbacks(this);
    }

    private void postTest() {
        List<FrameMetrics> frameMetrics = mCollectorThread.getFrameTimingStats();

        if (frameMetrics.size() > 0) {
            UiBenchmarkResult uiBenchmarkResult = new UiBenchmarkResult(frameMetrics);
            GlobalResultsStore.getInstance()
                .storeRunResults(mTestName, uiBenchmarkResult);
        }
        mCollectorThread.quitCollector();
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override public void onActivityStarted(Activity activity) {
        mActivity = activity;

        mCollectorThread = new CollectorThread(this);
        mCollectorThread.start();
    }

    @Override public void onActivityResumed(Activity activity) {

    }

    @Override public void onActivityPaused(Activity activity) {

    }

    @Override public void onActivityStopped(Activity activity) {

    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override public void onActivityDestroyed(Activity activity) {
        if (activity == mActivity) {
            mActivity = null;
        }
    }

    @Override public void onCollectorThreadReady() {
        mCollectorThread.attachToWindow(mActivity.getWindow());
        mCollectorThread.markInteractionStart();
    }

    @Override public void onPostInteraction(List<FrameMetrics> stats) {

    }
}
