import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:quick_pay/quick_pay.dart';
import 'package:uuid/uuid.dart';

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
                onPressed: () {
                  QuickPay.init(apiKey: '');
                },
                child: Text('init'),
              ),
              FlatButton(
                color: Colors.blue,
                onPressed: () async {
                  try {
                    final orderId = Uuid().v4().replaceAll('-', '').substring(0, 14);
                    final result = await QuickPay.makePayment(currency: 'DKK', orderId: orderId, price: 21.8);
                  } catch (e) {
                    // handle errors
                  }
                },
                child: Text('make payment'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
