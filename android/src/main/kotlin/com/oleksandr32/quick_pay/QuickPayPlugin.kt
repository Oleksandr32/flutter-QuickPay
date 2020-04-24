package com.oleksandr32.quick_pay

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import net.quickpay.quickpaysdk.QuickPay
import net.quickpay.quickpaysdk.QuickPayActivity
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.QPCreatePaymentLinkParameters
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.QPCreatePaymentLinkRequest
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.QPCreatePaymentParameters
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.QPCreatePaymentRequest

/** QuickPayPlugin */
public class QuickPayPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel

    private var currentPaymentId: Int? = null

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        activity = activityPluginBinding.activity
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "quick_pay")
        channel.setMethodCallHandler(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "quick_pay")
            channel.setMethodCallHandler(QuickPayPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when {
            call.method == "init" -> {
                val apiKey = call.argument<String>("api-key") ?: return
                init(apiKey)
            }
            call.method == "makePayment" -> {
                val currency = call.argument<String>("currency")!!
                val orderId = call.argument<String>("order-id")!!
                val price = call.argument<Double>("price")!!
                makePayment(currency, orderId, price)
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun init(apiKey: String) {
        QuickPay.init(apiKey, context)
    }

    private fun makePayment(currency: String, orderId: String, price: Double, listener: Result) {
        val createPaymentParams = QPCreatePaymentParameters(currency, orderId)
        val createPaymentRequest = QPCreatePaymentRequest(createPaymentParams)

        createPaymentRequest.sendRequest(
                listener = { payment ->
                    currentPaymentId = payment.id

                    val createPaymentLinkParameters = QPCreatePaymentLinkParameters(payment.id, price)
                    val createPaymentLinkRequest = QPCreatePaymentLinkRequest(createPaymentLinkParameters)

                    createPaymentLinkRequest.sendRequest(
                            listener = { paymentLink ->
                                QuickPayActivity.openQuickPayPaymentWindow(activity, paymentLink)
                            },
                            errorListener = { statusCode, message, error ->
                                listener.error(statusCode, message, error?.message)
                            }
                    )
                },
                errorListener = { statusCode, message, error ->
                    listener.error(statusCode, message, error?.message)
                }
        )
    }
}
