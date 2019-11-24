package com.android.launcher3.util;

import androidx.annotation.IntDef;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FrameTimingStats {
    @IntDef({
            Index.FLAGS,
            Index.INTENDED_VSYNC,
            Index.VSYNC,
            Index.OLDEST_INPUT_EVENT,
            Index.NEWEST_INPUT_EVENT,
            Index.HANDLE_INPUT_START,
            Index.ANIMATION_START,
            Index.PERFORM_TRAVERSALS_START,
            Index.DRAW_START,
            Index.SYNC_QUEUED,
            Index.SYNC_START,
            Index.ISSUE_DRAW_COMMANDS_START,
            Index.SWAP_BUFFERS,
            Index.FRAME_COMPLETED,
    })
    public @interface Index {
        int FLAGS = 0;
        int INTENDED_VSYNC = 1;
        int VSYNC = 2;
        int OLDEST_INPUT_EVENT = 3;
        int NEWEST_INPUT_EVENT = 4;
        int HANDLE_INPUT_START = 5;
        int ANIMATION_START = 6;
        int PERFORM_TRAVERSALS_START = 7;
        int DRAW_START = 8;
        int SYNC_QUEUED = 9;
        int SYNC_START = 10;
        int ISSUE_DRAW_COMMANDS_START = 11;
        int SWAP_BUFFERS = 12;
        int FRAME_COMPLETED = 13;

        int FRAME_STATS_COUNT = 14; // must always be last
    }

    private final long[] mStats;

    FrameTimingStats(long[] stats) {
        mStats = Arrays.copyOf(stats, Index.FRAME_STATS_COUNT);
    }

    public FrameTimingStats(DataInputStream inputStream) throws IOException {
        mStats = new long[Index.FRAME_STATS_COUNT];
        update(inputStream);
    }

    public void update(DataInputStream inputStream) throws IOException {
        for (int i = 0; i < mStats.length; i++) {
            mStats[i] = inputStream.readLong();
        }
    }

    public long get(@Index int index) {
        return mStats[index];
    }

    public long[] data() {
        return mStats;
    }
}
