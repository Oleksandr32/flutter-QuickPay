# Flutter QuickPay

A Flutter plugin for QuickPay payments in your mobile application.

This is a light and simple implementation of QuickPay payments. So if somebody will use it, don't be shy to create an issues so then I will improve or add new features to this plugin.

For more information please check QuickPay website: https://quickpay.net/

## Usage

First you need to initialize QuickPay.
For this please find API key in your account at https://quickpay.net/.

```dart

QuickPay.init(apiKey: 'PUT HERE YOUR QUICK PAY API KEY');

```

After successfully initializing, you can make payment. For that use static function `makePayment`.
Now available such params:
```
String currency,
String orderId,
double price,
int autoCapture // Optional
```

```dart

try {
  final price = 245.0 * 100;
  final payment = await QuickPay.makePayment(
    currency: 'DKK',
    orderId: '12345',
    price: price,
  );
} catch (error) {
  // handle error
}
```


## Examples

 Android                   |  IOS
:-------------------------:|:-------------------------:
<img src="https://github.com/Oleksandr32/flutter-QuickPay/raw/master/gifs/android.gif" width="250" height="500">  |  <img src="https://github.com/Oleksandr32/flutter-QuickPay/raw/master/gifs/ios.gif" width="250" height="500">