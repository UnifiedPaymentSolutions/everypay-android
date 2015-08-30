package com.everypay.sdk.model;


import android.content.Context;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockResources;
import android.util.Pair;

import com.everypay.sdk.R;
import com.everypay.sdk.util.Reflect;

public class CardTestBase extends AndroidTestCase {

    Context mockContext;

    public void setUp() {
        mockContext = new TestMockContext();
    }

    public void assertError(String expectedMessage, boolean expectedPartialOk, CardError e) {
        assertEquals(expectedMessage, e.getMessage());
        assertEquals(expectedPartialOk, e.partialOk);
    }

    public void assertThrows(Card card, String method, String message, boolean partialOk) throws Throwable {
        try {
            Pair<Class, Object> args = new Pair<Class, Object>(Context.class, mockContext);
            Reflect.call(card, method, args);
            fail();
        } catch (CardError e) {
            assertError(message, partialOk, e);
        }
    }


    /*
     * Helpers
     */

    public static Card c(String name, String number, String expMonth, String expYear, String cvc) {
        return new Card(name, number, expMonth, expYear, cvc);
    }

    private static class TestMockContext extends MockContext {
        MockResources res;

        TestMockContext() {
            res = new TestMockResources();
        }

        @Override
        public Resources getResources() {
            return res;
        }
    }

    private static class TestMockResources extends MockResources {
        @Override
        public String getString(int id) throws NotFoundException {
            if (id == R.string.ep_cc_error_name_missing) return "name_missing";
            if (id == R.string.ep_cc_error_number_missing) return "number_missing";
            if (id == R.string.ep_cc_error_number_invalid) return "number_invalid";
            if (id == R.string.ep_cc_error_number_short) return "number_short";
            if (id == R.string.ep_cc_error_number_long) return "number_long";
            if (id == R.string.ep_cc_error_month_missing) return "month_missing";
            if (id == R.string.ep_cc_error_month_invalid) return "month_invalid";
            if (id == R.string.ep_cc_error_year_missing) return "year_missing";
            if (id == R.string.ep_cc_error_year_invalid) return "year_invalid";
            if (id == R.string.ep_cc_error_year_short) return "year_short";
            if (id == R.string.ep_cc_error_expired) return "expired";
            if (id == R.string.ep_cc_error_cvc_missing) return "cvc_missing";
            if (id == R.string.ep_cc_error_cvc_invalid) return "cvc_invalid";
            if (id == R.string.ep_cc_error_cvc_invalid_american_express) return "cvc_amex";
            return super.getString(id);
        }
    }
}
