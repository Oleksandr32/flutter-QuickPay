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
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import net.quickpay.quickpaysdk.QuickPay
import net.quickpay.quickpaysdk.QuickPayActivity
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.models.QPPayment
import net.quickpay.quickpaysdk.networking.quickpayapi.quickpaylink.payments.*

/** QuickPayPlugin */
public class QuickPayPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private var pendingResult: Result? = null

    private var currentPaymentId: Int? = null

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        activity = activityPluginBinding.activity
        activityPluginBinding.addActivityResultListener(this)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "quick_pay")
        channel.setMethodCallHandler(this)
    }

    companion object {
        private const val METHOD_CALL_INIT = "init"
        private const val METHOD_CALL_MAKE_PAYMENT = "makePayment"

        private const val QUICK_PAY_SETUP_ERROR = "0"
        private const val CREATE_PAYMENT_ERROR = "1"
        private const val CREATE_PAYMENT_LINK_ERROR = "2"
        private const val ACTIVITY_ERROR = "3"
        private const val ACTIVITY_FAILURE_ERROR = "4"
        private const val PAYMENT_FAILURE_ERROR = "5"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "quick_pay")
            channel.setMethodCallHandler(QuickPayPlugin())
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
                                    pendingResult?.success(convertQPPaymentToMap(payment))
                                    currentPaymentId = null
                                },
                                errorListener = { _, message, error ->
                                    pendingResult?.error(PAYMENT_FAILURE_ERROR, message, error?.message)
                                }
                        )
                    }
                } else if (returnedResult == QuickPayActivity.FAILURE_RESULT) {
                    pendingResult?.error(ACTIVITY_FAILURE_ERROR, "QuickPayActivity failure", "")

                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                pendingResult?.error(ACTIVITY_ERROR, "Activity error", "")
            }

            return true
        }

        return false
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
                val autoCapture = call.argument<Int>("auto-capture")
                makePayment(currency, orderId, price, autoCapture)
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

    private fun makePayment(currency: String, orderId: String, price: Double, autoCapture: Int?) {
        val createPaymentParams = QPCreatePaymentParameters(currency, orderId)
        val createPaymentRequest = QPCreatePaymentRequest(createPaymentParams)

        try {
            createPaymentRequest.sendRequest(
                    listener = { payment ->
                        currentPaymentId = payment.id

                        val createPaymentLinkParameters = QPCreatePaymentLinkParameters(payment.id, price).apply {
                            auto_capture = autoCapture
                        }
                        val createPaymentLinkRequest = QPCreatePaymentLinkRequest(createPaymentLinkParameters)

                        createPaymentLinkRequest.sendRequest(
                                listener = { paymentLink ->
                                    QuickPayActivity.openQuickPayPaymentWindow(activity, paymentLink)
                                },
                                errorListener = { _, message, error ->
                                    pendingResult?.error(CREATE_PAYMENT_LINK_ERROR, message, error?.message)
                                }
                        )
                    },
                    errorListener = { _, message, error ->
                        pendingResult?.error(CREATE_PAYMENT_ERROR, message, error?.message)
                    }
            )
        } catch (exception: Exception) {
            pendingResult?.error(QUICK_PAY_SETUP_ERROR, exception?.message, exception?.cause)
        }
    }

    private fun convertQPPaymentToMap(payment: QPPayment): Map<String, Any?> {
        return mapOf(
                "id" to payment.id,
                "order_id" to payment.order_id,
                "accepted" to payment.accepted,
                "type" to payment.type,
                "text_on_statement" to payment.text_on_statement,
                "currency" to payment.currency,
                "state" to payment.state,
                "test_mode" to payment.test_mode,
                "created_at" to payment.created_at,
                "updated_at" to payment.updated_at,
                "balance" to payment.balance,
                "branding_id" to payment.branding_id,
                "acquirer" to payment.acquirer,
                "facilitator" to payment.facilitator,
                "retented_at" to payment.retented_at,
                "fee" to payment.fee,
                "subscriptionId" to payment.subscriptionId,
                "deadline_at" to payment.deadline_at,
                "metadata" to mapOf(
                        "type" to payment.metadata?.type,
                        "origin" to payment.metadata?.origin,
                        "brand" to payment.metadata?.brand,
                        "bin" to payment.metadata?.bin,
                        "corporate" to payment.metadata?.corporate,
                        "last4" to payment.metadata?.last4,
                        "exp_month" to payment.metadata?.exp_month,
                        "exp_year" to payment.metadata?.exp_year,
                        "country" to payment.metadata?.country,
                        "is_3d_secure" to payment.metadata?.is_3d_secure,
                        "issued_to" to payment.metadata?.issued_to,
                        "hash" to payment.metadata?.hash,
                        "number" to payment.metadata?.number,
                        "customer_ip" to payment.metadata?.customer_ip,
                        "customer_country" to payment.metadata?.customer_country,
                        "shopsystem_name" to payment.metadata?.shopsystem_name,
                        "shopsystem_version" to payment.metadata?.shopsystem_version
                ),
                "operatinons" to payment.operations?.map {
                    mapOf(
                            "id" to it.id,
                            "type" to it.type,
                            "amount" to it.amount,
                            "pending" to it.pending,
                            "qp_status_code" to it.qp_status_code,
                            "qp_status_msg" to it.qp_status_msg,
                            "aq_status_msg" to it.aq_status_msg,
                            "acquirer" to it.acquirer
                    )
                }
        )
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }
}
