package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.Purchase

/**
 * Interface for handling the restoration of purchases in the billing process.
 * Implement this interface to respond to purchase restoration events.
 */
interface BillingPurchaseListener {

    /**
     * Called when the restoration of past purchases is successfully completed.
     *
     * @param isAppPurchased A boolean indicating whether the app or a product has been purchased.
     * @param productDetails A mutable list of successfully restored purchases.
     */
    fun onRestoreBillingFinished(
        isAppPurchased: Boolean,
        productDetails: MutableList<Purchase>
    )

    /**
     * Called when the restoration of purchases fails.
     *
     * @param billingError An integer representing the error code for the failure.
     */
    fun onRestoreBillingFailed(billingError: Int)
}
