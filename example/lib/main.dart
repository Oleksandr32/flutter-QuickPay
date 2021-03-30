import 'package:flutter/material.dart';
import 'package:uuid/uuid.dart';

import 'package:quick_pay/quick_pay.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  void _initQuickPay() {
    QuickPay.init(apiKey: 'PUT HERE YOUR QUICK PAY API KEY');
  }

  void _makePayment() async {
    try {
      final orderId = Uuid().v4().replaceAll('-', '').substring(0, 14);
      final price = 245.0 * 100;
      final payment = await QuickPay.makePayment(currency: 'DKK', orderId: orderId, price: price);
    } catch (error) {
      // TODO: handle error
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              FlatButton(
                color: Colors.blue,
                onPressed: _initQuickPay,
                child: Text('init'),
              ),
              FlatButton(
                color: Colors.blue,
                onPressed: _makePayment,
                child: Text('make payment'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
