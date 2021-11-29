package com.example.tutorial.opus;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class Annotations {
    public static final int SPL_RT_8K = 8000;
    public static final int SPL_RT_12K = 12000;
    public static final int SPL_RT_16K = 16000;
    public static final int SPL_RT_24K = 24000;
    public static final int SPL_RT_48K = 48000;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SPL_RT_8K, SPL_RT_12K, SPL_RT_16K, SPL_RT_24K, SPL_RT_48K})
    public @interface SamplingRate {
    }


    public static final int CHANNEL_ONE = 1;
    public static final int CHANNEL_TWO = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHANNEL_ONE, CHANNEL_TWO})
    public @interface NumberOfChannels {
    }
}
