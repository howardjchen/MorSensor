/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.org.cic.control_activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tw.org.cic.morsenser_example.R;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private static final String TAG = "DeviceScanActivity";
    public static Activity mDeviceScanActivity;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothLeScanner mLEScanner;
    LocationManager status;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public static String mDeviceAddress = "123";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);

        mHandler = new Handler();

        mDeviceScanActivity=this;

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        Log.e("Scan","mScanning:"+mScanning);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                Log.e("Scan", "scanLeDevice:" + true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                Log.e("Scan", "scanLeDevice:" + false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else if(!checking) {
            checkLocation();
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    static boolean checking = false;
    @Override
    protected void onDestroy() {
        checking = false;
        super.onDestroy();
    }

    //取得App定位權限 及 開啟系統定位服務
    private void checkLocation(){
        checking = true;
        Log.e(TAG, "Ver.SDK:"+ Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23){ //Android 6.0↑
            Log.e(TAG, "Ver.SDK:"+Build.VERSION.SDK_INT + " Ver.SDK >= 23，開始要求'App定位權限'及'系統定位權限'！");
            status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));

            settingBLEScan();

            //判断是否有权限
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
                //判断是否需要 向用户解释，为什么要申请该权限
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS);
                Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " 要求取得'App定位權限'。");
            }else {//已取得App定位權限
                //開啟系統定位服務
                if (!status.isProviderEnabled(LocationManager.GPS_PROVIDER) || !status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    LocationAlertDialog().show();
                    Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " 要求取得'系統定位權限'。");
                }else {
                    scanLeDevice(true);
                    checking = false;
                    Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " '系統定位權限'已開啟，開始掃描。");
                }
            }
        }else {
            Log.e(TAG, "Ver.SDK:"+Build.VERSION.SDK_INT+" Ver.SDK < 23，開始掃描！");
            checking = false;
            scanLeDevice(true);
        }
    }

    private void settingBLEScan(){
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
    }

    //App權限回傳
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0)
        {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " permission:"+permission + " grantResult:"+grantResult);
                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) { //允許
                        if (!status.isProviderEnabled(LocationManager.GPS_PROVIDER) || !status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            LocationAlertDialog().show();
                            Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " 允許開啟'App定位權限'，並要求開啟'系統定位權限'。");
                        }else{
                            Log.e(TAG, "Ver.SDK:" + Build.VERSION.SDK_INT + " 允許開啟'App定位權限'，'系統定位權限'已開啟，開始掃描。");
                            checking = false;
                            scanLeDevice(true);
                        }
                    }else { //不允許
                        Log.e(TAG, "Ver.SDK:"+Build.VERSION.SDK_INT+" 不允許開啟'App定位權限'，關閉App。");
//                        ActivityForExtend.mExit();
                    }
                }
            }
        }
    }

    //系統定位服務
    private AlertDialog LocationAlertDialog(){
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceScanActivity.this);
//        //設定Dialog的標題
        builder.setMessage("請開啟'定位服務'來搜尋 BLE device.");
        //設定Positive按鈕資料
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //開啟設定頁面
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                checking = false;
//                ActivityForExtend.mExit();
            }
        });
        return builder.create();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;

        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        if (mScanning) {
            scanLeDevice(false);
        }
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 23)
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    else
                        mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 23)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            else
                mLEScanner.startScan(filters, settings, mScanCallback);
            mScanning = true;
        }else {
            if (Build.VERSION.SDK_INT < 23)
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            else
                mLEScanner.stopScan(mScanCallback);
            mScanning = false;
        }
        invalidateOptionsMenu();
    }

    //     Device scan callback.
    private ScanCallback mScanCallback =
            new ScanCallback() {
                ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

                public void onScanResult(int callbackType, final ScanResult result) {
                    Log.i("callbackType", String.valueOf(callbackType));
                    Log.i("result", result.toString());

                    Log.e(TAG, "rssi:" + result.getRssi() + " device:" + result.getDevice());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mLeDevices.contains(result.getDevice())) {
                                mLeDevices.add(result.getDevice());
                                mLeDeviceListAdapter.addDevice(result.getDevice());
                                mLeDeviceListAdapter.notifyDataSetChanged();
//                                mNewDevicesArrayAdapter.add(result.getDevice().getName() + "\n" + result.getDevice().getAddress());
//                                mNewDevicesArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    for (ScanResult sr : results) {
                        Log.i("ScanResult - Results", sr.toString());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.e("Scan Failed", "Error Code: " + errorCode);
                }
            };


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mLeDevices.contains(device)) {
                                mLeDevices.add(device);
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                                int a = rssi;
                                Log.e("List", "Address:" + device.getAddress() + " RSSI:" + a);
                            }
                        }
                    });
                }
            };

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();

            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}