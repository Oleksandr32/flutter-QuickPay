import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class QuickPay {
  static const MethodChannel _channel = const MethodChannel('quick_pay');

  static Future<void> init({@required String apiKey}) async {
    _channel.invokeMethod('init', {'api-key': apiKey});
  }

  static Future makePayment({String currency, String orderId, double price}) async {
    try {
      final result = await _channel.invokeMethod(
        'makePayment',
        <String, dynamic>{
          'currency': currency,
          'order-id': orderId,
          'price': price,
        },
      );
    } on PlatformException catch (error) {
      throw 'An error occured: code: ${error.code}, message: ${error.message}';
    }
  }
}
