import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';
import 'package:quick_pay/quick_pay_exceptions.dart';

class QuickPay {
  static const MethodChannel _channel = const MethodChannel('quick_pay');

  static Future<void> init({@required String apiKey}) async {
    _channel.invokeMethod('init', {'api-key': apiKey});
  }

  static Future<bool> makePayment({String currency, String orderId, double price}) async {
    try {
      final result = await _channel.invokeMethod(
        'makePayment',
        <String, dynamic>{
          'currency': currency,
          'order-id': orderId,
          'price': price,
        },
      );

      return result;
    } on PlatformException catch (error) {
      switch (error.code) {
        case "0":
          throw CreatePaymentException(error.message);
          break;
        case "1":
          throw CreatePaymentLinkException(error.message);
          break;
        case "2":
          throw ActivityException(error.message);
          break;
        case "3":
          throw ActivityFailureException(error.message);
          break;
        case "4":
          throw PaymentFailureException(error.message);
          break;
        default:
          break;
      }

      return false;
    }
  }
}
