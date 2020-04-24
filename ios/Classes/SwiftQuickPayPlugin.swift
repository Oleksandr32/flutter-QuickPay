import Flutter
import UIKit
import QuickPaySDK

public class SwiftQuickPayPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "quick_pay", binaryMessenger: registrar.messenger())
    let instance = SwiftQuickPayPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if (call.method == "init") {
        let args = call.arguments as! NSDictionary
        guard let apiKey : String = args.value(forKey: "api-key") as? String  else { return }
        QuickPay.initWith(apiKey: apiKey)
    }
  }
}
