package com.devinbrown.streaminglib.rtp;

import android.media.MediaDescription;
import android.media.MediaFormat;
import android.util.Pair;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * RtpStream encapsulates an RTP/RTCP session
 * <p>
 * Reference: https://tools.ietf.org/html/rfc3550
 */

public class RtpStream {
    enum RtpStreamState {NEW, INITIALIZED, CONFIGURED, STREAMING, PLAYING, PAUSED, FINISHED}

    public enum RtpProtocol {TCP, UDP}

    public enum StreamType {SERVER, CLIENT}

    public enum Delivery {UNICAST, MULTICAST}

    private static final int STARTING_UDP_RTP_PORT = 50000;

    // UDP
    Pair<Integer, Integer> localRtpPorts;
    Pair<Integer, Integer> remoteRtpPorts;

    // TCP (RTSP Interleaved)
    Pair<Integer, Integer> interleavedRtpChannels;

    RtpProtocol rtpProtocol;
    StreamType streamType;
    Delivery delivery;
    String sessionId;
    Integer timeout;
    RtpStreamState state = RtpStreamState.NEW;
    MediaDescription mediaDescription;
    MediaFormat format;

    // UDP
    DatagramSocket rtpSocket;
    DatagramSocket rtcpSocket;

    void setupUdpPorts() throws SocketException {
        int port = STARTING_UDP_RTP_PORT;
        while (rtpSocket == null && rtcpSocket == null) {
            if (port >= 0xFFFF) {
                throw new SocketException("Unable to create two consecutive sockets.");
            }
            try {
                rtpSocket = new DatagramSocket(port);
                rtcpSocket = new DatagramSocket(port + 1);
            } catch (SocketException e) {
                rtpSocket = null;
                rtcpSocket = null;
                port += 2;
            }
        }
    }

    /**
     * TODO: Make RTCP run in an extension of Runnable or make RTCP Client and Server and base class like RTP
     */
    void setupRtcp() {
        Thread rtcpThread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        rtcpThread.start();
    }

    void validateState(RtpStreamState newState, RtpStreamState requiredState) throws IllegalStateException {
        if (state != requiredState) {
            String msg = "RtpClientStream can only enter " + newState.name() + " state from the " +
                    requiredState.name() + " state (current RtpStreamState: <" + state.name() + ">)";
            throw new IllegalStateException(msg);
        }
    }

    void validateRtpProtocol(RtpProtocol requiredProtocol) throws IllegalStateException {
        if (rtpProtocol != requiredProtocol) {
            String msg = "RtpClientStream must be initialized for " + requiredProtocol.name() +
                    " (current RtpProtocol: <" + rtpProtocol.name() + ">)";
            throw new IllegalStateException(msg);
        }
    }

    public RtpProtocol getRtpProtocol() {
        return rtpProtocol;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public Pair<Integer, Integer> getLocalRtpPorts() {
        return localRtpPorts;
    }

    public Pair<Integer, Integer> getRemoteRtpPorts() {
        return remoteRtpPorts;
    }

    public Pair<Integer, Integer> getInterleavedRtpChannels() {
        return interleavedRtpChannels;
    }
}
