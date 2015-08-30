package com.everypay.sdk.model;


public class CardYearTest extends CardTestBase {

    public void testValidateYearEmpty() throws Throwable {
        assertThrows(c(null, null, null, null, null), "validateExpYear", "year_missing", true);
        assertThrows(c(null, null, null, "", null), "validateExpYear", "year_missing", true);
    }

    public void testValidateYearShort() throws Throwable {
        assertThrows(c(null, null, null, "1", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "12", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "15", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "95", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "995", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "102", null), "validateExpYear", "year_short", true);
        assertThrows(c(null, null, null, "095", null), "validateExpYear", "year_short", true);
    }

    public void testValidateYearUsual() throws CardError {
        c(null, null, null, "1995", null).validateExpYear(mockContext);
        c(null, null, null, "2005", null).validateExpYear(mockContext);
        c(null, null, null, "20 30", null).validateExpYear(mockContext);
    }

    public void testValidateYearExtraCrap() throws CardError {
        c(null, null, null, "wtd 1995 boo", null).validateExpYear(mockContext);
        c(null, null, null, "2005 hello", null).validateExpYear(mockContext);
        c(null, null, null, "...000 2030...", null).validateExpYear(mockContext);
    }

    public void testNoDigits() throws Throwable {
        assertThrows(c(null, null, null, "testing", null), "validateExpYear", "year_missing", true);
        assertThrows(c(null, null, null, "next year", null), "validateExpYear", "year_missing", true);
        assertThrows(c(null, null, null, ".,@:", null), "validateExpYear", "year_missing", true);
    }

    public void testCentury() throws Throwable {
        assertThrows(c(null, null, null, "1899", null), "validateExpYear", "year_invalid", false);
        assertThrows(c(null, null, null, "2101", null), "validateExpYear", "year_invalid", false);
        assertThrows(c(null, null, null, "19995", null), "validateExpYear", "year_invalid", false);
    }

}
