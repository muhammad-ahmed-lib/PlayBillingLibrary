package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

/**
 * Interface for handling the various callbacks from the billing flow.
 * Implement this interface to respond to different billing events such as
 * billing success, failure, cancellation, or other purchase outcomes.
 */
interface BillingLaunchFlowListener {

    /**
     * Called when billing initialization or purchase flow fails.
     *
     * @param error A descriptive error message.
     * @param responseCode The response code indicating the type of failure.
     */
    fun onBillingFailed(error: String, responseCode: Int)

    /**
     * Called when the billing flow is successfully initiated.
     *
     * @param status A message describing the initiation status.
     * @param billingResult Contains information about the billing flow result.
     */
    fun onBillingInitiatedSuccessfully(status: String, billingResult: BillingResult)

    /**
     * Called when the billing flow is canceled by the user.
     *
     * @param status A message describing the cancellation status.
     * @param billingResult Contains the billing flow result for cancellation.
     */
    fun onBillingCanceled(status: String, billingResult: BillingResult)

    /**
     * Called when the item being purchased is already owned by the user.
     *
     * @param status A message indicating the item is already owned.
     * @param billingResult Contains the billing flow result for this case.
     */
    fun onBillingItemAlreadyOwned(status: String, billingResult: BillingResult)

    /**
     * Called when a product is successfully purchased by the user.
     *
     * @param billingResult Contains the result of the successful purchase.
     * @param purchases A list of successfully purchased items, or null if none.
     */
    fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?)

    /**
     * Called when a product purchase is pending (e.g., awaiting user confirmation or payment).
     *
     * @param billingResult Contains the result of the pending purchase.
     * @param purchases A list of pending purchases, or null if none.
     */
    fun onProductPurchasePending(billingResult: BillingResult, purchases: List<Purchase>?)

    /**
     * Called when the product purchase status is unspecified or unclear.
     *
     * @param billingResult Contains the result of the unspecified purchase attempt.
     * @param purchases A list of purchases, or null if none.
     */
    fun onProductUnspecified(billingResult: BillingResult, purchases: List<Purchase>?)

    /**
     * Called when a product fails to be purchased due to an error.
     *
     * @param billingResult Contains the result of the failed purchase attempt.
     */
    fun onProductFailedToPurchase(billingResult: BillingResult)
}
