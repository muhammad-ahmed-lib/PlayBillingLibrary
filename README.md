Play Billing Library Integration

Version: 1.0.7


This library simplifies the process of integrating Google Play Billing into your Android applications, supporting both in-app purchases and subscriptions.

Features

In-App Purchases (One-Time Products): Easily handle the purchase of consumable and non-consumable products.

Subscription Management: Manage subscriptions with flexible billing periods (weekly, monthly, yearly).

Product Restoration: Restore previously purchased products and subscriptions.

In-App Messaging: Enable in-app messaging for subscription status updates and other in-app billing interactions.

Installation

Add the following dependency to your project-level build.gradle:

gradle

Copy code

	maven { url 'https://jitpack.io' }
implementation 'com.daily.dairy.journal.dairywithlock.playbillinglibrary:1.0.7'

Setup

Initialize Billing Service

In your MainActivity, initialize the BillingService in the onCreate method:

Copy code

private val mBillingService by lazy {
  
    BillingService.getInstance(this)
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Initialize the Billing Service
    mBillingService.initializeBilling(object : BillingStateListener {
        override fun onConnected(isConnected: Boolean, billingResult: BillingResult) {
            // Call methods once billing is connected
            restoreSubscription()
            getProductsDetails()
            purchaseProduct()
            subscribeProduct()
            enableInAppMessaging()
        }

        override fun onDisconnected(isConnected: Boolean, responseCode: Int) {
            Log.d(TAG, "onDisconnected: $responseCode")
        }
    })
}

Retrieve Product Details

Fetch the details of one-time products and subscription plans:

Copy code

private fun getProductsDetails() {
   
    mBillingService.getOneTimeProductDetails("lifetime", object : BillingProductDetailsListener {
      
        override fun onProductDetailsRetrieved(billingResult: BillingResult, productDetails: List<ProductDetails>) {
            Log.d(TAG, "One-time Product Details: $productDetails")
        }

        override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
            Log.e(TAG, "Failed to retrieve product details: $errorMessage")
        }
    })

    mBillingService.getSubscriptionDetails(listOf("weekly", "monthly"), object : BillingProductDetailsListener {
      
        override fun onProductDetailsRetrieved(billingResult: BillingResult, productDetails: List<ProductDetails>) {
            Log.d(TAG, "Subscription Details: $productDetails")
        }

        override fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String) {
            Log.e(TAG, "Failed to retrieve subscription details: $errorMessage")
        }
    })
}

Handle Purchases

Purchase a one-time product or a subscription:

Copy code
// Purchase a one-time product

private fun purchaseProduct() {
   
    mBillingService.purchaseOneTimeProduct(this, "lifeTime", object : BillingLaunchFlowListener {
        override fun onBillingFailed(error: String, responseCode: Int) {
            Log.e(TAG, "Purchase failed: $error")
        }

        override fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "Product purchased: $purchases")
        }
    })
}

// Purchase a subscription
private fun subscribeProduct() {
   
   mBillingService.purchaseSubscription(this, "monthly", object : BillingLaunchFlowListener {
      
        override fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?) {
            Log.d(TAG, "Subscription purchased: $purchases")
        }
    })
}
Restore Purchases

Restore previous in-app purchases or subscriptions:


Copy code

private fun restoreSubscription() {
  
    mBillingService.restoreSubscription(object : BillingPurchaseListener {
     
        override fun onRestoreBillingFinished(isAppPurchased: Boolean, productDetails: MutableList<Purchase>) {
            Log.d(TAG, "Restored subscription: $productDetails")
        }

        override fun onRestoreBillingFailed(billingError: Int) {
            Log.e(TAG, "Failed to restore subscriptions")
        }
    })
}

Enable In-App Messaging

Activate in-app messaging for subscription updates:


Copy code
private fun enableInAppMessaging() {
  
    mBillingService.enableInAppMessaging(this, object : InAppBillingMessaging {
     
        override fun onSubscriptionStatusUpdated(inAppMessageResult: InAppMessageResult) {
            Log.d(TAG, "Subscription status updated: $inAppMessageResult")
        }
    })
}

License

This library is available for use under the MIT License.

For feedback, issues, or feature requests, feel free to contact:

Muhammad Ahmed

Email: ahmed03160636141@gmail.com

WhatsApp: +923091370220
