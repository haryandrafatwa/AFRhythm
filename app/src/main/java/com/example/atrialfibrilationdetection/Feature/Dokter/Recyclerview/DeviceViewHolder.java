package com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview;

import android.view.View;
import android.widget.TextView;

import com.example.atrialfibrilationdetection.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class DeviceViewHolder extends ChildViewHolder {

    private TextView tv_device_id,tv_pasien_name;

    public DeviceViewHolder(View itemView){
        super(itemView);
        tv_device_id = itemView.findViewById(R.id.tv_device_id);
        tv_pasien_name = itemView.findViewById(R.id.tv_pasien_name);
    }

    public void bind(DeviceModel deviceModel){
        tv_device_id.setText(deviceModel.device_id);
        tv_pasien_name.setText(deviceModel.pasien_name);
    }

}
