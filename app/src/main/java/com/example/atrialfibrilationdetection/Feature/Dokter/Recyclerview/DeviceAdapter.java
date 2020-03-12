package com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atrialfibrilationdetection.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class DeviceAdapter extends ExpandableRecyclerViewAdapter<RumahSakitViewHolder, DeviceViewHolder> {

    public DeviceAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public RumahSakitViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rumah_sakit,parent,false);
        return new RumahSakitViewHolder(v);
    }

    @Override
    public DeviceViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_device,parent,false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(DeviceViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final DeviceModel deviceModel = (DeviceModel) group.getItems().get(childIndex);
        holder.bind(deviceModel);
    }

    @Override
    public void onBindGroupViewHolder(RumahSakitViewHolder holder, int flatPosition, ExpandableGroup group) {
        final RumahSakit rumahSakit = (RumahSakit) group;
        holder.bind(rumahSakit);
    }
}
