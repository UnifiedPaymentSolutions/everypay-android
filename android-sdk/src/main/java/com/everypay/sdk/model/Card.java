package com.everypay.sdk.model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.everypay.sdk.R;
import com.everypay.sdk.util.DateUtils;

import java.io.Serializable;

/**
 * Customised from https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/model/Card.java
 */
public class Card implements Parcelable {

    private static final int YEAR_MIN_VALUE = 1900;
    private static final int YEAR_MAX_VALUE = 2100;
    private static final int MONTH_MIN_VALUE = 1;
    private static final int MONTH_MAX_VALUE = 12;
    private static final int EXPIRE_DATE_DIGIT_COUNT = 4;
    private static final int CVC_COUNT_AMERICAN_EXPRESS = 4;
    private static final int CVC_COUNT = 3;

    private String name;
    private String number;
    private String cvc;
    private String expMonth;
    private String expYear;

    public Card() {
        normalize();
    }

    public Card(String name, String number, String expMonth, String expYear, String cvc) {
        this.name = name;
        this.number = number;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = cvc;
        normalize();
    }

    public void validateCard(Context context) throws CardError {
        validateName(context);
        validateNumber(context);
        validateExpiryDate(context);
        validateCVC(context);
    }

    public void validateName(Context context) throws CardError {
        if (TextUtils.isEmpty(name))
            CardError.raise(context, true, R.string.ep_cc_error_name_missing);
    }

    public void validateNumber(Context context) throws CardError {
        if (TextUtils.isEmpty(number))
            CardError.raise(context, true, R.string.ep_cc_error_number_missing);

        CardType type = getType();
        if (number.length() < type.getLength())
            CardError.raise(context, true, R.string.ep_cc_error_number_short);
        if (number.length() > type.getLength())
            CardError.raise(context, false, R.string.ep_cc_error_number_long);

        if (!isValidLuhnNumber(number))
            CardError.raise(context, false, R.string.ep_cc_error_number_invalid);
    }

    public void validateExpiryDate(Context context) throws CardError {
        int month = validateExpMonth(context);
        int year = validateExpYear(context);
        if (DateUtils.hasMonthPassed(year, month)) {
            CardError.raise(context, false, R.string.ep_cc_error_expired);
        }
    }

    public int validateExpMonth(Context context) throws CardError {
        if (TextUtils.isEmpty(expMonth))
            CardError.raise(context, true, R.string.ep_cc_error_month_missing);

        // normalize() already gets rid of non-digits.
        int month = Integer.parseInt(expMonth, 10);
        if (month < MONTH_MIN_VALUE)
            CardError.raise(context, true, R.string.ep_cc_error_month_invalid);
        if (month > MONTH_MAX_VALUE)
            CardError.raise(context, false, R.string.ep_cc_error_month_invalid);
        return month;
    }

    public int validateExpYear(Context context) throws CardError {
        if (TextUtils.isEmpty(expYear))
            CardError.raise(context, true, R.string.ep_cc_error_year_missing);

        // normalize() already gets rid of non-digits.
        if (expYear.length() < EXPIRE_DATE_DIGIT_COUNT)
            CardError.raise(context, true, R.string.ep_cc_error_year_short);

        int year = Integer.parseInt(expYear, 10);
        if (year < YEAR_MIN_VALUE || year > YEAR_MAX_VALUE)
            CardError.raise(context, false, R.string.ep_cc_error_year_invalid);
        return year;
    }

    public void validateCVC(Context context) throws CardError {
        if (TextUtils.isEmpty(cvc))
            CardError.raise(context, true, R.string.ep_cc_error_cvc_missing);

        // normalize() already gets rid of non-digits.
        if (getType() == CardType.AMERICAN_EXPRESS) {
            if (cvc.length() != CVC_COUNT_AMERICAN_EXPRESS)
                CardError.raise(context, cvc.length() < CVC_COUNT_AMERICAN_EXPRESS, R.string.ep_cc_error_cvc_invalid_american_express);
        } else {
            if (cvc.length() != CVC_COUNT)
                CardError.raise(context, cvc.length() < CVC_COUNT, R.string.ep_cc_error_cvc_invalid);
        }
    }

    private static boolean isValidLuhnNumber(String number) {
        boolean isOdd = true;
        int sum = 0;

        for (int index = number.length() - 1; index >= 0; index--) {
            char c = number.charAt(index);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digitInteger = Integer.parseInt("" + c);
            isOdd = !isOdd;

            if (isOdd) {
                digitInteger *= 2;
            }

            if (digitInteger > 9) {
                digitInteger -= 9;
            }

            sum += digitInteger;
        }

        return sum % 10 == 0;
    }

    private void normalize() {
        name = (name == null) ? "" : name;
        number = normalizeNumber(number);
        expMonth = normalizeNumber(expMonth);
        expYear = normalizeNumber(expYear);
        cvc = normalizeNumber(cvc);
    }

    private static String normalizeNumber(String num) {
        if (num == null) {
            return "";
        } else {
            return num.replaceAll("[^0-9]", "");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        normalize();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        normalize();
    }

    public String getCVC() {
        return cvc;
    }

    public void setCVC(String cvc) {
        this.cvc = cvc;
        normalize();
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
        normalize();
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
        normalize();
    }

    public CardType getType() {
        return CardType.byPrefixOf(number);
    }

    protected Card(Parcel in) {
        number = in.readString();
        cvc = in.readString();
        expMonth = in.readString();
        expYear = in.readString();
        name = in.readString();
        normalize();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(cvc);
        dest.writeString(expMonth);
        dest.writeString(expYear);
        dest.writeString(name);
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
