# EveryPay Android SDK

The EveryPay Android SDK is in the beta stage. Significant API changes are not expected, but improvements may be added as we receive feedback from customers.

* [Overview](https://github.com/UnifiedPaymentSolutions/everypay-android#overview)
* Integrating the SDK
  * [Add the SDK to your Android Studio project](https://github.com/UnifiedPaymentSolutions/everypay-android#add-the-sdk-to-your-android-studio-project)
  * [Configure the SDK parameters](https://github.com/UnifiedPaymentSolutions/everypay-android#configure-the-sdk-parameters)
  * [Add a listener for payment flow events](https://github.com/UnifiedPaymentSolutions/everypay-android#add-a-listener-for-payment-flow-events)
  * [Start the payment flow](https://github.com/UnifiedPaymentSolutions/everypay-android#start-the-payment-flow)
* [Required Android permissions](https://github.com/UnifiedPaymentSolutions/everypay-android#required-android-permissions)
* [Customising the app <-> merchant server communication steps](https://github.com/UnifiedPaymentSolutions/everypay-android#customising-the-app---merchant-server-communication-steps)
* [Theming the card input form](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/README.md#theming-the-card-input-form)
* [Customising the card input form](https://github.com/UnifiedPaymentSolutions/everypay-android#customising-the-card-input-form)
* [Customising everything](https://github.com/UnifiedPaymentSolutions/everypay-android#customising-everything)


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
   compile 'com.everypay.sdk:android-sdk:1.0.0'
}
```

Note that it goes into the app module build file (`app/build.gradle` or similar), NOT the project-wide build file (`./build.gradle`). If there is `apply plugin: 'com.android.application'` at the top of the file, it's probably the right one.

If you wish to download a copy of the SDK to add it to your project manually, then you can find the .aar library files at https://bintray.com/everypay/maven/android-sdk/view#files

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

### Add a listener for payment flow events

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

### Start the payment flow

When the user is ready to start, start CardFormActivity:

```
findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // Optional initial card data.
        Card initial = new Card();
        initial.setName("Tom Smith");
        // Maybe more initial fields.

        // Use null for initial to leave the card form empty at start.
        CardFormActivity.startForResult(MainActivity.this, initial);
    }
});
```

In the same activity, override and handle `onActivityResult()`, which is called after CardFormActivity finishes. Note the call to `startFullPaymentFlow()` with the received card, device info and the listener you created.

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CardFormActivity.REQUEST_CODE) {
        Pair<Card, String> result = CardFormActivity.getCardAndDeviceInfoFromResult(resultCode, data);
        if (result != null) {
            Log.d("logtag", "Valid card entered, starting payment flow");
            Everypay.getDefault().startFullPaymentFlow(result.first, result.second, everypayListener);
        } else {
            Log.e("logtag", "No valid card entered.");
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
```

The API call steps of the payment flow will run on a background thread, and results will be posted to the listener on the main UI thread.


## Required Android permissions

The SDK requires `<uses-permission android:name="android.permission.INTERNET" />` for internet access, and `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>` for device info.

If the app uses location services and has the `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />` permission, then it will collect user location as an extra device info field. If the permission is missing from the app, then user location is skipped.


## Customising the app <-> merchant server communication steps

The SDK includes example implementation for the app - merchant API calls, with the minimal required data for a payment. However, most apps using EveryPay will want to replace the communication step between the app and your server - for example to add your own user accounts, save shopping baskets or subscription plans.

To provide a replacement, create subclasses of MerchantParamsStep and MerchantPaymentStep:

```
public class MyMerchantParamsStep extends MerchantParamsStep {
    @Override
    public MerchantParamsResponseData run(Context activity, Everypay ep, String deviceInfo) {
        // Your implementation
    }
}
public class MyMerchantPaymentStep extends MerchantPaymentStep {
    @Override
    public MerchantPaymentResponseData run(Context activity, Everypay ep, MerchantParamsResponseData paramsResponse, EverypayTokenResponseData everypayResponse) {
        // Your implementation
    }
}
```

and pass an instance of the step when configuring EveryPay:

```
Everypay.with(this).setMerchantParamsStep(new MyMerchantParamsStep()).setMerchantPaymentStep(new MyMerchantPaymentStep()) ... .build();
```

`run()` is called on a background thread, and can be synchronous. If it throws an exception, the payment process is cancelled and the `stepFailure()` listener method is called on the main thread.

## Theming the card input form

To override the text, color and styles used in the card input form, define resources matching the identifiers in your `colors.xml`, `strings.xml` and `styles.xml` files. The EveryPay SDK resources start with the `ep_` prefix.

For an example, see https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/app/src/main/res/values/strings.xml

For more substantial theming, overriding the `layout/activity_cardform.xml` with your own layout is also a possibility.


## Customising the card input form

If the EveryPay card input form does not match your requirements, or if you wish to add custom branding beyond the configuration options, then you can create a custom one. There are two requirements for a custom card form:

* It should construct a [Card model](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/sdk/src/main/java/com/everypay/sdk/model/Card.java). The Card model can also be used to validate the inputs.
* Use the [`DeviceInfoCollector`](https://github.com/UnifiedPaymentSolutions/everypay-android/blob/master/sdk/src/main/java/com/everypay/sdk/deviceinfo/DeviceCollector.java) to obtain a device fingerprint. Collecting the device info in the background while the user is entering card details is a good choice, since it may take tens of seconds to get the (optional) GPS location.

After your custom card form has returned a Card model and the device info string, pass them to `Everypay.startFullPaymentFlow()` as usual.


## Customising everything

In a complex app it's possible that you might need to customise most of the SDK: both the merchant server <-> app communication steps (you might already have your own APIs), the card input form, and perhaps the ways how the success/failure events are tied to the UI.

In this case it might make more sense to skip the provided steps and `startFullPaymentFlow()`, and just run step 3 (Everypay API call) directly from your code.

It is provided as a Retrofit API call in both synchronous and asynchronous versions:

```
EverypayTokenResponseData respWithToken = Everypay.getDefault().getEverypayApi().saveCard(new EverypayTokenRequestData(paramsResponse, card, deviceInfo));

Everypay.getDefault().getEverypayApi().saveCard(new EverypayTokenRequestData(paramsResponse, card, deviceInfo), callback);
```


