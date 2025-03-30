package com.sprint.lock.app;
 
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
 
import android.Manifest;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.TextView;
 
import com.herohan.uvcapp.CameraHelper;
import com.herohan.uvcapp.ICameraHelper;
import com.hjq.permissions.XXPermissions;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.UVCParam;
import com.serenegiant.widget.AspectRatioSurfaceView;
 
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
 
public class UvGActivity extends AppCompatActivity {
 
    private static final boolean DEBUG = true;
    private static final String TAG = UvGActivity.class.getSimpleName();
 
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
 
    private TextView mCameraNameLeft;
    private UsbDevice mUsbDeviceLeft;
    private ICameraHelper mCameraHelperLeft;
    private AspectRatioSurfaceView svCameraViewLeft;
 
    private ConcurrentLinkedQueue<UsbDevice> mReadyUsbDeviceList = new ConcurrentLinkedQueue<>();
    private ConditionVariable mReadyDeviceConditionVariable = new ConditionVariable();
 
    private final Object mSync = new Object();
 
    private HandlerThread mHandlerThread;
    private Handler mAsyncHandler;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_uvc_camera);
        //setTitle(R.string.entry_multi_camera_new);
        if (DEBUG) Log.d(TAG, "huahua--onCreate:");
        mCameraNameLeft = findViewById(R.id.textViewLeft);
        List<String> needPermissions = new ArrayList<>();
        needPermissions.add(Manifest.permission.CAMERA);
        XXPermissions.with(this)
                .permission(needPermissions)
                .request((permissions, all) -> {
                    if (!all) {
                        return;
                    }
                    initViews();
                    mHandlerThread = new HandlerThread(TAG);
                    mHandlerThread.start();
                    mAsyncHandler = new Handler(mHandlerThread.getLooper());
                    initCameraHelper();
 
                });
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCameraHelper();
        mHandlerThread.quitSafely();
        mAsyncHandler.removeCallbacksAndMessages(null);
    }
 
    private void initViews() {
        setCameraViewLeft();
    }
 
    private void setCameraViewLeft() {
        svCameraViewLeft = findViewById(R.id.svCameraViewLeft);
        //svCameraViewLeft.setAspectRatio(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        svCameraViewLeft.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (mCameraHelperLeft != null) {
                    mCameraHelperLeft.addSurface(holder.getSurface(), false);
                }
            }
 
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
 
            }
 
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mCameraHelperLeft != null) {
                    mCameraHelperLeft.removeSurface(holder.getSurface());
                }
            }
        });
    }

 
    @Override
    protected void onStart() {
        if (DEBUG) Log.d(TAG, "huahua--onStart:");
        super.onStart();
 
    }
 
    @Override
    protected void onStop() {
        if (DEBUG) Log.d(TAG, "onStop:");
        super.onStop();
    }

    public void initCameraHelper() {
        if (DEBUG) Log.d(TAG, "initCameraHelper:");
        if (mCameraHelperLeft == null) {
            mCameraHelperLeft = new CameraHelper();
            mCameraHelperLeft.setStateCallback(mStateListenerLeft);
        }
    }
 
    private void clearCameraHelper() {
        if (DEBUG) Log.d(TAG, "clearCameraHelper:");
        if (mCameraHelperLeft != null) {
            mCameraHelperLeft.release();
            mCameraHelperLeft = null;
        }
    }
 
    private void selectDeviceLeft(final UsbDevice device) {
        if (DEBUG) Log.v(TAG, "selectDeviceLeft:device=" + device.getDeviceName());
        mUsbDeviceLeft = device;
 
        mAsyncHandler.post(() -> {
            waitCanSelectDevice(device);
 
            if (mCameraHelperLeft != null) {
                mCameraHelperLeft.selectDevice(device);
            }
        });
    }

 
    /**
     * wait for only one camera need request permission
     *
     * @param device
     */
    private void waitCanSelectDevice(UsbDevice device) {
        mReadyUsbDeviceList.add(device);
        while (mReadyUsbDeviceList.size() > 1) {
            mReadyDeviceConditionVariable.block();
            mReadyDeviceConditionVariable.close();
        }
    }
 
    /**
     * remove ready camera that wait  for select
     *
     * @param device
     */
    private void removeSelectedDevice(UsbDevice device) {
        mReadyUsbDeviceList.remove(device);
        mReadyDeviceConditionVariable.open();
    }
 
    private final ICameraHelper.StateCallback mStateListenerLeft = new ICameraHelper.StateCallback() {
        private final String LOG_PREFIX = "ListenerLeft#";
 
        @Override
        public void onAttach(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onAttach:");
            synchronized (mSync) {
                if (mUsbDeviceLeft == null) {
                    selectDeviceLeft(device);
                    mCameraNameLeft.setText("左相机("+device.getDeviceName()+")");
                }
            }
        }
 
        @Override
        public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onDeviceOpen:");
            if (mCameraHelperLeft != null && device.equals(mUsbDeviceLeft)) {
                UVCParam param = new UVCParam();
                param.setQuirks(UVCCamera.UVC_QUIRK_FIX_BANDWIDTH);
                mCameraHelperLeft.openCamera(param);
            }
 
            removeSelectedDevice(device);
        }
 
        @Override
        public void onCameraOpen(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onCameraOpen:");
            if (mCameraHelperLeft != null && device.equals(mUsbDeviceLeft)) {
                mCameraHelperLeft.startPreview();
 
                Size size = mCameraHelperLeft.getPreviewSize();
                if (size != null) {
                    int width = size.width;
                    int height = size.height;
                    //auto aspect ratio
                    svCameraViewLeft.setAspectRatio(width, height);
                }
 
                mCameraHelperLeft.addSurface(svCameraViewLeft.getHolder().getSurface(), false);
            }
        }
 
        @Override
        public void onCameraClose(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onCameraClose:");
            if (device.equals(mUsbDeviceLeft)) {
                if (mCameraHelperLeft != null) {
                    mCameraHelperLeft.removeSurface(svCameraViewLeft.getHolder().getSurface());
                }
            }
        }
 
        @Override
        public void onDeviceClose(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onDeviceClose:");
        }
 
        @Override
        public void onDetach(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onDetach:");
            if (device.equals(mUsbDeviceLeft)) {
                mUsbDeviceLeft = null;
            }
 
            removeSelectedDevice(device);
        }
 
        @Override
        public void onCancel(UsbDevice device) {
            if (DEBUG) Log.v(TAG, LOG_PREFIX + "onCancel:");
            if (device.equals(mUsbDeviceLeft)) {
                mUsbDeviceLeft = null;
            }
 
            removeSelectedDevice(device);
        }
    };
}