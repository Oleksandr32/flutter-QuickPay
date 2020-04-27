package com.oleksandr32.quick_pay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import net.quickpay.quickpaysdk.QuickPay
import net.quickpay.quickpaysdk.QuickPayActivity
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.*

/** QuickPayPlugin */
public class QuickPayPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private var pendingResult: Result? = null

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
        private const val METHOD_CALL_INIT = "init"
        private const val METHOD_CALL_MAKE_PAYMENT = "makePayment"

        private const val CREATE_PAYMENT_ERROR = "0"
        private const val CREATE_PAYMENT_LINK_ERROR = "1"
        private const val ACTIVITY_ERROR = "2"
        private const val ACTIVITY_FAILURE_ERROR = "3"
        private const val PAYMENT_FAILURE_ERROR = "4"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "quick_pay")
            channel.setMethodCallHandler(QuickPayPlugin())
        }
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        pendingResult = result
        when (call.method) {
            METHOD_CALL_INIT -> {
                val apiKey = call.argument<String>("api-key") ?: return
                init(apiKey)
            }
            METHOD_CALL_MAKE_PAYMENT -> {
                val currency = call.argument<String>("currency")!!
                val orderId = call.argument<String>("order-id")!!
                val price = call.argument<Double>("price")!!
                makePayment(currency, orderId, price)
            }
            else -> result.notImplemented()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): Boolean {
        if (requestCode == QuickPayActivity.QUICKPAY_INTENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val returnedResult = intent?.data?.toString() ?: ""

                if (returnedResult == QuickPayActivity.SUCCESS_RESULT) {
                    if (currentPaymentId != null) {
                        val getPaymentRequest = QPGetPaymentRequest(currentPaymentId!!)

                        getPaymentRequest.sendRequest(
                                listener = { payment ->
                                    pendingResult?.success(payment.accepted)
                                },
                                errorListener = { _, message, error ->
                                    pendingResult?.error(PAYMENT_FAILURE_ERROR, message, error?.message)
                                }
                        )

                        currentPaymentId = null
                        clearResult()
                    }
                } else if (returnedResult == QuickPayActivity.FAILURE_RESULT) {
                    pendingResult?.error(ACTIVITY_FAILURE_ERROR, "QuickPayActivity failure", "")
                    clearResult()

                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                pendingResult?.error(ACTIVITY_ERROR, "Activity error", "")
                clearResult()
            }

            return true
        }

        return false
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun init(apiKey: String) {
        QuickPay.init(apiKey, context)
    }

    private fun makePayment(currency: String, orderId: String, price: Double) {
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
                            errorListener = { _, message, error ->
                                pendingResult?.error(CREATE_PAYMENT_LINK_ERROR, message, error?.message)
                                clearResult()
                            }
                    )
                },
                errorListener = { _, message, error ->
                    pendingResult?.error(CREATE_PAYMENT_ERROR, message, error?.message)
                    clearResult()
                }
        )
    }

    private fun clearResult() {
        pendingResult = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }
}
