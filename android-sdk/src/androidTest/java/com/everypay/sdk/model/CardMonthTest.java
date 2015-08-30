package com.everypay.sdk.model;


public class CardMonthTest extends CardTestBase {

    public void testValidateMonthEmpty() throws Throwable {
        assertThrows(c(null, null, null, null, null), "validateExpMonth", "month_missing", true);
        assertThrows(c(null, null, "", null, null), "validateExpMonth", "month_missing", true);
    }

    public void testValidateMonthUsual() throws CardError {
        c(null, null, "1", null, null).validateExpMonth(mockContext);
        c(null, null, "01", null, null).validateExpMonth(mockContext);
        c(null, null, "2", null, null).validateExpMonth(mockContext);
        c(null, null, "02", null, null).validateExpMonth(mockContext);
        c(null, null, "12", null, null).validateExpMonth(mockContext);
    }

    public void testValidateMonthExtraCrap() throws CardError {
        c(null, null, "...1 hello", null, null).validateExpMonth(mockContext);
        c(null, null, "foo 01 asd", null, null).validateExpMonth(mockContext);
        c(null, null, "feb 2 february", null, null).validateExpMonth(mockContext);
        c(null, null, "02 - february", null, null).validateExpMonth(mockContext);
        c(null, null, "dec 12 dec", null, null).validateExpMonth(mockContext);
    }

    public void testBadNumber() throws Throwable {
        assertThrows(c(null, null, "0", null, null), "validateExpMonth", "month_invalid", true);
        assertThrows(c(null, null, "0000", null, null), "validateExpMonth", "month_invalid", true);
        assertThrows(c(null, null, "13 friday", null, null), "validateExpMonth", "month_invalid", false);
        assertThrows(c(null, null, "1000", null, null), "validateExpMonth", "month_invalid", false);
        assertThrows(c(null, null, "hello 15 world", null, null), "validateExpMonth", "month_invalid", false);
    }

    public void testNoDigits() throws Throwable {
        assertThrows(c(null, null, "testing", null, null), "validateExpMonth", "month_missing", true);
        assertThrows(c(null, null, "september", null, null), "validateExpMonth", "month_missing", true);
        assertThrows(c(null, null, "foo", null, null), "validateExpMonth", "month_missing", true);
        assertThrows(c(null, null, ".-;'c'a", null, null), "validateExpMonth", "month_missing", true);
    }

}
