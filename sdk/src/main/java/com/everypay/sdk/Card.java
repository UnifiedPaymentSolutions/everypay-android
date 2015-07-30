package com.everypay.sdk;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.everypay.sdk.util.DateUtils;
import com.everypay.sdk.util.TxtUtils;

/**
 * Customised from https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/model/Card.java
 */
public class Card implements Parcelable {

    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS_CLUB = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String UNKNOWN = "Unknown";

    // Based on http://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DISCOVER = {"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302", "303", "304", "305", "309", "36", "38", "37", "39"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String[] PREFIXES_MASTERCARD = {"50", "51", "52", "53", "54", "55"};

    public static final int MAX_LENGTH_STANDARD = 16;
    public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
    public static final int MAX_LENGTH_DINERS_CLUB = 14;

    private String number;
    private String cvc;
    private Integer expMonth;
    private Integer expYear;
    private String name;
    private String address;
    private String addressCity;
    private String addressState;
    private String addressZip;
    private String addressCountry;

    public static class Builder {
        private String number;
        private String cvc;
        private Integer expMonth;
        private Integer expYear;
        private String name;
        private String address;
        private String addressCity;
        private String addressState;
        private String addressZip;
        private String addressCountry;

        public Builder() {
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder cvc(String cvc) {
            this.cvc = cvc;
            return this;
        }

        public Builder expMonth(String expMonth) {
            try {
                this.expMonth = Integer.parseInt(expMonth, 10);
            } catch (NumberFormatException e) {
            }
            return this;
        }

        public Builder expYear(String expYear) {
            try {
                this.expYear = Integer.parseInt(expYear, 10);
            } catch (NumberFormatException e) {
            }
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder addressCity(String city) {
            this.addressCity = city;
            return this;
        }

        public Builder addressState(String state) {
            this.addressState = state;
            return this;
        }

        public Builder addressZip(String zip) {
            this.addressZip = zip;
            return this;
        }

        public Builder addressCountry(String country) {
            this.addressCountry = country;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }

    private Card(Builder builder) {
        this.number = TxtUtils.nullIfBlank(normalizeCardNumber(builder.number));
        this.expMonth = builder.expMonth;
        this.expYear = builder.expYear;
        this.cvc = TxtUtils.nullIfBlank(builder.cvc);
        this.name = TxtUtils.nullIfBlank(builder.name);
        this.address = TxtUtils.nullIfBlank(builder.address);
        this.addressCity = TxtUtils.nullIfBlank(builder.addressCity);
        this.addressState = TxtUtils.nullIfBlank(builder.addressState);
        this.addressZip = TxtUtils.nullIfBlank(builder.addressZip);
        this.addressCountry = TxtUtils.nullIfBlank(builder.addressCountry);
    }

    public Card(String number, Integer expMonth, Integer expYear, String cvc, String name, String address, String addressCity, String addressState, String addressZip, String addressCountry, String type) {
        this.number = TxtUtils.nullIfBlank(normalizeCardNumber(number));
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = TxtUtils.nullIfBlank(cvc);
        this.name = TxtUtils.nullIfBlank(name);
        this.address = TxtUtils.nullIfBlank(address);
        this.addressCity = TxtUtils.nullIfBlank(addressCity);
        this.addressState = TxtUtils.nullIfBlank(addressState);
        this.addressZip = TxtUtils.nullIfBlank(addressZip);
        this.addressCountry = TxtUtils.nullIfBlank(addressCountry);
    }

    public boolean validateCard() {
        if (cvc == null) {
            return validateName() && validateNumber() && validateExpiryDate();
        } else {
            return validateName() && validateNumber() && validateExpiryDate() && validateCVC();
        }
    }

    public boolean validateName() {
        return !TextUtils.isEmpty(name);
    }

    public boolean validateNumber() {
        if (TxtUtils.isBlank(number)) {
            return false;
        }

        String rawNumber = number.trim().replaceAll("\\s+|-", "");
        if (TxtUtils.isBlank(rawNumber)
                || !TxtUtils.isWholePositiveNumber(rawNumber)
                || !isValidLuhnNumber(rawNumber)) {
            return false;
        }

        if (AMERICAN_EXPRESS.equals(getType())) {
            return rawNumber.length() == MAX_LENGTH_AMERICAN_EXPRESS;
        } else if (DINERS_CLUB.equals(getType())) {
            return rawNumber.length() == MAX_LENGTH_DINERS_CLUB;
        } else {
            return rawNumber.length() == MAX_LENGTH_STANDARD;
        }
    }

    public boolean validateExpiryDate() {
        if (!validateExpMonth()) {
            return false;
        }
        if (!validateExpYear()) {
            return false;
        }
        return !DateUtils.hasMonthPassed(expYear, expMonth);
    }

    public boolean validateExpMonth() {
        if (expMonth == null) {
            return false;
        }
        return (expMonth >= 1 && expMonth <= 12);
    }

    public boolean validateExpYear() {
        if (expYear == null) {
            return false;
        }
        return !DateUtils.hasYearPassed(expYear);
    }

    public boolean validateCVC() {
        if (TxtUtils.isBlank(cvc)) {
            return false;
        }
        String cvcValue = cvc.trim();

        boolean validLength;
        if (AMERICAN_EXPRESS.equals(getType())) {
            validLength = cvcValue.length() == 4;
        } else {
            validLength = cvcValue.length() == 3;
        }

        return TxtUtils.isWholePositiveNumber(cvcValue) && validLength;
    }

    private boolean isValidLuhnNumber(String number) {
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

    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", "");
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCVC() {
        return cvc;
    }

    public void setCVC(String cvc) {
        this.cvc = cvc;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getType() {
        if (!TxtUtils.isBlank(number)) {
            if (TxtUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
                return AMERICAN_EXPRESS;
            } else if (TxtUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
                return DISCOVER;
            } else if (TxtUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
                return JCB;
            } else if (TxtUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
                return DINERS_CLUB;
            } else if (TxtUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
                return VISA;
            } else if (TxtUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
                return MASTERCARD;
            }
        }
        return UNKNOWN;
    }

    protected Card(Parcel in) {
        number = in.readString();
        cvc = in.readString();
        expMonth = in.readInt();
        expYear = in.readInt();
        name = in.readString();
        address = in.readString();
        addressCity = in.readString();
        addressState = in.readString();
        addressZip = in.readString();
        addressCountry = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(cvc);
        dest.writeInt(expMonth == null ? -1 : expMonth);
        dest.writeInt(expYear == null ? -1 : expYear);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(addressCity);
        dest.writeString(addressState);
        dest.writeString(addressZip);
        dest.writeString(addressCountry);
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
