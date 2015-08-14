# EveryPay Android SDK

> Warning: the Android SDK is still in an alpha stage. Significant API changes may happen before a 1.0 release.

* [Adding the SDK to your project](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#adding-the-sdk-to-your-project)
* [Overview](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#overview)
* [Integrating the SDK](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#integrating-the-sdk)
* [Required Android permissions](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#required-android-permissions)
* [Customising the app <-> merchant server communication](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-the-app---merchant-server-communication)
* [Customising the card input form](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-the-card-input-form)
* [Customising everything](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#customising-everything)

## Adding the SDK to your project

To add the SDK to your Android Studio project, please add the following line to your `app/build.gradle` file:

```
dependencies {
    ... Other dependencies ...
    
    compile 'com.everypay.sdp:android-sdk:0.1'
}
```

Individual releases are also available at https://github.com/UnifiedPaymentSolutions/everypay-android/releases and via git tags here.



## Overview

The payment process happens in four steps.

1. Card input. The user enters their card details, which are validated for basic errors (length and checksum on the number, length of the verification code, date of expiry in the future).
2. API call to the merchant (your) server, requesting EveryPay API credentials.
3. EveryPay API call, which saves & validates the card details, and returns an encrypted card token.
4. API call to the merchant (your) server with the encrypted card token. The merchant can decrypt the card token with a server-side EveryPay API call, and make a payment immediately or save it for later.

Example implementation is provided for steps 2 and 4, even if they are likely to be replaced in most apps. Step 1 may be replaced if the provided options for customising the look and feel of the CardFormActivity is not sufficient to match your branding.

## Integrating the SDK



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
