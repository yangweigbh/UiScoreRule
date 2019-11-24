package com.github.yangweigbh.uiscore;

import android.util.Log;
import android.view.FrameMetrics;

import com.jakewharton.picnic.BorderStyle;
import com.jakewharton.picnic.Cell;
import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Row;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TableStyle;
import com.jakewharton.picnic.TextAlignment;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Initiate pre-test and post-test procedures; including cleaning and moving files from the
 * internal app data directory to the external one.  Also perform writing to test-start and test-end
 * files which are used to indicate whether there was a fatal test exception
 * (e.g. OutOfMemoryException).
 */
public class TestListener extends RunListener {
    private static final String LOG_TAG = "TestListener";
    private static final String RES_TAG = "UiScore";

    @Override
    public void testRunStarted(Description description) throws Exception {
        Log.w(LOG_TAG, "Test run started.");

        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        Log.w(LOG_TAG, "Test run finished.");

        Map<String, UiBenchmarkResult> overallRes = GlobalResultsStore.getInstance().loadTestResults();
        if (overallRes.size() > 0) {
            SummaryStatistics summaryStatistics = new SummaryStatistics();

            List<Row> header = new ArrayList<>();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("Test Name", 1, 1, null));
            cells.add(new Cell("Total Frames", 1, 1, null));
            cells.add(new Cell("Average frame duration", 1, 1, null));
            cells.add(new Cell("Frame duration 99th", 1, 1, null));
            cells.add(new Cell("Frame duration 95th", 1, 1, null));
            cells.add(new Cell("Frame duration 90th", 1, 1, null));
            cells.add(new Cell("Longest frame", 1, 1, null));
            cells.add(new Cell("Shortest frame", 1, 1, null));
            cells.add(new Cell("Bad frames mean", 1, 1, null));
            cells.add(new Cell("Jank Frame Count", 1, 1, null));
            cells.add(new Cell("Bad frames standDeviation", 1, 1, null));
            cells.add(new Cell("Score", 1, 1, null));
            header.add(new Row(cells, null));

            List<Row> body = new ArrayList<>();

            for (Map.Entry<String, UiBenchmarkResult> e: overallRes.entrySet()) {
                String testName = e.getKey();
                UiBenchmarkResult uiBenchmarkResult = e.getValue();

                cells = new ArrayList<>();

                cells.add(new Cell(testName, 1, 1, null));
                cells.add(new Cell(Integer.toString(uiBenchmarkResult.getTotalFrameCount()), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getAverage(FrameMetrics.TOTAL_DURATION)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getPercentile(FrameMetrics.TOTAL_DURATION, 99)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getPercentile(FrameMetrics.TOTAL_DURATION, 95)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getPercentile(FrameMetrics.TOTAL_DURATION, 90)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getMaximum(FrameMetrics.TOTAL_DURATION)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getMinimum(FrameMetrics.TOTAL_DURATION)), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getScore().mean), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getScore().jankFrameCount), 1, 1, null));
                cells.add(new Cell(String.format("%.2f", uiBenchmarkResult.getScore().standDeviation), 1, 1, null));
                cells.add(new Cell(String.format("%d", uiBenchmarkResult.getScore().score), 1, 1, null));
                body.add(new Row(cells, null));

                float score = uiBenchmarkResult.getScore().score;
                summaryStatistics.addValue(score);
            }

            cells = new ArrayList<>();

            cells.add(new Cell("Total", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell("", 1, 1, null));
            cells.add(new Cell(Double.toString(summaryStatistics.getGeometricMean()), 1, 1, null));
            body.add(new Row(cells, null));

            Table table = new Table(new TableSection(header, null),
                    new TableSection(body, null),
                    null,
                    createCellStyle(),
                    new TableStyle(BorderStyle.Solid));
            Log.i(RES_TAG, "Result: \n\r" + table.toString());
        } else {
            Log.i(RES_TAG, "no perf test");
        }

        super.testRunFinished(result);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        logTestFailure(failure);
    }

    @SuppressWarnings("DefaultCharset")
    private void logTestFailure(Failure failure) throws IOException, InterruptedException {
        File failureLogFile = PerfTestingUtils.getTestFile(
                failure.getDescription().getClassName(),
                failure.getDescription().getMethodName(), "test.failure.log");

        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        String eol = System.getProperty("line.separator");
        try {
            fileWriter = new FileWriter(failureLogFile);
            printWriter = new PrintWriter(fileWriter);
            printWriter.append(failure.getTestHeader());
            printWriter.append(eol);
            failure.getException().printStackTrace(printWriter);
            printWriter.append(eol);
        } finally {
            if (printWriter != null) { try { printWriter.close(); } catch (Exception ignored) { } }
            if (fileWriter != null) { try { fileWriter.close(); } catch (Exception ignored) { } }
        }
    }

    private CellStyle createCellStyle() {
        return new CellStyle(0, 0, 0, 0, true, true, true, true, TextAlignment.MiddleCenter);
    }
}
