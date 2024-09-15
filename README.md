Play Billing Library

The Play Billing Library is an Android library designed to simplify the management of in-app purchases and subscriptions in your Android application. 
This library includes robust features like purchase handling, subscription management, in-app messaging, and more.

Note: This is a beta version of the Play Billing Library. We appreciate any feedback you have. Please send your feedback to ahmed03160636141@gmail.com.

Features
In-App Purchases: Handle one-time in-app purchases seamlessly.
Subscriptions: Manage recurring subscriptions with ease.
In-App Messaging: Enable and manage in-app messaging for billing-related notifications.
Restore Purchases: Restore previous purchases and subscriptions.
Billing Flow: Simplified and managed billing flow with detailed callbacks.
Getting Started
1. Add the Dependency
Add the following to your settings.gradle file:

Copy code

dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    
}

Then, add the dependency to your build.gradle (Module: app) file:

Copy code

dependencies {

Latest version <1.0.6>

    implementation 'com.github.muhammad-ahmed-lib:PlayBillingLibrary:1.0.6'
    
}

2. Initialize the Library
In your Application class or the main Activity, initialize the BillingService:

kotlin
Copy code

import com.daily.dairy.journal.dairywithlock.playbillinglibrary.BillingService
import com.daily.dairy.journal.dairywithlock.playbillinglibrary.BillingStateListener
import com.android.billingclient.api.BillingResult

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize BillingService
        val billingService = BillingService.getInstance(this)
        billingService.initializeBilling(object : BillingStateListener {
            override fun onConnected(isConnected: Boolean, billingResult: BillingResult) {
                // Handle billing connected state
            }

            override fun onDisconnected(isConnected: Boolean, responseCode: Int) {
                // Handle billing disconnected state
            }
        })
    }
}

3. Retrieve Product Details
To retrieve details for both in-app purchases and subscriptions:

kotlin
Copy code

private fun getProductsDetails() {
    // Retrieve in-app product details
    billingService.getProductDetails(listOf("lifetime"), object : BillingProductDetailsListener {
        override fun onProductDetailsRetrieved(billingResult: BillingResult, productDetails: List<ProductDetails>) {
            Log.d(TAG, "onProductDetailsRetrieved: $productDetails")
        }

        override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
            Log.d(TAG, "onProductDetailsRetrievalFailed: $errorMessage")
        }
    })

    // Retrieve subscription product details
    billingService.getSubscriptionDetails(listOf("weekly", "monthly"), object : BillingProductDetailsListener {
        override fun onProductDetailsRetrieved(billingResult: BillingResult, productDetails: List<ProductDetails>) {
            Log.d(TAG, "onProductDetailsRetrieved: $productDetails")
        }

        override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
            Log.d(TAG, "onProductDetailsRetrievalFailed: $errorMessage")
        }
    })
}

4. Launch the Billing Flow
To initiate the billing flow for both in-app purchases and subscriptions:

kotlin
Copy code

private fun purchaseProduct() {
    billingService.purchaseInAppProduct(this, "lifetime", object : BillingLaunchFlowListener {
        override fun onBillingFailed(error: String, responseCode: Int) {
            Log.d(TAG, "onBillingFailed: $error")
        }

        override fun onBillingInitiatedSuccessfully(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
        }

        override fun onBillingCanceled(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingCanceled: $status")
        }

        override fun onBillingItemAlreadyOwned(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingItemAlreadyOwned: $status")
        }

        override fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductPurchasedSuccessfully: $purchases")
        }

        override fun onProductPurchasePending(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductPurchasePending: $purchases")
        }

        override fun onProductUnspecified(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductUnspecified: $purchases")
        }

        override fun onProductFailedToPurchase(billingResult: BillingResult) {
            Log.d(TAG, "onProductFailedToPurchase: $billingResult")
        }
    })
}

private fun subscribeProduct() {
    billingService.purchaseSubscriptionProduct(this, "weekly", object : BillingLaunchFlowListener {
        override fun onBillingFailed(error: String, responseCode: Int) {
            Log.d(TAG, "onBillingFailed: $error")
        }

        override fun onBillingInitiatedSuccessfully(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingInitiatedSuccessfully: $status")
        }

        override fun onBillingCanceled(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingCanceled: $status")
        }

        override fun onBillingItemAlreadyOwned(status: String, billingResult: BillingResult) {
            Log.d(TAG, "onBillingItemAlreadyOwned: $status")
        }

        override fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductPurchasedSuccessfully: $purchases")
        }

        override fun onProductPurchasePending(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductPurchasePending: $purchases")
        }

        override fun onProductUnspecified(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "onProductUnspecified: $purchases")
        }

        override fun onProductFailedToPurchase(billingResult: BillingResult) {
            Log.d(TAG, "onProductFailedToPurchase: $billingResult")
        }
    })
}

5. Restore Purchases
To restore previous purchases:

Copy code

mBillingService.restoreSubscription(object :BillingPurchaseListener{
          
           override fun onRestoreBillingFinished(
               isAppPurchased: Boolean,
               productDetails: MutableList<Purchase>
           ) {
               
           }

           override fun onRestoreBillingFailed(billingError: Int) {

           }

       })
       
        mBillingService.restoreOneTimeProduct(object :BillingPurchaseListener{
            override fun onRestoreBillingFinished(
                isAppPurchased: Boolean,
                productDetails: MutableList<Purchase>
            ) {

            }

            override fun onRestoreBillingFailed(billingError: Int) {

            }

        })

6. Enable In-App Messaging
To enable in-app messaging related to billing:

kotlin
Copy code

private fun enableInAppMessaging() {
    billingService.enableInAppMessaging(this, object : InAppBillingMessaging {
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

Feedback
As this is a beta version, we welcome your feedback to help us improve the library. Please send your feedback to ahmed03160636141@gmail.com.

License
This project is licensed under the MIT License - see the LICENSE file for details.
