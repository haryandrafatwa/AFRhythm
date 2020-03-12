package com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class RumahSakit extends ExpandableGroup<DeviceModel>{

    public RumahSakit (String title, List<DeviceModel> items){
        super(title,items);
    }

}
