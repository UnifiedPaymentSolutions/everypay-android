package com.everypay.sdk.model;


import android.os.Parcel;

/**
 * Using https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
 */
public class CardParcelTest extends CardTestBase {

    public void testParcel() {
        Card c1 = c("John Smith", "5555555555554444", "05", "2015", "123");

        Parcel p = Parcel.obtain();
        c1.writeToParcel(p, 0);
        p.setDataPosition(0);
        Card c2 = new Card(p);
        p.recycle();

        assertEquals(c1.getName(), c2.getName());
        assertEquals(c1.getNumber(), c2.getNumber());
        assertEquals(c1.getExpMonth(), c2.getExpMonth());
        assertEquals(c1.getExpYear(), c2.getExpYear());
        assertEquals(c1.getCVC(), c2.getCVC());
        assertEquals(c1.getType(), c2.getType());
    }

}
