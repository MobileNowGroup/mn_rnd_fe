package com.mobilenow.cyrcadia_itbra;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.mobilenow.cyrcadia_itbra.ble.BleAPI;
import com.mobilenow.cyrcadia_itbra.ble.base.DefaultObserver;
import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;
import com.mobilenow.cyrcadia_itbra.data.ble.DeviceStatusModel;
import com.mobilenow.cyrcadia_itbra.databinding.ActivityMainBinding;
import com.mobilenow.cyrcadia_itbra.event.BleStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    BlueAdapter mAdapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Utils.init(this);
        ITBraBleManager.getInstance().init(this);
        if (!BleAPI.isITBraConnect()) {
            BleAPI.connectITBra();
        }
        EventBus.getDefault().register(this);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mainBinding.tvBlue.setText("支持ble  Android版本" + Build.VERSION.RELEASE);
        } else {
            mainBinding.tvBlue.setText("不支持ble  Android版本" + Build.VERSION.RELEASE);
        }
        mAdapter = new BlueAdapter(this, null);
        mainBinding.rvBlue.setLayoutManager(new MyGirdLayoutManager(this, 2));
        mainBinding.rvBlue.setAdapter(mAdapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mAdapter.notifyDataSetChanged();
            }
        };
        new Thread(() -> {
            int i = 0;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                Log.d("dddd", "i = " + i);
                List<BlueModel> datalist = mAdapter.getmDataList();
                if (datalist != null) {
                    for (int j = 0; j < datalist.size(); j++) {
//                            if (!datalist.get(j).getDevice().getAddress().equalsIgnoreCase
//                                    ("F2:E7:0B:0F:B7:54"))
                        BlueModel model = datalist.get(j);
                        BleAPI.getDeviceStatus(model.getDevice().getAddress()).subscribe(new DefaultObserver<DeviceStatusModel>() {
                            @Override
                            public void onNext(DeviceStatusModel deviceStatusModel) {
                                String status = "";
                                if (deviceStatusModel != null) {
                                    if (deviceStatusModel.getStatusByte() == DeviceStatusModel.StatusByte.MEASUREMENTCYCLECOMPLETED) {
                                        status = "MEASUREMENTCYCLECOMPLETED";
                                    } else if (deviceStatusModel.getStatusByte() == DeviceStatusModel.StatusByte.MEASUREMENTCYCLEINPROGRESS) {
                                        status = "MEASUREMENTCYCLEINPROGRESS";
                                    } else if (deviceStatusModel.getStatusByte() == DeviceStatusModel.StatusByte.MEASUREMENTCYCLEABORTED) {
                                        status = "MEASUREMENTCYCLEABORTED";
                                    } else if (deviceStatusModel.getStatusByte() == DeviceStatusModel.StatusByte.WARMUP) {
                                        status = "WARMUP";
                                    }
                                    model.setCount(model.getCount() + 1);
                                    Log.d("dddd", model.getDevice().getAddress() + ", finalJ = " + model.getCount());

                                    model.setDeviceStatus(model.getCount() + ":" + status);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                            break;
                    }


                }
                i++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1000);
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceStatusEvent(BleStatusChangeEvent messageEvent) {
        mAdapter.setmDataList(ITBraBleManager.getInstance().getDevices());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
