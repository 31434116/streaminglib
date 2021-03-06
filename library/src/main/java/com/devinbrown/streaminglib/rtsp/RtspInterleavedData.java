package com.devinbrown.streaminglib.rtsp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class RtspInterleavedData extends Rtsp {
    private static final String TAG = "RtspInterleavedData";

    public int channel;
    public byte[] data;

    public RtspInterleavedData(int c, byte[] d) {
        channel = c;
        data = d;
    }

    public static RtspInterleavedData parseInterleavedData(InputStream i) throws IOException {
        RtspInterleavedData r;
        int channel = i.read(), length = (i.read() << 8) & i.read();
        byte[] d = new byte[length];
        int actualLength = i.read(d);
        if (actualLength != length) Log.e(TAG, "Problem reading full RTSP Interleaved Data");
        return new RtspInterleavedData(channel, d);
    }

    @Override
    public byte[] getBytes() {
        return data;
    }
}
