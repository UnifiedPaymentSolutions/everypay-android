package com.everypay.sdk.model;


import java.util.Calendar;

public class CardExpiryTest extends CardTestBase {

    public void testValidateExpired() throws Throwable {
        assertThrows(c(null, null, "01", "1995", null), "validateExpiryDate", "expired", false);
        assertThrows(c(null, null, "1", "2014", null), "validateExpiryDate", "expired", false);
        assertThrows(c(null, null, "07", "2015", null), "validateExpiryDate", "expired", false);
    }

    public void testValidateActive() throws Throwable {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        c(null, null, "" + month, "" + year, null).validateExpiryDate(mockContext);
        c(null, null, "" + month, "" + (year + 1), null).validateExpiryDate(mockContext);
    }

}
