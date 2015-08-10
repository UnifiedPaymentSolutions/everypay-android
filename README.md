# EveryPay Android SDK

## Adding the SDK to your project

To add the SDK to your Android Studio project, please add the following line to your `app/build.gradle` file:

```
dependencies {
    ... Other dependencies ...
    
    compile 'com.every-pay:everypay-android:0.1'
}
```

Individual releases are also available at https://github.com/UnifiedPaymentSolutions/everypay-android/releases and via git tags here.

## Overview

## Integrating the SDK

## Customising the app <-> merchant server communication

The SDK includes example implementation for the app - merchant API calls, with the minimal required data for a payment. However, most apps using EveryPay will want to customise the communication between the app and your server - for example to add your own user accounts, save shopping baskets or subscription plans.



## Customising the card input form

## Required Android permissions

The SDK requires `<uses-permission android:name="android.permission.INTERNET" />` for internet access, and `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>` for device info.

If the app uses location services and has the `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />` permission, then it will collect user location as an extra device info field. If the permission is missing from the app, then user location is skipped.
