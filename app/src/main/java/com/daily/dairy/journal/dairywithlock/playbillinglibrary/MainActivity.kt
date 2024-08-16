package com.daily.dairy.journal.dairywithlock.playbillinglibrary

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InAppMessageResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.daily.dairy.journal.dairywithlock.playbillinglibrary.databinding.ActivityMainBinding
import com.example.inapppurchasessubscription_lib.BillingLaunchFlowListener
import com.example.inapppurchasessubscription_lib.BillingProductDetailsListener
import com.example.inapppurchasessubscription_lib.BillingPurchaseListener
import com.example.inapppurchasessubscription_lib.BillingService
import com.example.inapppurchasessubscription_lib.BillingStateListener
import com.example.inapppurchasessubscription_lib.InAppBillingMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //Instance
    private val mBillingService by lazy {
        BillingService.getInstance(this)
    }
    private val TAG="mainInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mBillingService.initializeBilling(object :BillingStateListener {
            override fun onConnected(
                isConnected: Boolean,
                billingResult: BillingResult
            ) {
                restoreSubscription()
                getProductsDetails()
                //purchase Product
                purchaseProduct()
                //subscribe Product
                subscribeProduct()
                //enable InApp Messaging
                enableInAppMessaging()
            }

            override fun onDisconnected(isConnected: Boolean, responseCode: Int) {
                Log.d(TAG, "onDisconnected: ")
            }

        })
    }
    private fun enableInAppMessaging(){
        mBillingService.enableInAppMessaging(this,
            object :InAppBillingMessaging{
                override fun onNoActionNeeded(inAppMessageResult: InAppMessageResult) {
                    Log.d(TAG, "onNoActionNeeded: $inAppMessageResult")
                }

                override fun onSubscriptionStatusUpdated(inAppMessageResult: InAppMessageResult) {
                    Log.d(TAG, "onSubscriptionStatusUpdated: $inAppMessageResult")
                }

                override fun onFailedToReceiveMessages(error: Int) {
                    Log.d(TAG, "onFailedToReceiveMessages: $error")
                }

            })
    }
    private fun subscribeProduct(){
        //Product Purchase
        mBillingService.purchaseSubscriptionProduct(this,
            "lifeTime",object:BillingLaunchFlowListener{
                override fun onBillingFailed(error: String, responseCode: Int) {
                    Log.d(TAG, "onBillingFailed: $error")
                }

                override fun onBillingInitiatedSuccessfully(
                    status: String,
                    billingResult: BillingResult
                ) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")

                }

                override fun onBillingCanceled(status: String, billingResult: BillingResult) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
                }

                override fun onBillingItemAlreadyOwned(
                    status: String,
                    billingResult: BillingResult
                ) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
                }

                override fun onProductPurchasedSuccessfully(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductPurchasedSuccessfully: $purchases")
                }

                override fun onProductPurchasePending(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductPurchasePending: $purchases")
                }

                override fun onProductUnspecified(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductUnspecified: $purchases")
                }

                override fun onProductFailedToPurchase(billingResult: BillingResult) {
                    Log.d(TAG, "onProductFailedToPurchase: $billingResult")
                }

            })
    }
    private fun purchaseProduct(){
        //Product Purchase
        mBillingService.purchaseInAppProduct(this,
            "lifeTime",object:BillingLaunchFlowListener{
                override fun onBillingFailed(error: String, responseCode: Int) {
                    Log.d(TAG, "onBillingFailed: $error")
                }

                override fun onBillingInitiatedSuccessfully(
                    status: String,
                    billingResult: BillingResult
                ) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")

                }

                override fun onBillingCanceled(status: String, billingResult: BillingResult) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
                }

                override fun onBillingItemAlreadyOwned(
                    status: String,
                    billingResult: BillingResult
                ) {
                    Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
                }

                override fun onProductPurchasedSuccessfully(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductPurchasedSuccessfully: $purchases")
                }

                override fun onProductPurchasePending(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductPurchasePending: $purchases")
                }

                override fun onProductUnspecified(
                    billingResult: BillingResult,
                    purchases: List<Purchase>?
                ) {
                    Log.d(TAG, "onProductUnspecified: $purchases")
                }

                override fun onProductFailedToPurchase(billingResult: BillingResult) {
                    Log.d(TAG, "onProductFailedToPurchase: $billingResult")
                }

            })
    }
    private fun getProductsDetails(){
        //products
        mBillingService.getProductDetails(listOf("lifetime"),object:BillingProductDetailsListener{
            override fun onProductDetailsRetrieved(
                billingResult: BillingResult,
                productDetails: List<ProductDetails>
            ) {
                Log.d(TAG, "onProductDetailsRetrieved: $productDetails")
            }

            override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
                Log.d(TAG, "onProductDetailsRetrievalFailed: $errorMessage")
            }
        })
        //subscription
        mBillingService.getSubscriptionDetails(listOf("weekly","monthly"),object:BillingProductDetailsListener{
            override fun onProductDetailsRetrieved(
                billingResult: BillingResult,
                productDetails: List<ProductDetails>
            ) {
                Log.d(TAG, "onProductDetailsRetrieved: $productDetails")
            }

            override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
                Log.d(TAG, "onProductDetailsRetrievalFailed: $errorMessage")
            }

        })
    }
    private fun restoreSubscription(){
        mBillingService.restorePurchases(object :BillingPurchaseListener{
            override fun onRestoreBillingFinished(
                isAppPurchased: Boolean,
                productDetails: MutableList<Purchase>
            ) {
                Log.d(TAG, "onRestoreBillingFinished: $isAppPurchased")
                Log.d(TAG, "onRestoreBillingFinished: $productDetails")
            }

            override fun onRestoreBillingFailed(billingError: Int) {
                Log.d(TAG, "onRestoreBillingFailed: $billingError")
            }

        })
    }
}