package com.everypay.sdk.model;


public class CardCvcTest extends CardTestBase {

    public void testValidateMissing() throws Throwable {
        assertThrows(c(null, null, null, null, null), "validateCVC", "cvc_missing", true);
        assertThrows(c(null, null, null, null, ""), "validateCVC", "cvc_missing", true);
    }

    public void testValidateShortLongUsual() throws Throwable {
        assertThrows(c(null, null, null, null, "1"), "validateCVC", "cvc_invalid", true);
        assertThrows(c(null, null, null, null, "12"), "validateCVC", "cvc_invalid", true);
        assertThrows(c(null, null, null, null, "1234"), "validateCVC", "cvc_invalid", false);

        assertThrows(c(null, "4111111111111111", null, null, "1"), "validateCVC", "cvc_invalid", true);
        assertThrows(c(null, "4111111111111111", null, null, "12"), "validateCVC", "cvc_invalid", true);
        assertThrows(c(null, "4111111111111111", null, null, "1234"), "validateCVC", "cvc_invalid", false);
    }

    public void testValidateShortLongAmex() throws Throwable {
        assertThrows(c(null, "378282246310005", null, null, "1"), "validateCVC", "cvc_amex", true);
        assertThrows(c(null, "378282246310005", null, null, "12"), "validateCVC", "cvc_amex", true);
        assertThrows(c(null, "378282246310005", null, null, "123"), "validateCVC", "cvc_amex", true);
        assertThrows(c(null, "378282246310005", null, null, "12345"), "validateCVC", "cvc_amex", false);
    }

    public void testValidateOk() throws CardError {
        c(null, null, null, null, "123").validateCVC(mockContext);
        c(null, "4111111111111111", null, null, "123").validateCVC(mockContext);
        c(null, "378282246310005", null, null, "1234").validateCVC(mockContext);
    }

    public void testValidateExtraCrap() throws CardError {
        c(null, null, null, null, " -> 123").validateCVC(mockContext);
        c(null, "4111111111111111", null, null, "1 2 3 x y z").validateCVC(mockContext);
        c(null, "378282246310005", null, null, "1234 !!").validateCVC(mockContext);
    }

    public void testNoDigits() throws Throwable {
        assertThrows(c(null, null, null, null, "yo"), "validateCVC", "cvc_missing", true);
        assertThrows(c(null, null, null, null, ".#'@"), "validateCVC", "cvc_missing", true);
        assertThrows(c(null, null, null, null, "hello"), "validateCVC", "cvc_missing", true);
    }

}
