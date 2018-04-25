package com.example.gaetan.babyphoneapp.bluetooth;


import android.os.Handler;

public class MessageHandler {
    public static final int MSG_NOT_CONNECTED = 10;
    public static final int MSG_CONNECTING = 11;
    public static final int MSG_CONNECTED = 12;
    public static final int MSG_CONNECTION_FAILED = 13;
    public static final int MSG_CONNECTION_LOST = 14;
    public static final int MSG_LINE_READ = 21;
    public static final int MSG_BYTES_WRITTEN = 22;

    private final Handler handler;

    public MessageHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendLineRead(String line) {
        handler.obtainMessage(MSG_LINE_READ, -1, -1, line).sendToTarget();
    }

    public void sendBytesWritten(byte[] bytes) {
        handler.obtainMessage(MSG_BYTES_WRITTEN, -1, -1, bytes).sendToTarget();
    }

    public void sendConnectingTo(String deviceName) {
        sendMessage(MSG_CONNECTING, deviceName);
    }

    public void sendConnectedTo(String deviceName) {
        sendMessage(MSG_CONNECTED, deviceName);
    }

    public void sendNotConnected() {
        sendMessage(MSG_NOT_CONNECTED);
    }

    public void sendConnectionFailed() {
        sendMessage(MSG_CONNECTION_FAILED);
    }

    public void sendConnectionLost() {
        sendMessage(MSG_CONNECTION_LOST);
    }

    private void sendMessage(int messageId, String deviceName) {
        handler.obtainMessage(messageId, -1, -1, deviceName).sendToTarget();
    }

    private void sendMessage(int messageId) {
        handler.obtainMessage(messageId).sendToTarget();
    }
}