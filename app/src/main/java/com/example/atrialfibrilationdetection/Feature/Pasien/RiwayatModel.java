package com.example.atrialfibrilationdetection.Feature.Pasien;

public class RiwayatModel {
    private String deviceID,date;

    public RiwayatModel(String trashbagId, String date) {
        this.deviceID = trashbagId;
        this.date = date;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String trashbagId) {
        this.deviceID = trashbagId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
