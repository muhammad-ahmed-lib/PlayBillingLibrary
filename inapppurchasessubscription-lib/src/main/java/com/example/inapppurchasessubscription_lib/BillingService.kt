package com.example.inapppurchasessubscription_lib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
/**
 * @author Muhammad Ahmed
 * @version 1.0
 * @since 2024-08-15
 *
 * BillingService is a singleton class that provides an interface for handling in-app purchases and subscriptions
 * using Google Play Billing Library. This class encapsulates all necessary operations related to billing,
 * including initializing the billing client, querying product details, initiating purchase flows, and
 * restoring previous purchases. It supports both one-time in-app purchases and subscriptions.
 *
 * Features:
 * - Singleton pattern implementation for a single instance across the application.
 * - Initialization and connection management with Google Play BillingClient.
 * - Asynchronous methods for querying product details and handling purchases.
 * - Separate methods for initiating purchases and subscriptions.
 * - Callbacks for billing state changes, purchase restorations, and product details retrieval.
 *
 * Usage:
 * - Initialize billing using [initializeBilling] method.
 * - Query product details with [getOneTimeProductDetails].
 * - Restore previous purchases with [restoreInAppPurchases].
 * - Restore previous subscription with [restoreSubscription].
 * - Initiate in-app purchases using [purchaseOneTimeProduct]
 * - and subscriptions using [purchaseSubscription].
 * - in app messaging using [enableInAppMessaging].
 *
 * @param context The application context required to initialize the BillingClient.
 *
 * Interfaces:
 * - [BillingStateListener]: Listens for billing client connection state changes.
 * - [BillingProductDetailsListener]: Listens for product details retrieval results.
 * - [BillingLaunchFlowListener]: Listens for purchase flow launch results.
 * - [BillingPurchaseListener]: Listens for purchase restoration results.
 * - [InAppBillingMessaging]: Listens for subscription status restoration results.
 */


class BillingService private constructor(private val context: Context) {

    private var mProductDetailsList = mutableListOf<ProductDetails>()
    private var mSubscriptionDetails = mutableListOf<ProductDetails>()
    private var isBillingConnected = false
    private val TAG = "BillingServiceInfo"
    private var mBillingFlowListener: BillingLaunchFlowListener? = null

