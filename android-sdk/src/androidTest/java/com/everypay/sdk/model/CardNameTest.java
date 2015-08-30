package com.everypay.sdk.model;


public class CardNameTest extends CardTestBase {

    public void testValidateNameEmpty() throws Throwable {
        assertThrows(c(null, null, null, null, null), "validateName", "name_missing", true);
        assertThrows(c("", null, null, null, null), "validateName", "name_missing", true);
    }

    public void testValidateNameProvided() throws CardError {
        c("John Smith", null, null, null, null).validateName(mockContext);
    }

}
