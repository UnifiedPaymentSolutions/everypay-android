package com.everypay.sdk.model;


public class CardNumberTest extends CardTestBase {

    public void testValidateNumberEmpty() throws Throwable {
        assertThrows(c(null, null, null, null, null), "validateNumber", "number_missing", true);
        assertThrows(c(null, "", null, null, null), "validateNumber", "number_missing", true);
    }

    public void testValidateShortLongUsual() throws Throwable {
        // Visa
        assertThrows(c(null, "411111111111111", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "41111111111111111", null, null, null), "validateNumber", "number_long", false);
        // Mastercard
        assertThrows(c(null, "555555555555444", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "55555555555544444", null, null, null), "validateNumber", "number_long", false);
        // Discover
        assertThrows(c(null, "601111111111117", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "60111111111111117", null, null, null), "validateNumber", "number_long", false);
    }

    public void testValidateShortLongAmex() throws Throwable {
        assertThrows(c(null, "37144963539831", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "3714496353984311", null, null, null), "validateNumber", "number_long", false);
        assertThrows(c(null, "37828224310005", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "3782822463100051", null, null, null), "validateNumber", "number_long", false);
    }

    public void testValidateShortLongDiners() throws Throwable {
        assertThrows(c(null, "3056930902594", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "305693090259041", null, null, null), "validateNumber", "number_long", false);
        assertThrows(c(null, "3852000002323", null, null, null), "validateNumber", "number_short", true);
        assertThrows(c(null, "385200000232378", null, null, null), "validateNumber", "number_long", false);
    }

    public void testValidateChecksumUsual() throws Throwable {
        // Visa
        c(null, "4111111111111111", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "4111111111111112", null, null, null), "validateNumber", "number_invalid", false);
        c(null, "4012888888881881", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "4012888888881882", null, null, null), "validateNumber", "number_invalid", false);
        // Mastercard
        c(null, "5555555555554444", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "5555555555554449", null, null, null), "validateNumber", "number_invalid", false);
        c(null, "5105105105105100", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "5105105105105105", null, null, null), "validateNumber", "number_invalid", false);
        // JCB
        c(null, "3530111333300000", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "3530111333300001", null, null, null), "validateNumber", "number_invalid", false);
        c(null, "3566002020360505", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "3566002020360507", null, null, null), "validateNumber", "number_invalid", false);
    }

    public void testValidateChecksumAmex() throws Throwable {
        c(null, "378282246310005", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "378282246310008", null, null, null), "validateNumber", "number_invalid", false);
        c(null, "378734493671000", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "378734493671001", null, null, null), "validateNumber", "number_invalid", false);
    }

    public void testValidateChecksumDiners() throws Throwable {
        c(null, "30569309025904", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "30569309025903", null, null, null), "validateNumber", "number_invalid", false);
        c(null, "38520000023237", null, null, null).validateNumber(mockContext);
        assertThrows(c(null, "38520000023230", null, null, null), "validateNumber", "number_invalid", false);
    }

}