    private val mBillingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .enablePrepaidPlans()
                    .build()
            )
            .setListener { billingResult, purchases ->
                handlePurchases(billingResult, purchases)
            }
            .build()
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        @Volatile
        private var INSTANCE: BillingService? = null

        fun getInstance(context: Context): BillingService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun initializeBilling(listener: BillingStateListener) {
        if (isBillingConnected) {
            listener.onConnected(
                true, BillingResult.newBuilder()
                    .setResponseCode(BillingClient.BillingResponseCode.OK)
                    .build()
            )
        } else {
            mBillingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isBillingConnected = true
                        listener.onConnected(true, billingResult)
                        Log.d(TAG, "onBillingSetupFinished: Connected")
                    } else {
                        isBillingConnected = false
                        listener.onDisconnected(false, billingResult.responseCode)
                    }
                    Log.d(TAG, "onBillingSetupFinished: ${billingResult.responseCode}")
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(TAG, "onBillingServiceDisconnected: ")
                    isBillingConnected = false
                    listener.onDisconnected(
                        false,
                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
                    )
                }
            })
        }
    }

    fun restoreSubscription(listener: BillingPurchaseListener) {
        if (isBillingConnected) {
            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            mBillingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.isNotEmpty()) {
                        Log.d(TAG, "restoreSubscription: $purchases")
                        listener.onRestoreBillingFinished(true, purchases)
                    } else {
                        Log.d(TAG, "restoreSubscription: not found any purchases")
                        listener.onRestoreBillingFinished(false, mutableListOf())
                    }
                } else {
                    listener.onRestoreBillingFailed(billingResult.responseCode)
                    Log.e(TAG, "Failed to query restoreSubscription: ${billingResult.responseCode}")
                }
            }
        } else {
            listener.onRestoreBillingFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
        }
    }

    fun restoreInAppPurchases(listener: BillingPurchaseListener) {
        if (isBillingConnected) {
            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            mBillingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.isNotEmpty()) {
                        Log.d(TAG, "Restored INAPP purchases: $purchases")
                        listener.onRestoreBillingFinished(true, purchases)
                    } else {
                        Log.d(TAG, "No INAPP purchases found.")
                        listener.onRestoreBillingFinished(false, mutableListOf())
                    }
                } else {
                    Log.e(TAG, "Failed to query INAPP purchases: ${billingResult.responseCode}")
                    listener.onRestoreBillingFailed(billingResult.responseCode)
                }
            }
        } else {
            Log.e(TAG, "Billing service is disconnected.")
            listener.onRestoreBillingFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
        }
    }

    fun getOneTimeProductDetails(productId: String, listener: BillingProductDetailsListener) {
        if (isBillingConnected) {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            mBillingClient.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (prodDetailsList.isNotEmpty()) {
                        Log.d(TAG, "getProductDetails: isNotEmpty Ok $prodDetailsList")
                        mProductDetailsList.clear()
                        mProductDetailsList.addAll(prodDetailsList)
                        listener.onProductDetailsRetrieved(billingResult, prodDetailsList)
                    } else {
                        Log.d(TAG, "getProductDetails: no products found")
                        listener.onProductDetailsRetrieved(billingResult, mutableListOf())
                    }
                } else {
                    listener.onProductDetailsRetrievalFailed(billingResult.responseCode, billingResult.debugMessage)
                    Log.e(TAG, "Failed to query product details: ${billingResult.responseCode}")
                }
            }
        } else {
            listener.onProductDetailsRetrievalFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, "Service is not connected.")
        }
    }

    fun getSubscriptionDetails(subscriptionIds: List<String>, listener: BillingProductDetailsListener) {
        if (isBillingConnected) {
            val productList = subscriptionIds.map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            mBillingClient.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (prodDetailsList.isNotEmpty()) {
                        Log.d(TAG, "getSubscriptionDetails: isNotEmpty Ok $prodDetailsList")
                        mSubscriptionDetails.clear()
                        mSubscriptionDetails.addAll(prodDetailsList)
                        listener.onProductDetailsRetrieved(billingResult, prodDetailsList)
                    } else {
                        Log.d(TAG, "getSubscriptionDetails: no subscription found")
                        listener.onProductDetailsRetrieved(billingResult, mutableListOf())
                    }
                } else {
                    listener.onProductDetailsRetrievalFailed(billingResult.responseCode, billingResult.debugMessage)
                    Log.e(TAG, "Failed to query subscription details: ${billingResult.responseCode}")
                }
            }
        } else {
            listener.onProductDetailsRetrievalFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, "Service is not connected.")
        }
    }

    fun purchaseOneTimeProduct(
        activity: Activity,
        productId: String,
        listener: BillingLaunchFlowListener
    ) {
        mBillingFlowListener = listener
        val productDetails = mProductDetailsList.find { it.productId == productId }
        if (productDetails == null) {
            listener.onBillingFailed(BillingResponseCode.PRODUCT_NOT_FOUND.message, -1)
            Log.e(TAG, "No product details found for product: $productId")
            return
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        val billingResult = mBillingClient.launchBillingFlow(activity, billingFlowParams)
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Purchase initiated successfully.")
                listener.onBillingInitiatedSuccessfully("Purchase initiated successfully.", billingResult)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled the purchase.")
                listener.onBillingCanceled("User canceled the purchase.", billingResult)
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item already owned.")
                listener.onBillingItemAlreadyOwned("Item already owned.", billingResult)
            }
            else -> {
                Log.e(TAG, "Error initiating purchase: ${billingResult.debugMessage}")
                listener.onBillingFailed("Error initiating purchase: ${billingResult.debugMessage}", billingResult.responseCode)
            }
        }
    }

    fun purchaseSubscription(
        activity: Activity,
        subscriptionId: String,
        listener: BillingLaunchFlowListener
    ) {
        mBillingFlowListener = listener
        val subscriptionDetails = mSubscriptionDetails.find { it.productId == subscriptionId }
        if (subscriptionDetails == null) {
            listener.onBillingFailed(BillingResponseCode.PRODUCT_NOT_FOUND.message, -1)
            Log.e(TAG, "No subscription details found for subscription: $subscriptionId")
            return
        }
        val offerToken=subscriptionDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken==null){
            listener.onBillingFailed("No offer token is found", -1)
            Log.e(TAG, "No subscription details found for subscription: $subscriptionId")
            return
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(subscriptionDetails)
                .setOfferToken(offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        val billingResult = mBillingClient.launchBillingFlow(activity, billingFlowParams)
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Subscription purchase initiated successfully.")
                listener.onBillingInitiatedSuccessfully("Subscription purchase initiated successfully.", billingResult)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled the subscription purchase.")
                listener.onBillingCanceled("User canceled the subscription purchase.", billingResult)
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Subscription item already owned.")
                listener.onBillingItemAlreadyOwned("Subscription item already owned.", billingResult)
            }
            else -> {
                Log.e(TAG, "Error initiating subscription purchase: ${billingResult.debugMessage}")
                listener.onBillingFailed("Error initiating subscription purchase: ${billingResult.debugMessage}", billingResult.responseCode)
            }
        }
    }
    /**
     * Handles in App Messaging for purchases
     * or subscription for handling payment methode etc.
     * @param activity
     * @param listener
     */
    fun enableInAppMessaging(activity: Activity,listener:InAppBillingMessaging){
        if (isBillingConnected){
            val inAppMessageParams = InAppMessageParams.newBuilder()
                .addInAppMessageCategoryToShow(InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                .build()

            mBillingClient.showInAppMessages(activity,
                inAppMessageParams
            ) { inAppMessageResult ->
                if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                    // The flow has finished and there is no action needed from developers.
                    listener.onNoActionNeeded(inAppMessageResult)
                } else if (inAppMessageResult.responseCode
                    == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED
                ) {
                    // The subscription status changed. For example, a subscription
                    // has been recovered from a suspend state. Developers should
                    // expect the purchase token to be returned with this response
                    // code and use the purchase token with the Google Play
                    // Developer API.
                    listener.onSubscriptionStatusUpdated(inAppMessageResult)
                }
            }
        }else{
            listener.onFailedToReceiveMessages(BillingResponseCode.SERVICE_DISCONNECTED.code)
        }
    }
    private fun handlePurchases(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Handle the purchase
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Grant entitlement to the user
                    Log.d(TAG, "Purchase successful: ${purchase.orderId}")
                    mBillingFlowListener?.onProductPurchasedSuccessfully(billingResult, purchases)
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Log.d(TAG, "Purchase pending: ${purchase.orderId}")
                    mBillingFlowListener?.onProductPurchasePending(billingResult, purchases)
                } else {
                    Log.d(TAG, "Purchase unspecified state: ${purchase.orderId}")
                    mBillingFlowListener?.onProductUnspecified(billingResult, purchases)
                }
            }
        } else {
            Log.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
            mBillingFlowListener?.onProductFailedToPurchase(billingResult)
        }
    }

    fun disconnectBilling() {
        if (isBillingConnected) {
            mBillingClient.endConnection()
            isBillingConnected = false
            Log.d(TAG, "Billing service disconnected.")
        }
    }
}
