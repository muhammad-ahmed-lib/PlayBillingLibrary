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
 * - Query product details with [getProductDetails].
 * - Restore previous purchases with [restoreBillingPurchases].
 * - Initiate in-app purchases using [purchaseInAppProduct]
 * - and subscriptions using [purchaseSubscriptionProduct].
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
    private val productNames = mutableListOf<String>()
    private var isBillingConnected = false
    private var inAppBillingMessaging: InAppBillingMessaging? = null
    private var billingStateListener: BillingStateListener? = null
    private var mBillingFlowListener: BillingLaunchFlowListener? = null
    private var billingProductDetailsListener: BillingProductDetailsListener? = null
    private val TAG = "BillingServiceInfo"

    private val mBillingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().enablePrepaidPlans()
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

    /**
     * Initializes the BillingClient and establishes the connection.
     */
    fun initializeBilling(listener: BillingStateListener) {
        billingStateListener = listener
        if (isBillingConnected) {
            billingStateListener?.onConnected(
                true, BillingResult.newBuilder()
                    .setResponseCode(BillingClient.BillingResponseCode.OK)
                    .build()
            )
        } else {
            establishConnection()
        }
    }

    /**
     * Establishes connection with the BillingClient.
     */
    private fun establishConnection() {
        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingConnected = true
                    billingStateListener?.onConnected(true, billingResult)
                } else {
                    isBillingConnected = false
                    billingStateListener?.onDisconnected(false, billingResult.responseCode)
                }
                Log.d(TAG, "onBillingSetupFinished: ${billingResult.responseCode}")
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: ")
                isBillingConnected = false
                billingStateListener?.onDisconnected(
                    false,
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
                )
                establishConnection()
            }
        })
    }

    /**
     * Checks for existing purchases.
     */
    fun restoreOneTimeProduct(listener: BillingPurchaseListener) {
        if (isBillingConnected) {
            /** Query to recover INAPP
             * purchases
             */
            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            mBillingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.isNotEmpty()) {
                        Log.d(TAG, "restoreBillingPurchases: $purchases")
                        listener.onRestoreBillingFinished(true, purchases)
                    } else {
                        Log.d(TAG, "restoreBillingPurchases: not found any purchases")
                        listener.onRestoreBillingFinished(
                            false,
                            mutableListOf()
                        )
                    }
                } else {
                    listener.onRestoreBillingFailed(billingResult.responseCode)
                    Log.e(TAG, "Failed to query purchases: ${billingResult.responseCode}")
                }
            }
        } else {
            listener.onRestoreBillingFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
        }
    }

    /**
     * Checks for existing subscriptions.
     */
    fun restoreSubscription(listener: BillingPurchaseListener) {
        if (isBillingConnected) {
            /** Query to recover SUBS
             * purchases
             */
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
                        listener.onRestoreBillingFinished(
                            false,
                            mutableListOf()
                        )
                    }
                } else {
                    listener.onRestoreBillingFailed(billingResult.responseCode)
                    Log.e(TAG, "Failed to query purchases: ${billingResult.responseCode}")
                }
            }
        } else {
            listener.onRestoreBillingFailed(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
        }
    }

    /**
     * Retrieves the details of INAPP specified products.
     */
    fun getProductDetails(productIds: List<String>, listener: BillingProductDetailsListener) {
        billingProductDetailsListener = listener
        productNames.clear()
        productNames.addAll(productIds)
        getProductDetails(BillingClient.ProductType.INAPP)
    }

    /**
     * Retrieves the details of SUBS specified products.
     */
    fun getSubscriptionDetails(productIds: List<String>, listener: BillingProductDetailsListener) {
        billingProductDetailsListener = listener
        productNames.clear()
        productNames.addAll(productIds)
        getProductDetails(BillingClient.ProductType.SUBS)
    }
    /**
     * Fetches product details asynchronously from Google Play.
     */
    private fun getProductDetails(productType: String) {
        if (isBillingConnected) {
            val productList = productNames.map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

            mBillingClient.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (prodDetailsList.isNotEmpty()) {
                        mProductDetailsList.clear()
                        mProductDetailsList.addAll(prodDetailsList)
                        Log.d(TAG, "getProductDetails: isNotEmpty Ok $prodDetailsList")
                        billingProductDetailsListener?.onProductDetailsRetrieved(
                            billingResult,
                            prodDetailsList
                        )
                    } else {
                        billingProductDetailsListener?.onProductDetailsRetrieved(
                            billingResult,
                            mutableListOf()
                        )
                    }
                } else {
                    billingProductDetailsListener?.onProductDetailsRetrievalFailed(
                        billingResult.responseCode,
                        billingResult.debugMessage
                    )
                    Log.e(TAG, "Failed to query product details: ${billingResult.responseCode}")
                }
            }
        } else {
            billingProductDetailsListener?.onProductDetailsRetrievalFailed(
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, "Service is not connected."
            )
        }
    }
    
    /**
     * Initiates the purchase flow for an INAPP product.
     */
    fun purchaseInAppProduct(
        activity: Activity,
        productId: String,
        listener: BillingLaunchFlowListener?
    ) {
        mBillingFlowListener = listener
        initiatePurchaseFlow(activity, productId)
    }

    /**
     * Initiates the purchase flow for a subscription product.
     */
    fun purchaseSubscriptionProduct(
        activity: Activity,
        productId: String,
        listener: BillingLaunchFlowListener?
    ) {
        mBillingFlowListener = listener
        initiatePurchaseFlow(activity, productId)
    }

    /**
     * Handles the purchase or subscription flow.
     */
    private fun initiatePurchaseFlow(activity: Activity, productId: String) {
        val productDetails = mProductDetailsList.find { it.productId == productId }
        if (productDetails == null) {
            mBillingFlowListener?.onBillingFailed(BillingResponseCode.PRODUCT_NOT_FOUND.message, -1)
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
                mBillingFlowListener?.onBillingInitiatedSuccessfully(
                    "Purchase initiated successfully.",
                    billingResult
                )
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled the purchase.")
                mBillingFlowListener?.onBillingCanceled(
                    "User canceled the purchase.",
                    billingResult
                )
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item already owned.")
                mBillingFlowListener?.onBillingItemAlreadyOwned(
                    "Item already owned.",
                    billingResult
                )
            }

            else -> {
                Log.e(TAG, "Error initiating purchase: ${billingResult.debugMessage}")
                mBillingFlowListener?.onBillingFailed(
                    "Error initiating purchase: ${billingResult.debugMessage}",
                    billingResult.responseCode
                )
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
        inAppBillingMessaging=listener
        if (isBillingConnected){
            val inAppMessageParams = InAppMessageParams.newBuilder()
                .addInAppMessageCategoryToShow(InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                .build()

            mBillingClient.showInAppMessages(activity,
                inAppMessageParams
            ) { inAppMessageResult ->
                if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                    // The flow has finished and there is no action needed from developers.
                    inAppBillingMessaging?.onNoActionNeeded(inAppMessageResult)
                } else if (inAppMessageResult.responseCode
                    == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED
                ) {
                    // The subscription status changed. For example, a subscription
                    // has been recovered from a suspend state. Developers should
                    // expect the purchase token to be returned with this response
                    // code and use the purchase token with the Google Play
                    // Developer API.
                    inAppBillingMessaging?.onSubscriptionStatusUpdated(inAppMessageResult)
                }
            }
        }else{
            inAppBillingMessaging?.onFailedToReceiveMessages(BillingResponseCode.SERVICE_DISCONNECTED.code)
        }
    }
    /**
     * Handles purchases and subscriptions.
     */
    private fun handlePurchases(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (purchases.isNullOrEmpty()) {
                // No purchases found, notify failure
                Log.d(TAG, "No purchases found.")
                mBillingFlowListener?.onProductFailedToPurchase(billingResult)
            } else {
                // Lists to hold successful and failed purchases
                val successfulPurchases = mutableListOf<Purchase>()
                val pendingPurchases = mutableListOf<Purchase>()
                val unspecifiedPurchases = mutableListOf<Purchase>()

                for (purchase in purchases) {
                    when (purchase.purchaseState) {
                        Purchase.PurchaseState.PURCHASED -> {
                            if (!purchase.isAcknowledged) {
                                successfulPurchases.add(purchase)
                            }
                        }

                        Purchase.PurchaseState.PENDING -> pendingPurchases.add(purchase)
                        else -> unspecifiedPurchases.add(purchase)
                    }
                }

                // Notify listeners based on the purchase state
                if (successfulPurchases.isNotEmpty()) {
                    Log.d(TAG, "Successful purchases: $successfulPurchases")
                    mBillingFlowListener?.onProductPurchasedSuccessfully(billingResult,successfulPurchases)
                } else if (pendingPurchases.isNotEmpty()) {
                    Log.d(TAG, "Pending purchases: $pendingPurchases")
                    mBillingFlowListener?.onProductPurchasePending(billingResult,pendingPurchases)
                } else if (unspecifiedPurchases.isNotEmpty()) {
                    Log.d(TAG, "Unspecified purchases: $unspecifiedPurchases")
                    mBillingFlowListener?.onProductFailedToPurchase(billingResult)
                    mBillingFlowListener?.onProductUnspecified(billingResult,unspecifiedPurchases)
                }
            }
        } else {
            Log.e(TAG, "Error in onPurchasesUpdated: ${billingResult.debugMessage}")
            mBillingFlowListener?.onProductFailedToPurchase(billingResult)
        }
    }
}