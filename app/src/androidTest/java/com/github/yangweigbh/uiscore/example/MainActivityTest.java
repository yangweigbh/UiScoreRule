package com.github.yangweigbh.uiscore.example;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.github.yangweigbh.uiscore.UiScoreRule;
import com.github.yangweigbh.uiscore.PerfTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public UiScoreRule mUiScoreRule = new UiScoreRule();

    @Rule
    public ActivityTestRule mActivityTestRule = new ActivityTestRule(MainActivity.class);

    protected UiDevice mDevice;
    private String mTargetPackage;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(getInstrumentation());
        mTargetPackage = InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName();
    }

    @Test
    @PerfTest
    public void scrollRecyclerView() {
        mDevice.waitForIdle();
        UiObject2 recyclerView = mDevice.wait(Until.findObject(By.res(mTargetPackage, "recycler_view")), 10000);

        for (int i = 0; i < 10; i++) {
            recyclerView.swipe(Direction.UP, 0.5f, 3000);

            mDevice.waitForIdle();

            recyclerView.swipe(Direction.UP, 0.5f, 3000);

            mDevice.waitForIdle();

            recyclerView.swipe(Direction.DOWN, 0.5f, 3000);

            mDevice.waitForIdle();

            recyclerView.swipe(Direction.DOWN, 0.5f, 3000);

            mDevice.waitForIdle();
        }
    }
}
