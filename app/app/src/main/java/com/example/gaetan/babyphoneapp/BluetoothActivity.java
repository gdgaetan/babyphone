package com.example.gaetan.babyphoneapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;

import com.example.gaetan.babyphoneapp.api.ApiBluetoothDevice;
import com.example.gaetan.babyphoneapp.bluetooth.BluetoothDeviceConnector;
import com.example.gaetan.babyphoneapp.bluetooth.MessageHandler;

public class BluetoothActivity extends Activity {

    private static final String TAG = BluetoothActivity.class.getSimpleName();
    private static final boolean D = true;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_LAUNCH_EMAIL_APP = 3;
    private static final int MENU_SETTINGS = 4;

    private static final String SAVED_PENDING_REQUEST_ENABLE_BT = "PENDING_REQUEST_ENABLE_BT";

    // Layout Views
    private TextView mStatusView;
    private ListView mConversationView;
    private EditText mOutEditText;
    private View mSendTextContainer;

    // Toolbar
    private ImageButton mToolbarConnectButton;

    private ArrayAdapter<String> mConversationArrayAdapter;
    private BluetoothDeviceConnector mDeviceConnector;

    // State variables
    private boolean paused = false;
    private boolean connected = false;

    // do not resend request to enable Bluetooth
    // if there is a request already in progress
    // See: https://code.google.com/p/android/issues/detail?id=24931#c1
    private boolean pendingRequestEnableBt = false;

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_CONNECTED:
                    mDeviceConnector.sendAsciiMessage("ls");
                    ApiBluetoothDevice.setdevice(mDeviceConnector);
                    break;
                case MessageHandler.MSG_CONNECTING:
                    connected = false;
                    mStatusView.setText(formatStatusMessage(R.string.btstatus_connecting_to_fmt, msg.obj));
                    onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                case MessageHandler.MSG_CONNECTION_FAILED:
                case MessageHandler.MSG_CONNECTION_LOST:
                    connected = false;
                    mStatusView.setText(R.string.btstatus_not_connected);
                    onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_BYTES_WRITTEN:
                    String written = new String((byte[]) msg.obj);
                    mConversationArrayAdapter.add(">>> " + written);
                    Log.i(TAG, "written = '" + written + "'");
                    break;
                case MessageHandler.MSG_LINE_READ:
                    // Read message from server delimited by \n
                    if (paused) break;
                    String line = (String) msg.obj;
                    if (D) Log.d(TAG, line);
                    String modems = line;
                    Intent intent = new Intent(BluetoothActivity.this, ModemListActivity.class);
                    intent.putExtra("modems", modems);
                    BluetoothActivity.this.startActivity(intent);
                    mConversationArrayAdapter.add(line);
                    break;
                default:
                    Log.d(TAG, "Unkown message: " + msg.what + ", arg1= " + msg.arg1 + ", arg2= " + msg.arg2);
            }
        }

        private String formatStatusMessage(int formatResId, Object deviceName) {
            return getString(formatResId, (String) deviceName);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "++onCreate");
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            pendingRequestEnableBt = savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }

        setContentView(R.layout.bluetooth);

        mStatusView = findViewById(R.id.btstatus);

        mSendTextContainer = findViewById(R.id.send_text_container);

        mToolbarConnectButton = findViewById(R.id.toolbar_btn_connect);
        mToolbarConnectButton.setOnClickListener(v -> startDeviceListActivity());

        mConversationArrayAdapter = new ArrayAdapter<>(this, R.layout.message);
        mConversationView = findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        onBluetoothStateChanged();

        requestEnableBluetooth();
    }

    private void startDeviceListActivity() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }

    private void requestEnableBluetooth() {
        if (!isBluetoothAdapterEnabled() && !pendingRequestEnableBt) {
            pendingRequestEnableBt = true;
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    private boolean isBluetoothAdapterEnabled() {
        return getBluetoothAdapter().isEnabled();
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDeviceConnector != null)
            mDeviceConnector.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // babyphone demande la connexion bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    MessageHandler messageHandler = new MessageHandler(mHandler);
                    mDeviceConnector = DeviceListActivity.createDeviceConnector(data, messageHandler, getAssets());
                    mDeviceConnector.connect();
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Log.i(TAG, "BT not enabled");
                }
                break;
            default:
                Log.d(TAG, "Unknown request code: " + requestCode);
        }
    }

    private void onBluetoothStateChanged() {
        if (connected) {
            mToolbarConnectButton.setVisibility(View.GONE);
        } else {
            mToolbarConnectButton.setVisibility(View.VISIBLE);
        }
        paused = false;
    }

    public void signIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}