package com.everypay.sdk.api.responsedata;


import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;

import java.util.ArrayList;

public class MerchantPaymentResponseData extends ErrorHelper {
    private static final long serialVersionUID = 1432107208287516008L;

    public String status;

    public MerchantPaymentResponseData(ArrayList<EveryPayError> errors) {
        super(errors);
    }
}
