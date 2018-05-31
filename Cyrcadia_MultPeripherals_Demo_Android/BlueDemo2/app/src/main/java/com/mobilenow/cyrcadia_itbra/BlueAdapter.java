package com.mobilenow.cyrcadia_itbra;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilenow.cyrcadia_itbra.ble.base.BlueStatus;

import java.util.List;

public class BlueAdapter extends RecyclerView.Adapter<BlueAdapter.ViewHolder> {
    private List<BlueModel> mDataList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_mac;
        TextView tv_status;

        public ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mac = view.findViewById(R.id.tv_mac);
            tv_status = view.findViewById(R.id.tv_status);
        }
    }

    BlueAdapter(Context context, List<BlueModel> dataList) {
        this.context = context;
        this.mDataList = dataList;
    }

    public void setmDataList(List<BlueModel> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blue, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlueModel mode = mDataList.get(position);
        if (position == 0) {
            Log.d("BleManager", "onBindViewHolder");
        }
        if (mode != null) {
            holder.tv_mac.setText(mode.getDevice().getAddress());
            holder.tv_name.setText(mode.getDevice().getName());
            if (mode.getmConnectionState() == BlueStatus.STATE_INIT) {
                holder.tv_status.setText("准备连接");
            } else if (mode.getmConnectionState() == BlueStatus.STATE_CONNECTING) {
                holder.tv_status.setText("连接中..");
            } else if (mode.getmConnectionState() == BlueStatus.STATE_CONNECTED) {
                holder.tv_status.setText("已连接");
            } else if (mode.getmConnectionState() == BlueStatus.STATE_DISCONNECTING) {
                holder.tv_status.setText("连接断开");
            }
        } else if (mode.getmConnectionState() == BlueStatus.STATE_CLOSE) {
            holder.tv_status.setText("连接关闭");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }
}

