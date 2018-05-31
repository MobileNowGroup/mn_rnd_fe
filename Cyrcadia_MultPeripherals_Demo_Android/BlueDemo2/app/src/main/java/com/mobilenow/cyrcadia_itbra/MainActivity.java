package com.mobilenow.cyrcadia_itbra;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.blankj.utilcode.util.Utils;
import com.mobilenow.cyrcadia_itbra.ble.BleAPI;
import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;
import com.mobilenow.cyrcadia_itbra.databinding.ActivityMainBinding;
import com.mobilenow.cyrcadia_itbra.event.BleStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    BlueAdapter mAdapter;

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
        mAdapter = new BlueAdapter(this, null);
        mainBinding.rvBlue.setLayoutManager(new MyGirdLayoutManager(this, 2));
        mainBinding.rvBlue.setAdapter(mAdapter);
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
