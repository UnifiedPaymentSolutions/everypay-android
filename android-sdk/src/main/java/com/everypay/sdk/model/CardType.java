package com.everypay.sdk.model;


import android.text.TextUtils;

import com.everypay.sdk.R;

public enum CardType {

    // Based on http://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29
    AMERICAN_EXPRESS(R.drawable.ep_bt_amex, "American Express", 15, "34", "37"),
    DISCOVER(R.drawable.ep_bt_discover, "Discover", 16, "60", "62", "64", "65"),
    JCB(R.drawable.ep_bt_jcb, "JCB", 16, "35"),
    DINERS_CLUB(R.drawable.ep_bt_diners, "Diners Club", 14, "300", "301", "302", "303", "304", "305", "309", "36", "38", "37", "39"),
    VISA(R.drawable.ep_bt_visa, "Visa", 16, "4"),
    MASTERCARD(R.drawable.ep_bt_mastercard, "Mastercard", 16, "50", "51", "52", "53", "54", "55"),
    UNKNOWN(-1, "Unknown", 16);

    public static final int INVALID_ICON = -1;

    private int iconId;
    private String displayName;
    private int length;
    private String[] prefixes;

    CardType(int iconId, String displayName, int length, String... prefixes) {
        this.iconId = iconId;
        this.displayName = displayName;
        this.length = length;
        this.prefixes = prefixes;
    }

    public int getIconId() {
        return iconId;
    }

    public int getLength() {
        return length;
    }

    public static CardType byPrefixOf(String number) {
        if (TextUtils.isEmpty(number))
            return UNKNOWN;
        for (CardType type : CardType.values()) {
            if (type.prefixes != null && type.prefixes.length > 0) {
                for (String prefix : type.prefixes) {
                    if (number.startsWith(prefix))
                        return type;
                }
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
