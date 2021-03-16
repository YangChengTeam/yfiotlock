package com.yc.yfiotlock.ble;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BleConnectReceiver extends BroadcastReceiver {
    public BleConnectReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            if (btDevice.getName().contains(LockBLEManager.DEVICE_NAME)) {
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    try {
                        BleReflectionUtils.createBond(btDevice.getClass(), btDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                Toast.makeText(context, "正在配对", Toast.LENGTH_LONG).show();
                if (btDevice.getName().contains(LockBLEManager.DEVICE_NAME)) {
                    try {
                        BleReflectionUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                        abortBroadcast();
                        BleReflectionUtils.setPin(btDevice.getClass(), btDevice, LockBLEManager.PIN_CODE);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
