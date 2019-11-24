package com.github.yangweigbh.uiscore;

import java.util.HashMap;
import java.util.Map;

public class GlobalResultsStore {
    private static GlobalResultsStore sInstance;

    private Map<String, UiBenchmarkResult> resultMap = new HashMap<>();

    private GlobalResultsStore() {
    }

    public static GlobalResultsStore getInstance() {
        if (sInstance == null) {
            sInstance = new GlobalResultsStore();
        }

        return sInstance;
    }

    public void storeRunResults(String testName, UiBenchmarkResult result) {
        resultMap.put(testName, result);
    }

    public Map<String, UiBenchmarkResult> loadTestResults() {
        return resultMap;
    }
}
