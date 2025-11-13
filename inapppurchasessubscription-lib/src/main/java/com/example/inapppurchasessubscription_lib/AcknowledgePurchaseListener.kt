package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.Purchase

/**
 * Interface for handling purchase acknowledgment callbacks.
 * Implement this interface to respond to the outcomes of purchase acknowledgment operations.
 */
interface AcknowledgePurchaseListener {

    /**
     * Called when a purchase is successfully acknowledged by the Google Play Billing service.
     * This indicates that the purchase has been confirmed and recorded on the server.
     *
     * @param purchase The purchase object that was successfully acknowledged.
     */
    fun onAcknowledgeSuccess(purchase: Purchase)

    /**
     * Called when an attempt to acknowledge a purchase fails.
     * This could be due to network issues, server errors, or other problems.
     *
     * @param errorCode The error code indicating the type of failure.
     * @param errorMessage A descriptive error message explaining the failure.
     */
    fun onAcknowledgeFailed(errorCode: Int, errorMessage: String)

    /**
     * Called when the purchase has already been acknowledged previously.
     * This prevents duplicate acknowledgment of the same purchase.
     *
     * @param purchase The purchase object that was already acknowledged.
     */
    fun onAlreadyAcknowledged(purchase: Purchase)
}
