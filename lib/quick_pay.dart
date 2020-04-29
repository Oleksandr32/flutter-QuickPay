import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';
import 'package:quick_pay/payment.dart';
import 'package:quick_pay/exceptions.dart';

class QuickPay {
  static const MethodChannel _channel = const MethodChannel('quick_pay');

  static Future<void> init({@required String apiKey}) async {
    _channel.invokeMethod('init', {'api-key': apiKey});
  }

  static Future<Payment> makePayment({String currency, String orderId, double price}) async {
    try {
      final result = await _channel.invokeMethod(
        'makePayment',
        <String, dynamic>{
          'currency': currency,
          'order-id': orderId,
          'price': price,
        },
      );

      return Payment.fromMap(result);
    } on PlatformException catch (error) {
      switch (error.code) {
        case "0":
          throw QuickPaySetupException(error.message);
          break;
        case "1":
          throw CreatePaymentException(error.details);
          break;
        case "2":
          throw CreatePaymentLinkException(error.details);
          break;
        case "3":
          throw ActivityException(error.details);
          break;
        case "4":
          throw ActivityFailureException(error.details);
          break;
        case "5":
          throw PaymentFailureException(error.details);
          break;
        default:
          rethrow;
      }
    }
  }
}
