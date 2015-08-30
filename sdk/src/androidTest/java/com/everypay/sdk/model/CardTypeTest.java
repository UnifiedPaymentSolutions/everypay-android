package com.everypay.sdk.model;


/**
 * Using https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
 */
public class CardTypeTest extends CardTestBase {

    public void testType() {
        assertEquals(CardType.AMERICAN_EXPRESS, c(null, "378282246310005", null, null, null).getType());
        assertEquals(CardType.AMERICAN_EXPRESS, c(null, "371449635398431", null, null, null).getType());
        assertEquals(CardType.AMERICAN_EXPRESS, c(null, "378734493671000", null, null, null).getType());
        assertEquals(CardType.DINERS_CLUB, c(null, "30569309025904", null, null, null).getType());
        assertEquals(CardType.DINERS_CLUB, c(null, "38520000023237", null, null, null).getType());
        assertEquals(CardType.DISCOVER, c(null, "6011111111111117", null, null, null).getType());
        assertEquals(CardType.DISCOVER, c(null, "6011000990139424", null, null, null).getType());
        assertEquals(CardType.JCB, c(null, "3530111333300000", null, null, null).getType());
        assertEquals(CardType.JCB, c(null, "3566002020360505", null, null, null).getType());
        assertEquals(CardType.MASTERCARD, c(null, "5555555555554444", null, null, null).getType());
        assertEquals(CardType.MASTERCARD, c(null, "5105105105105100", null, null, null).getType());
        assertEquals(CardType.VISA, c(null, "4111111111111111", null, null, null).getType());
        assertEquals(CardType.VISA, c(null, "4012888888881881", null, null, null).getType());
        assertEquals(CardType.UNKNOWN, c(null, "9999999999999999", null, null, null).getType());
    }

}
