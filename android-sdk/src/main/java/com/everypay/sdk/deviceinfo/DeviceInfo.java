package com.everypay.sdk.deviceinfo;


import com.google.gson.annotations.SerializedName;

public class DeviceInfo {

    @SerializedName("android_id")
    InfoField androidId;
    @SerializedName("app_install_token")
    InfoField appInstallToken;
    @SerializedName("hardware")
    InfoField hardware;
    @SerializedName("net_macs")
    InfoField netMacs;
    @SerializedName("os")
    InfoField os;
    @SerializedName("wifi_mac")
    InfoField wifiMac;
    @SerializedName("gps")
    InfoField gps;
}
