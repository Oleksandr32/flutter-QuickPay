import Flutter
import UIKit
import QuickPaySDK

public class SwiftQuickPayPlugin: NSObject, FlutterPlugin {
    private let METHOD_CALL_INIT = "init"
    private let METHOD_CALL_MAKE_PAYMENT = "makePayment"
    
    private let CREATE_PAYMENT_ERROR = "1"
    private let CREATE_PAYMENT_LINK_ERROR = "2"
    private let ACTIVITY_ERROR = "3"
    private let ACTIVITY_FAILURE_ERROR = "4"
    private let PAYMENT_FAILURE_ERROR = "5"
    
    private var currentPaymentId: Int? = nil
    private var pendingResult: FlutterResult? = nil
    private var viewController: UIViewController? = nil
    
    init(viewController: UIViewController) {
        self.viewController = viewController
    }

    public static func register(with registrar: FlutterPluginRegistrar) {
        let viewController: UIViewController = (UIApplication.shared.delegate?.window??.rootViewController)!;
        let channel = FlutterMethodChannel(name: "quick_pay", binaryMessenger: registrar.messenger())
        let instance = SwiftQuickPayPlugin(viewController: viewController)
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        pendingResult = result
        if (call.method == METHOD_CALL_INIT) {
            let args = call.arguments as! NSDictionary
            guard let apiKey : String = args.value(forKey: "api-key") as? String  else { return }
            initQuickPay(apiKey: apiKey)
        } else if (call.method == METHOD_CALL_MAKE_PAYMENT) {
            let args = call.arguments as! NSDictionary
            guard let currency : String = args.value(forKey: "currency") as? String  else { return }
            guard let orderId : String = args.value(forKey: "order-id") as? String  else { return }
            guard let price : Double = args.value(forKey: "price") as? Double  else { return }
            makePayment(currency: currency, orderId: orderId, price: price)
        }
    }
    
    private func initQuickPay(apiKey: String) {
        QuickPay.initWith(apiKey: apiKey)
    }
    
    private func makePayment(currency: String, orderId: String, price: Double) {
        let createPeymentParams = QPCreatePaymentParameters(currency: currency, order_id: orderId)
        let createPaymentRequest = QPCreatePaymentRequest(parameters: createPeymentParams)
        
        createPaymentRequest.sendRequest(success: { (payment) in
            self.currentPaymentId = payment.id
            
            let createPaymentLinkParams = QPCreatePaymentLinkParameters(id: payment.id, amount: price)
            let createPaymentLinkRequest = QPCreatePaymentLinkRequest(parameters: createPaymentLinkParams)
            
            createPaymentLinkRequest.sendRequest(success: { (paymentLink) in
                QuickPay.openPaymentLink(paymentUrl: paymentLink.url, onCancel: {
                    self.pendingResult!(FlutterError(code: self.ACTIVITY_ERROR, message: "User cancel payment", details: "User cancel payment"))
                }, onResponse: { (success) in
                    if (success) {
                        if let paymentId = self.currentPaymentId {
                            self.currentPaymentId = nil

                            QPGetPaymentRequest(id: paymentId).sendRequest(success: { (payment) in
                                self.pendingResult!(payment.accepted)
                            }, failure: { (data, response, error) in
                                self.pendingResult!(FlutterError(code: self.PAYMENT_FAILURE_ERROR, message: String(data: data!, encoding: String.Encoding.utf8)!, details: String(data: data!, encoding: String.Encoding.utf8)!))
                            })
                        }
                    } else {
                        self.pendingResult!(FlutterError(code: self.ACTIVITY_FAILURE_ERROR, message: "User cancel payment", details: "User cancel payment"))
                    }
                }, presentation: .present(controller: self.viewController!, animated: true, completion: nil))
            }, failure: { (data, response, error) in
                self.pendingResult!(FlutterError(code: self.CREATE_PAYMENT_LINK_ERROR, message: String(data: data!, encoding: String.Encoding.utf8)!, details: String(data: data!, encoding: String.Encoding.utf8)!))
            })
        }, failure: { (data, response, error) in
            self.pendingResult!(FlutterError(code: self.CREATE_PAYMENT_ERROR, message: String(data: data!, encoding: String.Encoding.utf8)!, details: String(data: data!, encoding: String.Encoding.utf8)!))
        })
    }
}
