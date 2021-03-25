package com.yc.yfiotlock.libs.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.kk.securityhttp.utils.LogUtil;


public class ShakeSensor implements SensorEventListener {


    protected Context mContext = null;
    protected SensorManager mSensorManager = null;
    protected Sensor mSensor = null;
    protected OnShakeListener mShakeListener = null;

    protected final String TAG = this.getClass().getName();


    private int mSpeedShreshold = 4000;
    private static final int UPTATE_INTERVAL_TIME = 100;
    public static final int DEFAULT_SHAKE_SPEED = 2000;
    private boolean isStart = false;

    private float mLastX = 0.0f;
    private float mLastY = 0.0f;
    private float mLastZ = 0.0f;

    private long mLastUpdateTime;


    public ShakeSensor(Context context) {
        this(context, ShakeSensor.DEFAULT_SHAKE_SPEED);
    }

    public ShakeSensor(Context context, int speedShreshold) {
        mContext = context;
        mSpeedShreshold = speedShreshold;
    }


    public boolean register() {
        // 获得传感器管理器
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            // 获得重力传感器
            mSensor = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册传感器
        if (mSensor != null) {
            isStart = mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.d(TAG, "### 传感器初始化失败!");
        }
        return isStart;
    }

    public void unregister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            isStart = false;
            mShakeListener = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "### onAccuracyChanged,  accuracy = " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        // 两次检测的时间间隔
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - mLastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }
        // 现在的时间变成last时间
        mLastUpdateTime = currentUpdateTime;

        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // 获得x,y,z的变化值
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;

        // 将现在的坐标变成last坐标
        mLastX = x;
        mLastY = y;
        mLastZ = z;

        // 获取摇晃速度
        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval) * 10000;
        // 达到速度阀值，回调给开发者
        if (speed >= mSpeedShreshold && mShakeListener != null) {
            mShakeListener.onShakeComplete(event);
        }

    }


    public void setShakeListener(OnShakeListener listener) {
        this.mShakeListener = listener;
    }

    public interface OnShakeListener {
        void onShakeComplete(SensorEvent event);
    }
}
