package com.everypay.sdk.api.merchant;

import java.util.Map;

public class MerchantParamsRequestData {
    Map<String, Object> deviceInfo;

    public MerchantParamsRequestData(Map<String, Object> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
