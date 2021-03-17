package com.yc.yfiotlock.demo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleMtuChangedCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEPackage;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {
    BleDevice bleDevice;
    ResultAdapter mResultAdapter;

    private static ServiceActivity serviceActivity;

    public static ServiceActivity getInstance() {
        return serviceActivity;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_service);
        serviceActivity = this;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bleDevice = getIntent().getParcelableExtra("bleDevice");
        BleManager.getInstance().setMtu(bleDevice, 512, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                Toast.makeText(ServiceActivity.this, "设置mtu失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMtuChanged(int mtu) {
                // 设置MTU成功，并获得当前设备传输支持的MTU值
                LockBLEPackage.setMtu(mtu);
                Toast.makeText(ServiceActivity.this, "设置mtu成功" + mtu, Toast.LENGTH_SHORT).show();
            }
        });

        TextView blenameTv = findViewById(R.id.tv_blename);
        blenameTv.setText("蓝牙名称:"+bleDevice.getName());

        mResultAdapter = new ResultAdapter(this);
        ListView listView_device = findViewById(R.id.list_service);
        listView_device.setAdapter(mResultAdapter);
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattService service = mResultAdapter.getItem(position);
                Intent intent = new Intent(ServiceActivity.this, CharacteristicActivity.class);
                intent.putExtra("service", service);

                startActivity(intent);
            }
        });
        showData();
    }

    private void showData() {
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        if(gatt == null) return;

        mResultAdapter.clear();
        for (BluetoothGattService service : gatt.getServices()) {
            mResultAdapter.addResult(service);
        }
        mResultAdapter.notifyDataSetChanged();
    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private List<BluetoothGattService> bluetoothGattServices;

        ResultAdapter(Context context) {
            this.context = context;
            bluetoothGattServices = new ArrayList<>();
        }

        void addResult(BluetoothGattService service) {
            bluetoothGattServices.add(service);
        }

        void clear() {
            bluetoothGattServices.clear();
        }

        @Override
        public int getCount() {
            return bluetoothGattServices.size();
        }

        @Override
        public BluetoothGattService getItem(int position) {
            if (position > bluetoothGattServices.size())
                return null;
            return bluetoothGattServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.demo_adapter_service, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
                holder.txt_uuid = (TextView) convertView.findViewById(R.id.txt_uuid);
                holder.txt_type = (TextView) convertView.findViewById(R.id.txt_type);
            }

            BluetoothGattService service = bluetoothGattServices.get(position);
            String uuid = service.getUuid().toString();

            holder.txt_title.setText(String.valueOf(getString(R.string.service) + "(" + position + ")"));
            holder.txt_uuid.setText(uuid);
            holder.txt_type.setText(getString(R.string.type));
            return convertView;
        }

        class ViewHolder {
            TextView txt_title;
            TextView txt_uuid;
            TextView txt_type;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
