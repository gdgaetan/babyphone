package com.example.gaetan.babyphoneapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.gaetan.babyphoneapp.R;
import com.example.gaetan.babyphoneapp.bluetooth.BluetoothDeviceConnector;
import com.example.gaetan.babyphoneapp.bluetooth.MessageHandler;

import java.util.HashSet;
import java.util.Set;



/**
 * This Activity appears as a dialog. It lists already paired devices,
 * and it can scan for devices nearby. When the user selects a device,
 * its MAC address is returned to the caller as the result of this activity.
 */
public class DeviceListActivity extends Activity {

    private static final String TAG = "DeviceListActivity";

    public static final String EXTRA_MOCK_DEVICES_ENABLED = "MOCK_DEVICES_ENABLED";

    public enum ConnectorType {
        BLUETOOTH
    }

    public enum Message {
        DEVICE_CONNECTOR_TYPE,
        BLUETOOTH_ADDRESS,
    }

    private abstract static class DeviceListEntry {
        @Override
        public String toString() {
            return String.format("%s%n%s", getFirstLine(), getSecondLine());
        }

        abstract String getFirstLine();

        abstract String getSecondLine();
    }

    private static class BluetoothDeviceEntry extends DeviceListEntry {
        private final String name;
        private final String address;

        BluetoothDeviceEntry(String name, String address) {
            this.name = name;
            this.address = address;
        }

        @Override
        protected String getFirstLine() {
            return name;
        }

        @Override
        protected String getSecondLine() {
            return address;
        }
    }

    private final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<BluetoothDeviceEntry> mNewDevicesArrayAdapter;
    private final Set<String> mNewDevicesSet = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set default result to CANCELED, in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // babyphone Liste des devices pairées
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            ArrayAdapter<BluetoothDeviceEntry> pairedDevicesAdapter = new ArrayAdapter<BluetoothDeviceEntry>(this, R.layout.device_name);
            ListView pairedListView = findViewById(R.id.paired_devices);
            pairedListView.setAdapter(pairedDevicesAdapter);
            pairedListView.setOnItemClickListener(new BluetoothDeviceClickListener(pairedDevicesAdapter));

            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(new BluetoothDeviceEntry(device.getName(), device.getAddress()));
            }

            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
        }

        mNewDevicesArrayAdapter = new ArrayAdapter<BluetoothDeviceEntry>(this, R.layout.device_name);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(new BluetoothDeviceClickListener(mNewDevicesArrayAdapter));

        IntentFilter bluetoothDeviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, bluetoothDeviceFoundFilter);

        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, discoveryFinishedFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBtAdapter.cancelDiscovery();

        this.unregisterReceiver(mReceiver);
    }

    private abstract class AbstractItemClickListener<T> implements OnItemClickListener {
        final ArrayAdapter<T> adapter;

        private AbstractItemClickListener(ArrayAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            T item = adapter.getItem(position);
            if (item != null) {
                Intent intent = new Intent();
                putExtras(intent, item);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }

        abstract void putExtras(Intent intent, T item);
    }

    private class BluetoothDeviceClickListener extends AbstractItemClickListener<BluetoothDeviceEntry> {
        private BluetoothDeviceClickListener(ArrayAdapter<BluetoothDeviceEntry> adapter) {
            super(adapter);
        }

        @Override
        void putExtras(Intent intent, BluetoothDeviceEntry item) {
            intent.putExtra(Message.DEVICE_CONNECTOR_TYPE.toString(), ConnectorType.BLUETOOTH);
            intent.putExtra(Message.BLUETOOTH_ADDRESS.toString(), item.address);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                String parcelableExtraName = BluetoothDevice.EXTRA_DEVICE;
                BluetoothDevice device = intent.getParcelableExtra(parcelableExtraName);
                if (device != null) {
                    String address = device.getAddress();
                    if (!mNewDevicesSet.contains(address)) {
                        mNewDevicesSet.add(address);
                        mNewDevicesArrayAdapter.add(new BluetoothDeviceEntry(device.getName(), address));
                    }
                } else {
                    Log.e(TAG, "Could not get parcelable extra: " + parcelableExtraName);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

    public static BluetoothDeviceConnector createDeviceConnector(Intent data, MessageHandler messageHandler, AssetManager assetManager) {
        String addressMsgId = Message.BLUETOOTH_ADDRESS.toString();
        String address = data.getStringExtra(addressMsgId);

        return new BluetoothDeviceConnector(messageHandler, address);
    }
}