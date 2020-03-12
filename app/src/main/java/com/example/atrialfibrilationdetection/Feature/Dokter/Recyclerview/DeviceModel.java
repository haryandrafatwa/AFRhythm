package com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceModel implements Parcelable {

    public final String device_id;
    public final String pasien_name;

    public DeviceModel (String device_id, String pasien_name){
        this.device_id = device_id;
        this.pasien_name = pasien_name;
    }

    protected DeviceModel(Parcel in){
        pasien_name = in.readString();
        device_id = in.readString();
    }

    public static final  Creator<DeviceModel> CREATOR = new Creator<DeviceModel>() {
        @Override
        public DeviceModel createFromParcel(Parcel source) {
            return new DeviceModel(source);
        }

        @Override
        public DeviceModel[] newArray(int size) {
            return new DeviceModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(device_id);
        dest.writeString(pasien_name);
    }
}
