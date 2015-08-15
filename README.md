# EveryPay Android SDK

> Warning: the Android SDK is still in an alpha stage. Significant API changes may happen before a 1.0 release.

* [Adding the SDK to your project](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#adding-the-sdk-to-your-project)
* [Overview](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#overview)
* [Integrating the SDK](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#integrating-the-sdk)
* [Required Android permissions](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#required-android-permissions)
* [Customising the app <-> merchant server communication](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-the-app---merchant-server-communication)
* [Customising the card input form](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-the-card-input-form)
* [Customising everything](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-everything)


## Overview

The payment process happens in four steps.

1. Card input. The user enters their card details, which are validated for basic errors (length and checksum on the number, length of the verification code, date of expiry in the future). While the user fills out the card details, a device fingerprint is collected in the background.
2. API call to the merchant (your) server, requesting EveryPay API credentials.
3. EveryPay API call, which saves & validates the card details, and returns an encrypted card token.
4. API call to the merchant (your) server with the encrypted card token. The merchant can decrypt the card token with a server-side EveryPay API call, and make a payment immediately or save it for later.

Example implementation is provided for steps 2 and 4, even if they are likely to be replaced in most apps. Step 1 may be replaced if the provided options for customising the look and feel of the CardFormActivity is not sufficient to match your branding.

## Integrating the SDK

### Add the SDK to your Android Studio project

Add the following line to your `app/build.gradle` file:

```
dependencies {
    ... Other dependencies ...
    compile 'com.everypay.sdp:android-sdk:0.1'
}
```

Note that it goes into the app module build file (`app/build.gradle` or similar), NOT the project-wide build file (`./build.gradle`). If there is `apply plugin: 'com.android.application'` at the top of the file, it's probably the right one.

Individual releases are also available at https://github.com/UnifiedPaymentSolutions/everypay-android/releases and via git tags here.

### Configure the SDK parameters

Create a new EveryPay object, for example in your payment activity onCreate():

```
Everypay ep = Everypay.with(this).setEverypayApiBaseUrl(Everypay.EVERYPAY_API_URL_TESTING).setMerchantApiBaseUrl(Everypay.MERCHANT_API_URL_TESTING).build();
```

Or create and save one as the default for the app, for example in your Application subclass's onCreate(), and fetch it in the activity:

```
Everypay.with(this).setEverypayApiBaseUrl(Everypay.EVERYPAY_API_URL_TESTING).setMerchantApiBaseUrl(Everypay.MERCHANT_API_URL_TESTING).build().setDefault();

Everypay ep = Everypay.getDefault();
```

### Add a listener for payment API events

```
EverypayListener epListener = new EverypayListener() {
    @Override
    public void stepStarted(StepType step) {
        Log.d("logtag", "Started payment step " + step);
    }

    @Override
    public void stepSuccess(StepType step) {
        Log.d("logtag", "Completed payment step " + step);
    }

    @Override
    public void fullSuccess() {
        Log.d("logtag", "Payment complete, your server now has a card token!");
        toast("Payment successful!");
    }

    @Override
    public void stepFailure(StepType step, Exception e) {
        Log.e("logtag", "Payment failed at step " + step);
    }
});
```

### Start and receive a result from the CardFormActivity

When the user is ready to start, start CardFormActivity:

```
findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        CardFormActivity.startForResult(MainActivity.this, "Enter Card Details");
    }
});
```

In the same activity, override and handle `onActivityResult()`, which is called after CardFormActivity finishes:

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CardFormActivity.REQUEST_CODE) {
            Pair<Card, String> result = CardFormActivity.getCardAndDeviceInfoFromResult(resultCode, data);
            if (result != null) {
                Log.d("logtag", "Valid card entered, start payment flow");
                ... See next section ...
            } else {
                Log.e("logtag", "No valid card entered.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```

### Start payment flow





## Required Android permissions

The SDK requires `<uses-permission android:name="android.permission.INTERNET" />` for internet access, and `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>` for device info.

If the app uses location services and has the `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />` permission, then it will collect user location as an extra device info field. If the permission is missing from the app, then user location is skipped.



## Customising the app <-> merchant server communication

The SDK includes example implementation for the app - merchant API calls, with the minimal required data for a payment. However, most apps using EveryPay will want to replace the communication step between the app and your server - for example to add your own user accounts, save shopping baskets or subscription plans.

To provide a replacement, override the `run()` method in a MerchantParamsStep subclass:

```
public class MyMerchantParamsStep extends MerchantParamsStep {

    @Override
    public MerchantParamsResponseData run(Activity activity, Everypay ep) {
        // Your implementation
    }
}
```

and pass it to `EveryPay.startFullPaymentFlow()`:

```
TODO
```

`run()` is called on a background thread, and can be synchronous. If it throws an exception, the payment process is cancelled and the `stepFailure()` listener method is called on the main thread.


## Customising the card input form


## Customising everything
