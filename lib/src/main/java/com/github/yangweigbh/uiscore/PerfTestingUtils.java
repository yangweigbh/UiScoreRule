package com.github.yangweigbh.uiscore;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;

/**
 * Centralized methods to ensure tests perform certain actions in the same manner.
 */
public class PerfTestingUtils {
    /**
     * Retrieve the directory where test files should be written to.
     * This directory is local to the application and removed when the app is uninstalled.
     */
    public static File getTestDir(String className, String testName) {

        File rootDir = getAppContext().getFilesDir();

        // Create a test data subdirectory.
        File testFileDir = new File(rootDir, "testdata");
        if (getTranslatedTestName(className, testName) != null) {
            testFileDir = new File(testFileDir, getTranslatedTestName(className, testName));
        }

        if (!testFileDir.exists()) {
            if (!testFileDir.mkdirs()) {
                throw new RuntimeException("Unable to create test file directory.");
            }
        }
        return testFileDir;

    }

    /**
     * Retrieve the app-under-test's {@code Context}.
     */
    public static Context getAppContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    private static String getTranslatedTestName(String className, String testName) {
        if (className == null || testName == null) {
            return null;
        }
        String base = className + "_" + testName;

        return base;
    }

    /**
     * Retrieve a file handle that is within the testing directory where tests should be written to.
     */
    public static File getTestFile(String className, String testName, String filename) {
        return new File(getTestDir(className, testName), filename);
    }
}
